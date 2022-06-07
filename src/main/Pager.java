package main;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Pager {
    File file;
    long file_length;
    Page[] pages;

    /**
     * 以页为单位加载到内存
     */
    Page get_page(int page_num) {
        if (page_num >= Table.TABLE_MAX_PAGES) {
            System.out.println("Tried to fetch page number out of bounds. " + Table.TABLE_MAX_PAGES);
            return null;
        }
        if (pages[page_num] == null) {
            Page page = null;
            //已经存储的page数量
            int num_pages = (int) (file_length / Page.PAGE_SIZE);
            if (file_length % Page.PAGE_SIZE != 0) num_pages += 1;
            if (page_num <= num_pages) {
                /***
                 * 内存映射该页
                 */
                try {
                    RandomAccessFile file = new RandomAccessFile(this.file, "rw");
                    MappedByteBuffer buffer = file.getChannel().map(FileChannel.MapMode.READ_WRITE, page_num * Page.PAGE_SIZE, Page.PAGE_SIZE);
                    page = new Page(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            pages[page_num] = page;
        }
        return pages[page_num];
    }

    /**
     * 将Pager转化为字节数组冲刷到磁盘上
     */
    void flush(int page_num, int size) {
        if (pages[page_num] == null) {
            System.out.println("Tried to flush null page.");
            return;
        }
        try {
            RandomAccessFile file = new RandomAccessFile(this.file, "rw");
            MappedByteBuffer buffer = file.getChannel().map(FileChannel.MapMode.READ_WRITE, page_num * Page.PAGE_SIZE, Page.PAGE_SIZE);
            pages[page_num].page2Buffer(buffer, size);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Flush fail.");
        }

    }
}
