package test.main; 

import main.Page;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/** 
* MiniSql Tester. 
* 
* @author <Authors name> 
* @since <pre>6ÔÂ 1, 2022</pre> 
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
* Method: prepareInsert(String input_buffer, Statement statement) 
* 
*/ 
@Test
public void testPrepareInsert() throws Exception { 
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
* Method: pager_open(String filename) 
* 
*/ 
@Test
public void testPager_open() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: get_page(Pager pager, int page_num) 
* 
*/ 
@Test
public void testGet_page() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: db_close(Table table) 
* 
*/ 
@Test
public void testDb_close() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: pager_flush(Pager pager, int page_num, int size) 
* 
*/ 
@Test
public void testPager_flush() throws Exception { 
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
    MappedByteBuffer buffer = new RandomAccessFile("test.txt","rw").getChannel().map(FileChannel.MapMode.READ_WRITE,0,16);
    byte[] bytes = new byte[16];
    for(int i = 0;i<16;i++) bytes[i] = 1;
    buffer.put(new byte[16]);
} 


/** 
* 
* Method: db_open(String filename) 
* 
*/ 
@Test
public void testDb_open() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = MiniSql.getClass().getMethod("db_open", String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 
