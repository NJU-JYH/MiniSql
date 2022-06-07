package main;

public class Table<T> {
    final static int TABLE_MAX_PAGES = 100;
    final int TABLE_MAX_ROW = Page.ROWS_PER_PAGE * TABLE_MAX_PAGES;
    int num_rows;

    Pager pager;

    public Table(int num_rows, Pager pager) {
        this.num_rows = num_rows;
        this.pager = pager;
    }

    boolean full(){
        return num_rows >= TABLE_MAX_ROW;
    }
}
