package main;

import type.ExecuteResult;
import type.MetaCommandResult;
import type.PrepareResult;
import type.StatementType;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.FileHandler;

public class MiniSql {
    public static void print_prompt() {
        System.out.print("db >");
    }

    static MetaCommandResult do_meta_command(String input_buffer) {
        if (input_buffer.equals(".exit")) {
            return MetaCommandResult.META_COMMAND_EXIT;
        } else {
            return MetaCommandResult.META_COMMAND_UNRECOGNIZED_COMMAND;
        }
    }

    static PrepareResult prepare_statement(String input_buffer, Statement statement) {
        try {
            if (input_buffer.substring(0, 6).equals("insert")) {
                return prepareInsert(input_buffer, statement);
            }
            if (input_buffer.substring(0, 6).equals("select")) {
                statement.type = StatementType.STATEMENT_SELECT;
                return PrepareResult.PREPARE_SUCCESS;
            }
        } catch (StringIndexOutOfBoundsException e) {
            return PrepareResult.PREPARE_UNRECOGNIZED_STATEMENT;
        }
        return PrepareResult.PREPARE_UNRECOGNIZED_STATEMENT;
    }

    static PrepareResult prepareInsert(String input_buffer, Statement statement) {
        statement.type = StatementType.STATEMENT_INSERT;
        Scanner scanner = new Scanner(input_buffer);
        if (!scanner.next().equals("insert")) return PrepareResult.PREPARE_UNRECOGNIZED_STATEMENT;
        try {
            int id = scanner.nextInt();
            String username = scanner.next();
            String email = scanner.next();
            if (id < 0) return PrepareResult.PREPARE_NEGATIVE_ID;
            if (username.length() > Row.COLUMN_USERNAME_SIZE || email.length() > Row.COLUMN_EMAIL_SIZE)
                return PrepareResult.PREPARE_STRING_TOO_LONG;
            else {
                statement.row_to_insert.id = id;
                statement.row_to_insert.username = username;
                statement.row_to_insert.email = email;
                return PrepareResult.PREPARE_SUCCESS;
            }
        } catch (NoSuchElementException e) {
            return PrepareResult.PREPARE_SYNTAX_ERROR;
        }

    }

    static ExecuteResult execute_statement(Statement statement, Table table) {
        switch (statement.type) {
            case STATEMENT_INSERT: {
                return execute_insert(statement, table);
            }
            case STATEMENT_SELECT: {
                return execute_select(statement, table);
            }
            default:
                break;
        }
        return null;
    }

    static ExecuteResult execute_insert(Statement statement, Table table) {
        if (table.full()) {
            return ExecuteResult.EXECUTE_TABLE_FULL;
        }
        serialize_row(statement.row_to_insert, table, table.num_rows);
        table.num_rows += 1;
        return ExecuteResult.EXECUTE_SUCCESS;
    }

    static ExecuteResult execute_select(Statement statement, Table table) {
        Row row = new Row();
        for (int i = 0; i < table.num_rows; i++) {
            deserialize_row(table, i, row);
            System.out.println(row.toString());
        }
        return ExecuteResult.EXECUTE_SUCCESS;
    }

    /**
     * 返回表中第i行数据的引用
     */
    static Row row_slot(Table table, int i) {
        int page_num = i / Page.ROWS_PER_PAGE;
        Page page = get_page(table.pager, page_num);
        return page.rows[i % Page.ROWS_PER_PAGE];
    }

    /**
     * 仿序列化
     */
    static void serialize_row(Row source, Table table, int i) {
        Row row = row_slot(table, i);
        row.id = source.id;
        row.username = source.username;
        row.email = source.email;
    }

    /**
     * 仿反序列化
     */
    static void deserialize_row(Table table, int i, Row destination) {
        Row row = row_slot(table, i);
        destination.id = row.id;
        destination.username = row.username;
        destination.email = row.email;
    }

    /**
     * 从磁盘上获取Pager
     */
    static Pager pager_open(String filename) {
        Pager pager = new Pager();
        pager.file = new File(filename);
        pager.file_length = pager.file.length();
        pager.pages = new Page[Table.TABLE_MAX_PAGES];
        return pager;
    }

