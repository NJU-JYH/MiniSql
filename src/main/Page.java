package main;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class Page {
    final static int PAGE_SIZE = 4096;
    final static int ROWS_PER_PAGE = PAGE_SIZE / Row.ROW_SIZE;
    Row[] rows = new Row[ROWS_PER_PAGE];
    void setBytes(byte[] bytes, int begin, int end){
        for(int i = 0;i<ROWS_PER_PAGE;i++){
            rows[i] = new Row();
            rows[i].setBytes(bytes, begin + i * Row.ROW_SIZE, begin + (i+1) * Row.ROW_SIZE);
        }
    }
    byte[] getBytes(){
        byte[] bytes = new byte[PAGE_SIZE];
        int i = 0;
        for(Row row:rows){
            for(Byte b:row.getBytes()){
                bytes[i++] = b;
            }
        }
        return bytes;
    }
}
