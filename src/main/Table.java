package main;

public class Table<T> {
    final static int TABLE_MAX_PAGES = 100;
//    final int TABLE_MAX_ROW = Page.ROWS_PER_PAGE * TABLE_MAX_PAGES;
//    int num_rows;

    int root_page_num;
    Pager pager;

    public Table(Pager pager) {
        this.pager = pager;
    }
}
