package main;

import type.ExecuteResult;
import type.MetaCommandResult;
import type.PrepareResult;
import type.StatementType;
import java.util.NoSuchElementException;
import java.util.Scanner;

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
        }catch (StringIndexOutOfBoundsException e){
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
            if(id < 0) return PrepareResult.PREPARE_NEGATIVE_ID;
            if(username.length() > Row.COLUMN_USERNAME_SIZE || email.length() > Row.COLUMN_EMAIL_SIZE)
                return PrepareResult.PREPARE_STRING_TOO_LONG;
            else{
                statement.row_to_insert.id = id;
                statement.row_to_insert.username = username;
                statement.row_to_insert.email = email;
                return PrepareResult.PREPARE_SUCCESS;
            }
        } catch (NoSuchElementException e){
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
        Page page = table.pages[page_num];
        if (page == null) {
            page = new Page();
            table.pages[page_num] = page;
        }
        Row row = page.rows[i % Page.ROWS_PER_PAGE];
        if (row == null) {
            row = new Row();
            page.rows[i % Page.ROWS_PER_PAGE] = row;
        }
        return row;
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

    public static void main(String[] args) {
        Table table = new Table();
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
                    case META_COMMAND_EXIT:
                        return;
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
                case PREPARE_NEGATIVE_ID:{
                    System.out.println("ID must be positive.");
                    continue;
                }
                case PREPARE_STRING_TOO_LONG:{
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
        }
    }
}
