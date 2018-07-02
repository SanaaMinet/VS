package vanetsim.routing.A_Star;

import vanetsim.map.Node;

public class A_Star_LookupTable<K, V> {

    private A_Star_Node[] table_;

    private int size_;

    public A_Star_LookupTable(int initialCapacity) {
        if (initialCapacity < 0)
            initialCapacity = 1000;
        table_ = new A_Star_Node[initialCapacity];
        size_ = initialCapacity;
    }

    public A_Star_Node get(Node key) {
        int pos = key.getNodeID();
        if (pos > size_)
            return null;
        else
            return table_[key.getNodeID()];
    }

    public void put(Node key, A_Star_Node value) {
        int pos = key.getNodeID();
        if (pos > size_) {
            A_Star_Node[] newTable = new A_Star_Node[pos + 1];
            System.arraycopy(table_, 0, newTable, 0, size_);
            table_ = newTable;
            size_ = pos + 1;
        }
        table_[pos] = value;
    }
}
