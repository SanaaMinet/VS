package vanetsim.scenario;

import vanetsim.gui.Renderer;
import vanetsim.scenario.messages.Message;

public class KnownMessages {

    private static final int MAX_FORWARD_TIME = 2500;

    private static final int MAX_OLD_TIME = 5000;

    private static final Renderer renderer_ = Renderer.getInstance();

    private final Vehicle vehicle_;

    private Message[] executeMessages_;

    private int executeMessageSize_ = 0;

    private Message[] unprocessedMessages_;

    private int unprocessedMessageSize = 0;

    private Message[] forwardMessages_;

    private int forwardMessageSize_ = 0;

    private int[] forwardArrivalTime_;

    private Message[] oldMessages_;

    private int oldMessageSize_ = 0;

    private int[] oldMessageArrivalTime_;

    private int failedToForwardCount_ = 0;

    private boolean hasNewMessages_ = false;

    public KnownMessages(Vehicle vehicle) {
        vehicle_ = vehicle;
        executeMessages_ = new Message[2];
        unprocessedMessages_ = new Message[2];
        forwardMessages_ = new Message[2];
        forwardArrivalTime_ = new int[2];
        oldMessages_ = new Message[2];
        oldMessageArrivalTime_ = new int[2];
    }

    public KnownMessages() {
        vehicle_ = null;
        executeMessages_ = new Message[2];
        unprocessedMessages_ = new Message[2];
        forwardMessages_ = new Message[2];
        forwardArrivalTime_ = new int[2];
        oldMessages_ = new Message[2];
        oldMessageArrivalTime_ = new int[2];
    }

    public synchronized void addMessage(Message message, boolean doExecute, boolean doForward) {
        boolean foundExecute = false;
        boolean foundForward = false;
        int i;
        for (i = 0; i < oldMessageSize_; ++i) {
            if (oldMessages_[i] == message) {
                foundExecute = true;
                foundForward = true;
                break;
            }
        }
        if (doExecute && !foundExecute) {
            for (i = 0; i < executeMessageSize_; ++i) {
                if (executeMessages_[i] == message) {
                    foundExecute = true;
                    break;
                }
            }
        }
        if (doForward && !foundForward) {
            for (i = 0; i < forwardMessageSize_; ++i) {
                if (forwardMessages_[i] == message) {
                    foundForward = true;
                    break;
                }
            }
        }
        if (doForward && !foundForward) {
            for (i = 0; i < unprocessedMessageSize; ++i) {
                if (unprocessedMessages_[i] == message) {
                    foundForward = true;
                    break;
                }
            }
        }
        if (doExecute && !foundExecute) {
            hasNewMessages_ = true;
            if (executeMessageSize_ < executeMessages_.length) {
                executeMessages_[executeMessageSize_] = message;
            } else {
                Message[] newArray = new Message[executeMessageSize_ + 2];
                System.arraycopy(executeMessages_, 0, newArray, 0, executeMessageSize_);
                newArray[executeMessageSize_] = message;
                executeMessages_ = newArray;
            }
            ++executeMessageSize_;
        }
        if (doForward && !foundForward) {
            hasNewMessages_ = true;
            if (unprocessedMessageSize < unprocessedMessages_.length) {
                unprocessedMessages_[unprocessedMessageSize] = message;
            } else {
                Message[] newArray = new Message[unprocessedMessageSize + 2];
                System.arraycopy(unprocessedMessages_, 0, newArray, 0, unprocessedMessageSize);
                newArray[unprocessedMessageSize] = message;
                unprocessedMessages_ = newArray;
            }
            ++unprocessedMessageSize;
        }
    }

