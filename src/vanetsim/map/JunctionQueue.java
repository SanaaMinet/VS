package vanetsim.map;

import vanetsim.gui.Renderer;
import vanetsim.scenario.Vehicle;

public final class JunctionQueue {

    private static final int LAST_SEEN_TIMEOUT = 2500;

    private static final Renderer renderer_ = Renderer.getInstance();

    private Vehicle[] vehicles_;

    private int[] waitingSince_;

    private int[] lastSeen_;

    private int size_ = 0;

    public JunctionQueue() {
        vehicles_ = new Vehicle[2];
        waitingSince_ = new int[2];
        lastSeen_ = new int[2];
    }

    public synchronized boolean addVehicle(Vehicle vehicle) {
        int i;
        for (i = 0; i < size_; ++i) {
            if (vehicles_[i] == vehicle) {
                lastSeen_[i] = renderer_.getTimePassed();
                return false;
            }
        }
        if (size_ >= vehicles_.length) {
            Vehicle[] newArray = new Vehicle[size_ + 2];
            System.arraycopy(vehicles_, 0, newArray, 0, size_);
            vehicles_ = newArray;
            int[] newArray2 = new int[size_ + 2];
            System.arraycopy(waitingSince_, 0, newArray2, 0, size_);
            waitingSince_ = newArray2;
            newArray2 = new int[size_ + 2];
            System.arraycopy(lastSeen_, 0, newArray2, 0, size_);
            lastSeen_ = newArray2;
        }
        int curTime = renderer_.getTimePassed();
        for (i = size_ - 1; i > -1; --i) {
            if (waitingSince_[i] != curTime)
                break;
            else if (vehicles_[i].getX() > vehicle.getX())
                break;
            else if (vehicles_[i].getX() == vehicle.getX()) {
                if (vehicles_[i].getY() > vehicle.getY())
                    break;
                else if (vehicles_[i].getY() > vehicle.getY()) {
                    if (vehicles_[i].hashCode() > vehicle.hashCode())
                        break;
                }
            }
        }
        ++size_;
        ++i;
        vehicles_[i] = vehicle;
        waitingSince_[i] = curTime;
        lastSeen_[i] = curTime;
        return true;
    }

    public synchronized boolean delVehicle(Vehicle vehicle) {
        for (int i = 0; i < size_; ++i) {
            if (vehicles_[i] == vehicle) {
                --size_;
                System.arraycopy(vehicles_, i + 1, vehicles_, i, size_ - i);
                System.arraycopy(waitingSince_, i + 1, waitingSince_, i, size_ - i);
                System.arraycopy(lastSeen_, i + 1, lastSeen_, i, size_ - i);
                return true;
            }
        }
        return false;
    }

    public synchronized void delFirstVehicle() {
        if (size_ > 0) {
            --size_;
            if (size_ > 0) {
                System.arraycopy(vehicles_, 1, vehicles_, 0, size_);
                System.arraycopy(waitingSince_, 1, waitingSince_, 0, size_);
                System.arraycopy(lastSeen_, 1, lastSeen_, 0, size_);
            }
        }
    }

    public synchronized void cleanUp() {
        int i, checkTime = renderer_.getTimePassed() - LAST_SEEN_TIMEOUT;
        for (i = size_ - 1; i > -1; --i) {
            if (lastSeen_[i] < checkTime) {
                --size_;
                System.arraycopy(vehicles_, i + 1, vehicles_, i, size_ - i);
                System.arraycopy(waitingSince_, i + 1, waitingSince_, i, size_ - i);
                System.arraycopy(lastSeen_, i + 1, lastSeen_, i, size_ - i);
            }
        }
    }

    public Vehicle getFirstVehicle() {
        if (size_ > 0)
            return vehicles_[0];
        else
            return null;
    }

    public int size() {
        return size_;
    }
}