    /**
     *
     */
    static Page get_page(Pager pager, int page_num) {
        if (page_num > Table.TABLE_MAX_PAGES) {
            System.out.println("Tried to fetch page number out of bounds. " + Table.TABLE_MAX_PAGES);
            return null;
        }
        if (pager.pages[page_num] == null) {
            Page page = new Page();
            //已经存储的page数量
            int num_pages = (int) (pager.file_length / Page.PAGE_SIZE);
            if (pager.file_length % Page.PAGE_SIZE != 0) num_pages += 1;
            if (page_num <= num_pages) {
                /***
                 * 文件读过程
                 */
                try {
                    RandomAccessFile file = new RandomAccessFile(pager.file, "rw");
                    byte[] bytes = new byte[Page.PAGE_SIZE];
                    file.read(bytes, page_num * Page.PAGE_SIZE, Page.PAGE_SIZE);
                    page.setBytes(bytes, 0, bytes.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            pager.pages[page_num] = page;
        }
        return pager.pages[page_num];
    }

    private static Table db_open(String filename) {
        Pager pager = pager_open(filename);
        int num_rows = (int) (pager.file_length / Row.ROW_SIZE);
        Table table = new Table(num_rows, pager);
        return table;
    }

    /***/
    static void db_close(Table table) {
        Pager pager = table.pager;
        /**
         * 先将行满的页冲刷
         * */
        int num_full_pages = table.num_rows / Page.ROWS_PER_PAGE;
        for (int i = 0; i < num_full_pages; i++) {
            if (pager.pages[i] == null) continue;
            pager_flush(pager, i, Page.PAGE_SIZE);
            pager.pages[i] = null;
        }
        /**
         * 最后一页可能行未满
         * */
        int num_additional_rows = table.num_rows % Page.ROWS_PER_PAGE;
        if (num_additional_rows > 0) {
            int page_num = num_full_pages;
            if (pager.pages[page_num] != null) {
                pager_flush(pager, page_num, num_additional_rows);
                pager.pages[page_num] = null;
            }
        }
        System.gc();
    }

    /**
     * 将Pager转化为字节数组冲刷到磁盘上
     */
    static void pager_flush(Pager pager, int page_num, int size) {
        if (pager.pages[page_num] == null) {
            System.out.println("Tried to flush null page.");
            return;
        }
        try {
            RandomAccessFile file = new RandomAccessFile(pager.file, "rw");
            file.write(pager.pages[page_num].getBytes(), page_num * Page.PAGE_SIZE, size);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Flush fail.");
        }

    }


    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Must supply a database filename.");
            return;
        }
        String filename = args[0];
        Table table = db_open(filename);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            print_prompt();
            String input_buffer = scanner.nextLine();
            if (input_buffer.matches("^\\..*")) {
                switch (do_meta_command(input_buffer)) {
                    case META_COMMAND_SUCCESS:
                        break;
                    case META_COMMAND_UNRECOGNIZED_COMMAND: {
                        System.out.println("Unrecognized command " + input_buffer);
                        continue;
                    }
                    case META_COMMAND_EXIT:{
                        db_close(table);
                        return;
                    }

                    default:
                        continue;
                }
            }
            Statement statement = new Statement();
            switch (prepare_statement(input_buffer, statement)) {
                case PREPARE_SUCCESS: {
                    break;
                }
                case PREPARE_SYNTAX_ERROR: {
                    System.out.println("Syntax error. Could not parse statement.");
                    continue;
                }
                case PREPARE_UNRECOGNIZED_STATEMENT: {
                    System.out.println("Unrecognized keyword at start of " + input_buffer);
                    continue;
                }
                case PREPARE_NEGATIVE_ID: {
                    System.out.println("ID must be positive.");
                    continue;
                }
                case PREPARE_STRING_TOO_LONG: {
                    System.out.println("String is too long.");
                    continue;
                }
                default:
                    continue;
            }
            switch (execute_statement(statement, table)) {
                case EXECUTE_SUCCESS: {
                    System.out.println("Executed.");
                    break;
                }
                case EXECUTE_TABLE_FULL: {
                    System.out.println("Error: main.Table full.");
                    break;
                }
                default:
                    break;
            }
            Deque<Integer> deque;
        }
    }


}