    public synchronized void deleteForwardMessage(int position, boolean addToOld) {
        if (position > -1 && position < forwardMessageSize_) {
            if (addToOld) {
                if (oldMessageSize_ + 1 > oldMessages_.length) {
                    Message[] newArray = new Message[oldMessageSize_ + 3];
                    if (oldMessageSize_ > 0)
                        System.arraycopy(oldMessages_, 0, newArray, 0, oldMessageSize_);
                    oldMessages_ = newArray;
                    int[] newArray2 = new int[oldMessageSize_ + 3];
                    if (oldMessageSize_ > 0)
                        System.arraycopy(oldMessageArrivalTime_, 0, newArray2, 0, oldMessageSize_);
                    oldMessageArrivalTime_ = newArray2;
                }
                oldMessages_[oldMessageSize_] = forwardMessages_[position];
                oldMessageArrivalTime_[oldMessageSize_] = renderer_.getTimePassed();
                ++oldMessageSize_;
            }
            --forwardMessageSize_;
            System.arraycopy(forwardMessages_, position + 1, forwardMessages_, position, forwardMessageSize_ - position);
            System.arraycopy(forwardArrivalTime_, position + 1, forwardArrivalTime_, position, forwardMessageSize_ - position);
        }
    }

    public synchronized void deleteAllForwardMessages(boolean addToOld) {
        if (addToOld) {
            if (oldMessageSize_ + forwardMessageSize_ > oldMessages_.length) {
                Message[] newArray = new Message[oldMessageSize_ + 3];
                if (oldMessageSize_ > 0)
                    System.arraycopy(oldMessages_, 0, newArray, 0, oldMessageSize_);
                oldMessages_ = newArray;
                int[] newArray2 = new int[oldMessageSize_ + 3];
                if (oldMessageSize_ > 0)
                    System.arraycopy(oldMessageArrivalTime_, 0, newArray2, 0, oldMessageSize_);
                oldMessageArrivalTime_ = newArray2;
            }
            System.arraycopy(forwardMessages_, 0, oldMessages_, oldMessageSize_, forwardMessageSize_);
            int time = renderer_.getTimePassed();
            for (int i = oldMessageSize_ + forwardMessageSize_ - 1; i >= oldMessageSize_; --i) {
                oldMessageArrivalTime_[i] = time;
            }
            oldMessageSize_ += forwardMessageSize_;
        }
        forwardMessageSize_ = 0;
    }

    public synchronized void deleteAllFloodingForwardMessages(boolean addToOld) {
        Message[] newArray;
        int[] newArray2;
        int time = renderer_.getTimePassed();
        for (int i = forwardMessageSize_ - 1; i > -1; --i) {
            if (forwardMessages_[i].getFloodingMode()) {
                if (addToOld) {
                    if (oldMessageSize_ + 1 > oldMessages_.length) {
                        newArray = new Message[oldMessageSize_ + 3];
                        if (oldMessageSize_ > 0)
                            System.arraycopy(oldMessages_, 0, newArray, 0, oldMessageSize_);
                        oldMessages_ = newArray;
                        newArray2 = new int[oldMessageSize_ + 3];
                        if (oldMessageSize_ > 0)
                            System.arraycopy(oldMessageArrivalTime_, 0, newArray2, 0, oldMessageSize_);
                        oldMessageArrivalTime_ = newArray2;
                    }
                    oldMessages_[oldMessageSize_] = forwardMessages_[i];
                    oldMessageArrivalTime_[oldMessageSize_] = time;
                    ++oldMessageSize_;
                }
                --forwardMessageSize_;
                System.arraycopy(forwardMessages_, i + 1, forwardMessages_, i, forwardMessageSize_ - i);
                System.arraycopy(forwardArrivalTime_, i + 1, forwardArrivalTime_, i, forwardMessageSize_ - i);
            }
        }
    }

