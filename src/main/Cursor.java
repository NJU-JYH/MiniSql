package main;

public class Cursor {
    Table table;
    int row_num;
    boolean end_of_table;

    public Cursor(Table table) {
        this.table = table;
    }

    void advance(){
        row_num += 1;
        end_of_table = (row_num >= table.num_rows);
    }

    Row value() {
        int row_num = this.row_num;
        int page_num = row_num / Page.ROWS_PER_PAGE;
        Page page = table.pager.get_page(page_num);
        return page.rows[row_num % Page.ROWS_PER_PAGE];
    }
}
