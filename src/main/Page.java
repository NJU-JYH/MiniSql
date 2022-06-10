package main;

import org.w3c.dom.Node;
import type.NodeType;

import java.nio.MappedByteBuffer;

public class Page {
    public final static int PAGE_SIZE = 4096;

    /**
     * 普通节点头部
     * */

    public final static int NODE_TYPE_SIZE = 1;
    public final static int NODE_TYPE_OFFSET = 0;
    public final static int IS_ROOT_SIZE = 1;
    public final static int IS_ROOT_OFFSET = NODE_TYPE_SIZE;
    public final static int PARENT_POINTER_SIZE = 4;
    public final static int PARENT_POINTER_OFFSET = IS_ROOT_OFFSET + IS_ROOT_SIZE;
    public final static int COMMON_NODE_HEADER_SIZE =
            NODE_TYPE_SIZE + IS_ROOT_SIZE + PARENT_POINTER_SIZE;
    /**
     * 叶子节点头部
     * */
    public final static int LEAF_NODE_NUM_CELLS_SIZE = 4;
    public final static int LEAF_NODE_NUM_CELLS_OFFSET = COMMON_NODE_HEADER_SIZE;
    public final static int LEAF_NODE_HEADER_SIZE =
            COMMON_NODE_HEADER_SIZE + LEAF_NODE_NUM_CELLS_SIZE;
    /**
     * 叶子节点体
     * */
    public final static int LEAF_NODE_KEY_SIZE = 4;
    public final static int LEAF_NODE_KEY_OFFSET = 0;
    public final static int LEAF_NODE_VALUE_SIZE = Row.ROW_SIZE;
    public final static int LEAF_NODE_VALUE_OFFSET =
            LEAF_NODE_KEY_OFFSET + LEAF_NODE_KEY_SIZE;
    public final static int LEAF_NODE_CELL_SIZE = LEAF_NODE_KEY_SIZE + LEAF_NODE_VALUE_SIZE;
    public final static int LEAF_NODE_SPACE_FOR_CELLS = Page.PAGE_SIZE - LEAF_NODE_HEADER_SIZE;
    public final static int LEAF_NODE_MAX_CELLS =
            LEAF_NODE_SPACE_FOR_CELLS / LEAF_NODE_CELL_SIZE;
    Cell[] cells = new Cell[LEAF_NODE_MAX_CELLS];
    int leaf_node_num_cells;
    public NodeType nodeType;

    public Page(MappedByteBuffer buffer) {
        nodeType = NodeType.NODE_LEAF;
        buffer2Page(buffer);
    }

    void buffer2Page(MappedByteBuffer buffer) {
        for (int i = 0; i < LEAF_NODE_MAX_CELLS; i++) {
            Row row = new Row(buffer, i * Row.ROW_SIZE, Row.ROW_SIZE);
            if(!row.isNull()){
                cells[i] = new Cell(row.id, row);
                leaf_node_num_cells++;
            }
        }
    }

    void page2Buffer(MappedByteBuffer buffer) {
        for (int i = 0; i < leaf_node_num_cells; i++) {
            cells[i].value.row2Buffer(buffer);
        }
    }
}
