package vanetsim.routing.A_Star;

import java.util.ArrayDeque;
import vanetsim.map.Node;

public final class A_Star_LookupTableFactory {

    private static final ArrayDeque<A_Star_LookupTable<Node, A_Star_Node>> table_ = new ArrayDeque<A_Star_LookupTable<Node, A_Star_Node>>();

    private static final ArrayDeque<Integer> counter_ = new ArrayDeque<Integer>();

    public static synchronized A_Star_LookupTable<Node, A_Star_Node> getTable(int[] counter) {
        if (table_.isEmpty()) {
            counter[0] = Integer.MIN_VALUE;
            return new A_Star_LookupTable<Node, A_Star_Node>(Node.getMaxNodeID() + 1);
        } else {
            counter[0] = counter_.poll() + 1;
            if (counter[0] == Integer.MAX_VALUE) {
                table_.poll();
                counter[0] = Integer.MIN_VALUE;
                return new A_Star_LookupTable<Node, A_Star_Node>(Node.getMaxNodeID() + 1);
            } else
                return table_.poll();
        }
    }

    public static synchronized void putTable(int counter, A_Star_LookupTable<Node, A_Star_Node> table) {
        table_.add(table);
        counter_.add(counter);
    }

    public static synchronized void clear() {
        table_.clear();
        counter_.clear();
    }
}
