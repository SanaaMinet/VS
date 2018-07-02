package vanetsim.routing.A_Star;

import java.util.Arrays;

public final class A_Star_Queue {

    private A_Star_Node[] queue_;

    private int size_ = 0;

    public A_Star_Queue() {
        queue_ = new A_Star_Node[100];
    }

    public void add(A_Star_Node node) {
        int i = size_;
        if (i >= queue_.length) {
            if (i + 1 < 0)
                throw new OutOfMemoryError();
            int oldCapacity = queue_.length;
            int newCapacity = ((oldCapacity < 64) ? ((oldCapacity + 1) * 2) : ((oldCapacity / 2) * 3));
            if (newCapacity < 0)
                newCapacity = Integer.MAX_VALUE;
            if (newCapacity < i + 1)
                newCapacity = i + 1;
            queue_ = Arrays.copyOf(queue_, newCapacity);
        }
        size_ = i + 1;
        if (i == 0)
            queue_[0] = node;
        else {
            A_Star_Node e;
            int parent;
            while (i > 0) {
                parent = (i - 1) >>> 1;
                e = queue_[parent];
                if (node.getF() >= e.getF())
                    break;
                queue_[i] = e;
                i = parent;
            }
            queue_[i] = node;
        }
    }

    public void signalDecreasedF(A_Star_Node node) {
        for (int i = 0; i < size_; ++i) {
            if (node == queue_[i]) {
                A_Star_Node e;
                int parent;
                while (i > 0) {
                    parent = (i - 1) >>> 1;
                    e = queue_[parent];
                    if (node.getF() >= e.getF())
                        break;
                    queue_[i] = e;
                    i = parent;
                }
                queue_[i] = node;
                break;
            }
        }
    }

    public boolean isEmpty() {
        return (size_ == 0 ? true : false);
    }

    public A_Star_Node poll() {
        if (size_ == 0)
            return null;
        int s = --size_;
        A_Star_Node result = queue_[0];
        A_Star_Node node = queue_[s];
        queue_[s] = null;
        if (s != 0) {
            int pos = 0;
            int half = size_ >>> 1;
            A_Star_Node c;
            int child;
            while (pos < half) {
                child = (pos << 1) + 1;
                c = queue_[child];
                int right = child + 1;
                if (right < size_ && c.getF() > queue_[right].getF())
                    c = queue_[child = right];
                if (node.getF() <= c.getF())
                    break;
                queue_[pos] = c;
                pos = child;
            }
            queue_[pos] = node;
        }
        return result;
    }

    public void remove(A_Star_Node node) {
        for (int i = 0; i < size_; ++i) {
            if (node == queue_[i]) {
                int s = --size_;
                if (s == i)
                    queue_[i] = null;
                else {
                    A_Star_Node moved = queue_[s];
                    queue_[s] = null;
                    siftDown(i, moved);
                    if (queue_[i] == moved)
                        siftUp(i, moved);
                }
            }
        }
    }

    private void siftUp(int pos, A_Star_Node node) {
        A_Star_Node e;
        int parent;
        while (pos > 0) {
            parent = (pos - 1) >>> 1;
            e = queue_[parent];
            if (node.getF() >= e.getF())
                break;
            queue_[pos] = e;
            pos = parent;
        }
        queue_[pos] = node;
    }

    private void siftDown(int pos, A_Star_Node node) {
        int half = size_ >>> 1;
        A_Star_Node c;
        int child;
        while (pos < half) {
            child = (pos << 1) + 1;
            c = queue_[child];
            int right = child + 1;
            if (right < size_ && c.getF() > queue_[right].getF())
                c = queue_[child = right];
            if (node.getF() <= c.getF())
                break;
            queue_[pos] = c;
            pos = child;
        }
        queue_[pos] = node;
    }
}
