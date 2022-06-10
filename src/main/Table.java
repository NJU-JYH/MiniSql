package main;

import type.NodeType;

public class Table<T> {
    final static int TABLE_MAX_PAGES = 100;
    int root_page_num;
    Pager pager;

    public Table(Pager pager) {
        this.pager = pager;
    }

    /**
     * Return the position of the given key.
     * If the key is not present, return the position
     * where it should be inserted
     * */
    Cursor find(int key){
        Page root_node = pager.get_page(root_page_num);
        if(root_node.nodeType == NodeType.NODE_LEAF){
            return leaf_node_find(root_page_num, key);
        }else{
            System.out.println("Need to searching an internal node");
            return null;
        }
    }

    Cursor leaf_node_find(int page_num, int key){
        Page node = pager.get_page(page_num);
        Cursor cursor = new Cursor();
        cursor.table = this;
        cursor.page_num = page_num;
        // Binary search
        int left = 0;
        int right = node.leaf_node_num_cells;
        while(left < right){
            int index = (left + right) / 2;
            if(node.cells[index].key < key){
                left = index + 1;
            }else if(node.cells[index].key > key){
                right = index - 1;
            }else {
                cursor.cell_num = index;
                return cursor;
            }
        }

        cursor.cell_num = left;
        return cursor;
    }
}
