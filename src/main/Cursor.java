package main;

public class Cursor {
    Table table;

    boolean end_of_table;
//    int row_num;
    int page_num;
    int cell_num;

    public Cursor(Table table) {
        this.table = table;
    }



    Row value() {
//        int page_num = row_num / Page.ROWS_PER_PAGE;
        Page page = table.pager.get_page(page_num);
//        return page.cells[row_num % Page.ROWS_PER_PAGE].value;
        return page.cells[cell_num].value;
    }

    void advance(){
//        row_num += 1;
//        end_of_table = (row_num >= table.num_rows);
        Page page = table.pager.get_page(page_num);
        cell_num++;
        if(cell_num >= page.leaf_node_num_cells) end_of_table = true;
    }

    void leaf_node_insert(int key, Row value){
        Page node = table.pager.get_page(page_num);
        if(node.leaf_node_num_cells >= Page.LEAF_NODE_MAX_CELLS){
            System.out.println("Need to implement splitting a leaf node.");
        }
//        if(cell_num < node.leaf_node_num_cells){
//            if (node.leaf_node_num_cells - cell_num >= 0)
//                System.arraycopy(node.cells, cell_num, node.cells, cell_num + 1, node.leaf_node_num_cells - cell_num);
//        }
        node.leaf_node_num_cells += 1;
        node.cells[cell_num] = new Cell(key, value);
    }
}
