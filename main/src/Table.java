public class Table {
    final int TABLE_MAX_PAGES = 100;
    final int TABLE_MAX_ROW = Page.ROWS_PER_PAGE * TABLE_MAX_PAGES;
    int num_rows;

    Page[] pages = new Page[TABLE_MAX_PAGES];

    boolean full(){
        return num_rows >= TABLE_MAX_ROW;
    }
}
