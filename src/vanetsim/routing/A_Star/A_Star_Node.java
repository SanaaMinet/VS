package vanetsim.routing.A_Star;

import vanetsim.ErrorLog;
import vanetsim.localization.Messages;
import vanetsim.map.Node;

public final class A_Star_Node implements Comparable<Object> {

    private final Node realNode_;

    private boolean inOpenList_ = false;

    private boolean inClosedList_ = false;

    private double f_;

    private double g_;

    private A_Star_Node predecessor_ = null;

    private int counter_ = 0;

    public A_Star_Node(Node realNode, double f, int counter) {
        counter_ = counter;
        realNode_ = realNode;
        f_ = f;
    }

    public A_Star_Node(Node realNode, int counter) {
        counter_ = counter;
        realNode_ = realNode;
        f_ = 0;
    }

    public void reset(int counter) {
        counter_ = counter;
        f_ = 0;
        inOpenList_ = false;
        inClosedList_ = false;
    }

    public int getCounter() {
        return counter_;
    }

    public boolean isInClosedList() {
        return inClosedList_;
    }

    public void setInClosedList(boolean state) {
        inClosedList_ = state;
    }

    public boolean isInOpenList() {
        return inOpenList_;
    }

    public void setInOpenList(boolean state) {
        inOpenList_ = state;
    }

    public double getF() {
        return f_;
    }

    public void setF(double f) {
        f_ = f;
    }

    public double getG() {
        return g_;
    }

    public void setG(double g) {
        g_ = g;
    }

    public A_Star_Node getPredecessor() {
        return predecessor_;
    }

    public void setPredecessor(A_Star_Node predecessor) {
        predecessor_ = predecessor;
    }

    public Node getRealNode() {
        return realNode_;
    }

    public int compareTo(Object other) {
        if (this == other)
            return 0;
        A_Star_Node otherNode = (A_Star_Node) other;
        if (f_ > otherNode.getF())
            return 1;
        else if (f_ < otherNode.getF())
            return -1;
        else {
            if (this.hashCode() < other.hashCode())
                return -1;
            else if (this.hashCode() > other.hashCode())
                return 1;
            else {
                if (realNode_.getX() > otherNode.getRealNode().getX())
                    return -1;
                else if (realNode_.getX() < otherNode.getRealNode().getX())
                    return 1;
                else {
                    if (realNode_.getY() > otherNode.getRealNode().getY())
                        return -1;
                    else if (realNode_.getY() < otherNode.getRealNode().getY())
                        return 1;
                    else {
                        ErrorLog.log(Messages.getString("A_Star_Node.NodeCompareError"), 7, A_Star_Node.class.getName(), "compareTo", null);
                        return 0;
                    }
                }
            }
        }
    }
}
