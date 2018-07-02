package vanetsim.scenario;

public class LaneContainer {

    protected final boolean direction_;

    protected LaneObject head_;

    protected LaneObject tail_;

    protected int size_ = 0;

    public LaneContainer(boolean direction) {
        head_ = null;
        tail_ = null;
        direction_ = direction;
    }

    public synchronized void addSorted(LaneObject object) {
        if (size_ == 0 || head_ == null) {
            head_ = tail_ = object;
        } else {
            if ((direction_ && object.getCurPosition() < head_.getCurPosition()) || (!direction_ && object.getCurPosition() > head_.getCurPosition())) {
                object.setNext(head_);
                head_.setPrevious(object);
                head_ = object;
            } else {
                LaneObject tmpObject = head_;
                boolean isLargest = true;
                if (direction_) {
                    for (int i = 1; i < size_; ++i) {
                        tmpObject = tmpObject.getNext();
                        if (tmpObject.getCurPosition() > object.getCurPosition()) {
                            isLargest = false;
                            break;
                        }
                    }
                } else {
                    for (int i = 1; i < size_; ++i) {
                        tmpObject = tmpObject.getNext();
                        if (tmpObject.getCurPosition() < object.getCurPosition()) {
                            isLargest = false;
                            break;
                        }
                    }
                }
                if (isLargest) {
                    object.setNext(null);
                    object.setPrevious(tail_);
                    tail_.setNext(object);
                    tail_ = object;
                } else {
                    object.setNext(tmpObject);
                    object.setPrevious(tmpObject.getPrevious());
                    tmpObject.getPrevious().setNext(object);
                    tmpObject.setPrevious(object);
                }
            }
        }
        ++size_;
    }

    public synchronized void updatePosition(LaneObject object, double newPosition) {
        object.curPosition_ = newPosition;
        if (size_ > 1) {
            LaneObject nextObject = object.getNext();
            LaneObject prevObject = object.getPrevious();
            int needsupdate = 0;
            if (direction_) {
                if (nextObject != null && nextObject.getCurPosition() < object.getCurPosition())
                    needsupdate = 1;
                else if (prevObject != null && prevObject.getCurPosition() > object.getCurPosition())
                    needsupdate = 2;
            } else {
                if (nextObject != null && nextObject.getCurPosition() > object.getCurPosition())
                    needsupdate = 1;
                else if (prevObject != null && prevObject.getCurPosition() < object.getCurPosition())
                    needsupdate = 2;
            }
            if (needsupdate > 0) {
                if (needsupdate == 1) {
                    if (direction_) {
                        while (nextObject != null) {
                            if (nextObject.getCurPosition() > object.getCurPosition())
                                break;
                            nextObject = nextObject.getNext();
                        }
                    } else {
                        while (nextObject != null) {
                            if (nextObject.getCurPosition() < object.getCurPosition())
                                break;
                            nextObject = nextObject.getNext();
                        }
                    }
                    if (object == head_) {
                        head_ = object.getNext();
                        object.getNext().setPrevious(null);
                    } else {
                        object.getPrevious().setNext(object.getNext());
                        object.getNext().setPrevious(object.getPrevious());
                    }
                    if (nextObject == null) {
                        object.setNext(null);
                        object.setPrevious(tail_);
                        tail_.setNext(object);
                        tail_ = object;
                    } else {
                        object.setNext(nextObject);
                        object.setPrevious(nextObject.getPrevious());
                        nextObject.getPrevious().setNext(object);
                        nextObject.setPrevious(object);
                    }
                } else {
                    if (direction_) {
                        while (prevObject != null) {
                            if (prevObject.getCurPosition() < object.getCurPosition())
                                break;
                            prevObject = prevObject.getPrevious();
                        }
                    } else {
                        while (prevObject != null) {
                            if (prevObject.getCurPosition() > object.getCurPosition())
                                break;
                            prevObject = prevObject.getPrevious();
                        }
                    }
                    if (object == tail_) {
                        tail_ = object.getPrevious();
                        object.getPrevious().setNext(null);
                    } else {
                        object.getPrevious().setNext(object.getNext());
                        object.getNext().setPrevious(object.getPrevious());
                    }
                    if (prevObject == null) {
                        object.setNext(head_);
                        object.setPrevious(null);
                        head_.setPrevious(object);
                        head_ = object;
                    } else {
                        object.setPrevious(prevObject);
                        object.setNext(prevObject.getNext());
                        prevObject.getNext().setPrevious(object);
                        prevObject.setNext(object);
                    }
                }
            }
        }
    }

    public synchronized void remove(LaneObject object) {
        LaneObject prev = object.getPrevious();
        LaneObject next = object.getNext();
        if (next == null) {
            if (prev == null) {
                head_ = null;
                tail_ = null;
            } else {
                prev.setNext(null);
                tail_ = prev;
                object.setPrevious(null);
            }
        } else if (prev == null) {
            next.setPrevious(null);
            head_ = next;
            object.setNext(null);
        } else {
            prev.setNext(next);
            next.setPrevious(prev);
            object.setNext(null);
            object.setPrevious(null);
        }
        --size_;
    }

    public LaneObject getHead() {
        return head_;
    }

    public LaneObject getTail() {
        return tail_;
    }

    public int size() {
        return size_;
    }

    public void clear() {
        head_ = null;
        tail_ = null;
        size_ = 0;
    }
}
