package main;

import type.ExecuteResult;
import type.MetaCommandResult;
import type.PrepareResult;
import type.StatementType;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MiniSql {
    public static void print_prompt() {
        System.out.print("db > ");
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
        serialize_row(statement.row_to_insert, table_end(table).value());
//        pager_flush(table.pager, table.num_rows / Page.ROWS_PER_PAGE, table.num_rows % Page.ROWS_PER_PAGE + 1);
        table.num_rows += 1;
        table.pager.file_length += Row.ROW_SIZE;
        return ExecuteResult.EXECUTE_SUCCESS;
    }

    static ExecuteResult execute_select(Statement statement, Table table) {
        Cursor cursor = table_start(table);
        Row row = new Row();
        while(!cursor.end_of_table){
            deserialize_row(cursor.value(), row);
            System.out.println(row.toString());
            cursor.advance();
        }
        return ExecuteResult.EXECUTE_SUCCESS;
    }

    /**
     * 返回表中第i行数据的引用
     */


    /**
     * 仿序列化
     */
    static void serialize_row(Row source, Row destination) {
        destination.id = source.id;
        destination.username = source.username;
        destination.email = source.email;
    }

    /**
     * 仿反序列化
     */
    static void deserialize_row(Row source, Row destination) {
        destination.id = source.id;
        destination.username = source.username;
        destination.email = source.email;
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



    private static Table db_open(String filename) {
        Pager pager = pager_open(filename);
        int num_rows = (int) (pager.file_length / Page.PAGE_SIZE * Page.ROWS_PER_PAGE);
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
            pager.flush(i, Page.PAGE_SIZE);
            pager.pages[i] = null;
        }
        /**
         * 最后一页可能行未满
         * */
        int num_additional_rows = table.num_rows % Page.ROWS_PER_PAGE;
        if (num_additional_rows > 0) {
            int page_num = num_full_pages;
            if (pager.pages[page_num] != null) {
                pager.flush(page_num, num_additional_rows * Row.ROW_SIZE);
                pager.pages[page_num] = null;
            }
        }
    }



    static Cursor table_start(Table table){
        Cursor cursor = new Cursor(table);
        cursor.end_of_table = (table.num_rows == 0);
        return cursor;
    }

    static Cursor table_end(Table table){
        Cursor cursor = new Cursor(table);
        cursor.end_of_table = true;
        return cursor;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Must supply a database filename.");
            return;
        }
        String filename = args[0];
        Table table = db_open(filename);
        Scanner scanner = new Scanner(System.in);
        int test_num = 0;
        while (test_num >= 0) {
            print_prompt();
            String input_buffer = null;
            if(test_num > 0){
                input_buffer = "insert " + test_num + " username example@qq.com";
                test_num--;
            }else{
                input_buffer = scanner.nextLine();
            }

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
                    System.out.println("Error: Table full.");
                    break;
                }
                default:
                    break;
            }
        }
    }


}
