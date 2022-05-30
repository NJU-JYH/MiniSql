public class Page {
    final static int PAGE_SIZE = 4096;
    final static int ROWS_PER_PAGE = PAGE_SIZE / Row.ROW_SIZE;
    Row[] rows = new Row[ROWS_PER_PAGE];
}
