package test.main; 

import main.MiniSql;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After; 

/** 
* MiniSql Tester. 
* 
* @author <Authors name> 
* @since <pre>5ÔÂ 31, 2022</pre> 
* @version 1.0 
*/ 
public class MiniSqlTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: print_prompt() 
* 
*/ 
@Test
public void testPrint_prompt() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: do_meta_command(String input_buffer) 
* 
*/ 
@Test
public void testDo_meta_command() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: prepare_statement(String input_buffer, Statement statement) 
* 
*/ 
@Test
public void testPrepare_statement() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: execute_statement(Statement statement, Table table) 
* 
*/ 
@Test
public void testExecute_statement() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: execute_insert(Statement statement, Table table) 
* 
*/ 
@Test
public void testExecute_insert() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: execute_select(Statement statement, Table table) 
* 
*/ 
@Test
public void testExecute_select() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: row_slot(Table table, int i) 
* 
*/ 
@Test
public void testRow_slot() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: serialize_row(Row source, Table table, int i) 
* 
*/ 
@Test
public void testSerialize_row() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: deserialize_row(Table table, int i, Row destination) 
* 
*/ 
@Test
public void testDeserialize_row() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: main(String[] args) 
* 
*/ 
@Test
public void testMain() throws Exception { 
//TODO: Test goes here...
    MiniSql.main(null);
} 


} 
