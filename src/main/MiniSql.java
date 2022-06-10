package main;

import type.ExecuteResult;
import type.MetaCommandResult;
import type.PrepareResult;
import type.StatementType;

import java.io.*;
import java.nio.MappedByteBuffer;
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
        Page page = table.pager.get_page(table.root_page_num);
        if(page.leaf_node_num_cells >= Page.LEAF_NODE_MAX_CELLS) return ExecuteResult.EXECUTE_TABLE_FULL;
        Cursor cursor = table.find(statement.row_to_insert.id);
        if(cursor.cell_num < page.leaf_node_num_cells && statement.row_to_insert.id == page.cells[cursor.cell_num].key){
            return ExecuteResult.EXECUTE_DUPLICATE_KEY;
        }
        cursor.leaf_node_insert(statement.row_to_insert.id, statement.row_to_insert);
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
        pager.pages = new Page[Table.TABLE_MAX_PAGES];
        pager.num_pages = (int) (pager.file.length() / Page.PAGE_SIZE);
        return pager;
    }



    private static Table db_open(String filename) {
        Pager pager = pager_open(filename);
        Table table = new Table(pager);
        return table;
    }

    static void db_close(Table table) {
        Pager pager = table.pager;
        for(int i = 0; i < Page.LEAF_NODE_MAX_CELLS;i++){
            if(pager.pages[i] != null){
                pager.flush(i);
            }
        }
    }



    static Cursor table_start(Table table){
        Cursor cursor = new Cursor(table);
        cursor.page_num = table.root_page_num;
        Page root_node = table.pager.get_page(table.root_page_num);
        int num_cells = root_node.leaf_node_num_cells;
        cursor.end_of_table = (num_cells == 0);
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
        int test_num = 13;
        while (true) {
            print_prompt();
            String input_buffer;
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
                case EXECUTE_DUPLICATE_KEY:{
                    System.out.println("Error: Duplicate key.");
                    break;
                }
                default:
                    break;
            }
        }
    }
}
