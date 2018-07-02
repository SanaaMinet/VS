package vanetsim.scenario;

import vanetsim.gui.helpers.GeneralLogWriter;

public class KnownEventSourcesList {

    private static final int HASH_SIZE = 16;

    private static int timePassed_ = 0;

    private KnownEventSource[] head_ = new KnownEventSource[HASH_SIZE];

    private int size_ = 0;

    private int updatedSources = 0;

    private int createdSources = 0;

    private int timeBetweenUpdates = 0;

    private int spamCount = 0;

    public KnownEventSourcesList(long ID) {
        for (int i = 0; i < HASH_SIZE; ++i) {
            head_[i] = null;
        }
    }

    public synchronized void update(Vehicle vehicle, long ID, int x, int y, double speed, boolean isFake) {
        boolean found = false;
        int hash = (int) (ID % HASH_SIZE);
        if (hash < 0)
            hash = -hash;
        KnownEventSource next = head_[hash];
        while (next != null) {
            if (next.getID() == ID) {
                updatedSources++;
                next.setX(x);
                next.setY(y);
                timeBetweenUpdates = timeBetweenUpdates + (timePassed_ - next.getLastUpdate());
                next.setLastUpdate(timePassed_);
                next.setSpeed(speed);
                next.setUpdates_(next.getUpdates_() + 1);
                if (isFake)
                    next.setFakeMessageCounter_(next.getFakeMessageCounter_() + 1);
                else
                    next.setRealMessageCounter_(next.getRealMessageCounter_() + 1);
                found = true;
                break;
            }
            next = next.getNext();
        }
        if (!found) {
            createdSources++;
            next = new KnownEventSource(vehicle, ID, x, y, speed, timePassed_, isFake);
            next.setNext(head_[hash]);
            next.setPrevious(null);
            if (head_[hash] != null)
                head_[hash].setPrevious(next);
            head_[hash] = next;
            ++size_;
        }
    }

    public KnownEventSource[] getFirstKnownEventSource() {
        return head_;
    }

    public int getSize() {
        return size_;
    }

    public static void setTimePassed(int time) {
        timePassed_ = time;
    }

    public int getUpdatedSources() {
        return updatedSources;
    }

    public void setUpdatedSources(int updatedSources) {
        this.updatedSources = updatedSources;
    }

    public int getCreatedSources() {
        return createdSources;
    }

    public void setCreatedSources(int createdSources) {
        this.createdSources = createdSources;
    }

    public void clear() {
        writeSpam();
        head_ = new KnownEventSource[HASH_SIZE];
        size_ = 0;
    }

    public void writeOutputFile() {
        String output = "";
        KnownEventSource next;
        for (int i = 0; i < HASH_SIZE; ++i) {
            next = head_[i];
            while (next != null) {
                output += next.getUpdates_() + "#";
                next.setUpdates_(0);
                next = next.getNext();
            }
        }
        if (output.length() > 0)
            GeneralLogWriter.log("***:" + output.subSequence(0, (output.length() - 1)));
    }

    public void writeSpam() {
        KnownEventSource next;
        for (int i = 0; i < HASH_SIZE; ++i) {
            next = head_[i];
            while (next != null) {
                spamCount += next.getSpamCounter_();
                next.setSpamCounter_(0);
                next = next.getNext();
            }
        }
    }

    public int getTimeBetweenUpdates() {
        return timeBetweenUpdates;
    }

    public void setTimeBetweenUpdates(int timeBetweenUpdates) {
        this.timeBetweenUpdates = timeBetweenUpdates;
    }

    public int getSpamCount() {
        return spamCount;
    }

    public void setSpamCount(int spamCount) {
        this.spamCount = spamCount;
    }
}