    public void processMessages() {
        if (executeMessageSize_ > 0) {
            for (int i = 0; i < executeMessageSize_; ++i) {
                if (vehicle_ != null)
                    executeMessages_[i].execute(vehicle_);
            }
            executeMessageSize_ = 0;
        }
        if (unprocessedMessageSize > 0) {
            if (unprocessedMessageSize + forwardMessageSize_ > forwardMessages_.length) {
                Message[] newArray = new Message[unprocessedMessageSize + forwardMessageSize_ + 2];
                if (forwardMessageSize_ > 0)
                    System.arraycopy(forwardMessages_, 0, newArray, 0, forwardMessageSize_);
                forwardMessages_ = newArray;
                int[] newArray2 = new int[unprocessedMessageSize + forwardMessageSize_ + 2];
                if (forwardMessageSize_ > 0)
                    System.arraycopy(forwardArrivalTime_, 0, newArray2, 0, forwardMessageSize_);
                forwardArrivalTime_ = newArray2;
            }
            System.arraycopy(unprocessedMessages_, 0, forwardMessages_, forwardMessageSize_, unprocessedMessageSize);
            int time = renderer_.getTimePassed();
            for (int i = forwardMessageSize_ + unprocessedMessageSize - 1; i >= forwardMessageSize_; --i) {
                forwardArrivalTime_[i] = time;
            }
            forwardMessageSize_ += unprocessedMessageSize;
            unprocessedMessageSize = 0;
        }
        hasNewMessages_ = false;
    }

    public void checkOutdatedMessages(boolean addToOld) {
        int timeout = renderer_.getTimePassed() - MAX_FORWARD_TIME;
        for (int i = forwardMessageSize_ - 1; i > -1; --i) {
            if (forwardArrivalTime_[i] < timeout || !forwardMessages_[i].isValid()) {
                if (addToOld) {
                    if (oldMessageSize_ + 1 > oldMessages_.length) {
                        Message[] newArray = new Message[oldMessageSize_ + 3];
                        if (oldMessageSize_ > 0)
                            System.arraycopy(oldMessages_, 0, newArray, 0, oldMessageSize_);
                        oldMessages_ = newArray;
                        int[] newArray2 = new int[oldMessageSize_ + 3];
                        if (oldMessageSize_ > 0)
                            System.arraycopy(oldMessageArrivalTime_, 0, newArray2, 0, oldMessageSize_);
                        oldMessageArrivalTime_ = newArray2;
                    }
                    oldMessages_[oldMessageSize_] = forwardMessages_[i];
                    oldMessageArrivalTime_[oldMessageSize_] = renderer_.getTimePassed();
                    ++oldMessageSize_;
                }
                --forwardMessageSize_;
                System.arraycopy(forwardMessages_, i + 1, forwardMessages_, i, forwardMessageSize_ - i);
                System.arraycopy(forwardArrivalTime_, i + 1, forwardArrivalTime_, i, forwardMessageSize_ - i);
                ++failedToForwardCount_;
            }
        }
        timeout = renderer_.getTimePassed() - MAX_OLD_TIME;
        for (int i = oldMessageSize_ - 1; i > -1; --i) {
            if (oldMessageArrivalTime_[i] < timeout || !oldMessages_[i].isValid()) {
                --oldMessageSize_;
                System.arraycopy(oldMessages_, i + 1, oldMessages_, i, oldMessageSize_ - i);
                System.arraycopy(oldMessageArrivalTime_, i + 1, oldMessageArrivalTime_, i, oldMessageSize_ - i);
            }
        }
    }

    public Message[] getForwardMessages() {
        return forwardMessages_;
    }

    public boolean hasNewMessages() {
        return hasNewMessages_;
    }

    public int getSize() {
        return forwardMessageSize_;
    }

    public int getOldMessagesSize() {
        return oldMessageSize_;
    }

    public int getFailedForwardCount() {
        return failedToForwardCount_;
    }

    public void clear() {
        executeMessages_ = new Message[2];
        executeMessageSize_ = 0;
        unprocessedMessages_ = new Message[2];
        unprocessedMessageSize = 0;
        forwardMessages_ = new Message[2];
        forwardMessageSize_ = 0;
        forwardArrivalTime_ = new int[2];
        oldMessages_ = new Message[2];
        oldMessageSize_ = 0;
        oldMessageArrivalTime_ = new int[2];
        failedToForwardCount_ = 0;
        hasNewMessages_ = false;
    }
}
