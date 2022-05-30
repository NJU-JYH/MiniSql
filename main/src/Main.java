import java.util.Scanner;

public class Main {
    static void print_prompt(){
        System.out.print("db >");
    }

    enum MetaCommandResult{
        META_COMMAND_SUCCESS,
        META_COMMAND_UNRECOGNIZED_COMMAND,
        META_COMMAND_EXIT
    }

    enum PrepareResult{
        PREPARE_SUCCESS, PREPARE_UNRECOGNIZED_STATEMENT
    }

    static MetaCommandResult do_meta_command(String input_buffer) {
        if (input_buffer.equals(".exit")) {
            return MetaCommandResult.META_COMMAND_EXIT;
        } else {
            return MetaCommandResult.META_COMMAND_UNRECOGNIZED_COMMAND;
        }
    }

    static PrepareResult prepare_statement(String input_buffer,Statement statement){
        try {
            if(input_buffer.substring(0,6).equals("insert")){
                statement.type = Statement.StatementType.STATEMENT_INSERT;
                return PrepareResult.PREPARE_SUCCESS;
            }
            if(input_buffer.substring(0,6).equals("select")){
                statement.type = Statement.StatementType.STATEMENT_SELECT;
                return PrepareResult.PREPARE_SUCCESS;
            }
        }catch (StringIndexOutOfBoundsException e){
            return PrepareResult.PREPARE_UNRECOGNIZED_STATEMENT;
        }
        return PrepareResult.PREPARE_UNRECOGNIZED_STATEMENT;
    }

    static void execute_statement(Statement statement){
        switch (statement.type){
            case STATEMENT_INSERT :{
                System.out.println("This is where we would do an insert.");
                break;
            }
            case STATEMENT_SELECT :{
                System.out.println("This is where we would do a select.");
                break;
            }
            default:break;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while(true){
            print_prompt();
            String input_buffer = scanner.nextLine();
            if(input_buffer.matches("^\\..*")){
                switch (do_meta_command(input_buffer)){
                    case META_COMMAND_SUCCESS:break;
                    case META_COMMAND_UNRECOGNIZED_COMMAND:{
                        System.out.println("Unrecognized command " + input_buffer);
                        break;
                    }
                    case META_COMMAND_EXIT:return;
                    default:break;
                }
            }
            Statement statement = new Statement();
            switch (prepare_statement(input_buffer, statement)){
                case PREPARE_SUCCESS:{
                    break;
                }
                case PREPARE_UNRECOGNIZED_STATEMENT:{
                    System.out.println("Unrecognized keyword at start of " + input_buffer);
                    continue;
                }
                default:break;
            }
            execute_statement(statement);
            System.out.println("Executed.");
        }
    }
}
