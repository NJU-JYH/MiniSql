package main;

import java.nio.MappedByteBuffer;

public class Page {
    final static int PAGE_SIZE = 4096;
    final static int ROWS_PER_PAGE = PAGE_SIZE / Row.ROW_SIZE;

    Row[] rows = new Row[ROWS_PER_PAGE];

    Page(MappedByteBuffer buffer) {
        buffer2Page(buffer);
    }



    void buffer2Page(MappedByteBuffer buffer) {
        for (int i = 0; i < ROWS_PER_PAGE; i++) {
            rows[i] = new Row(buffer, i * Row.ROW_SIZE, Row.ROW_SIZE);
        }
    }

    void page2Buffer(MappedByteBuffer buffer,int size) {
        for (int i = 0; i < size / Row.ROW_SIZE; i++) {
            rows[i].row2Buffer(buffer);
        }
    }
}
