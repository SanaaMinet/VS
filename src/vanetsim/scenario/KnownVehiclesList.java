package vanetsim.scenario;
 

public class KnownVehiclesList {

    private static final int VALID_TIME = 2000;

    private static final int HASH_SIZE = 16;

    private static int timePassed_ = 0;

    private KnownVehicle[] head_ = new KnownVehicle[HASH_SIZE];

    private int size_ = 0;

    private static int timePerStep_ = 0;

    public KnownVehiclesList() {
        for (int i = 0; i < HASH_SIZE; ++i) {
            head_[i] = null;
        }
    }

    public synchronized void updateVehicle(Vehicle vehicle, long ID, int x, int y, double speed, long sourceID, boolean isEncrypted, boolean isARSU) {
        boolean found = false;
        int hash = (int) (ID % HASH_SIZE);
        if (hash < 0)
            hash = -hash;
        KnownVehicle next = head_[hash];
        while (next != null) {
            if (next.getID() == ID) {
                if (KnownVehicle.getAmountOfSavedBeacons_() != -1) {
                    next.setArrayCounter((next.getArrayCounter() + 1) % KnownVehicle.getAmountOfSavedBeacons_());
                    int counter = next.getArrayCounter();
                    next.getSavedX_()[counter] = next.getX();
                    next.getSavedY_()[counter] = next.getY();
                    next.getSavedSpeed_()[counter] = next.getSpeed();
                    next.getSavedLastUpdate_()[counter] = next.getLastUpdate();
                }
                next.setX(x);
                next.setY(y);
                next.setLastUpdate(timePassed_);
                next.setSpeed(speed);
                next.setEncrypted_(isEncrypted);
                found = true;
                break;
            }
            next = next.getNext();
        }
        if (!found) {
            next = new KnownVehicle(vehicle, ID, x, y, timePassed_ + VALID_TIME, speed, isEncrypted, timePassed_);
            next.setNext(head_[hash]);
            next.setPrevious(null);
            if (head_[hash] != null)
                head_[hash].setPrevious(next);
            head_[hash] = next;
            ++size_;
        }
 
    }

    public void checkOutdatedVehicles() {
        int timeout = timePassed_ - VALID_TIME;
        KnownVehicle next;
        for (int i = 0; i < HASH_SIZE; ++i) {
            next = head_[i];
            while (next != null) {
                if (next.getLastUpdate() < timeout) {
                    if (next.getNext() != null)
                        next.getNext().setPrevious(next.getPrevious());
                    if (next.getPrevious() != null)
                        next.getPrevious().setNext(next.getNext());
                    else {
                        head_[i] = next.getNext();
                    }
                    --size_;
                }
                next = next.getNext();
            }
        }
    }

    public Vehicle findNearestVehicle(int vehicleX, int vehicleY, int destX, int destY, int maxDistance) {
        double tmpDistance, bestDistance;
        long dx = vehicleX - destX;
        long dy = vehicleY - destY;
        long maxDistanceSquared = (long) maxDistance * maxDistance;
        bestDistance = dx * dx + dy * dy;
        KnownVehicle bestVehicle = null;
        KnownVehicle next;
        for (int i = 0; i < HASH_SIZE; ++i) {
            next = head_[i];
            while (next != null) {
                dx = next.getX() - destX;
                dy = next.getY() - destY;
                tmpDistance = dx * dx + dy * dy;
                if (tmpDistance < bestDistance) {
                    dx = next.getX() - vehicleX;
                    dy = next.getY() - vehicleY;
                    if ((dx * dx + dy * dy) < maxDistanceSquared) {
                        bestDistance = tmpDistance;
                        bestVehicle = next;
                    }
                }
                next = next.getNext();
            }
        }
        if (bestVehicle != null)
            return bestVehicle.getVehicle();
        else
            return null;
    }

    public double[] getBeaconInformationFromVehicles(long monitoredID) {
        int knownTime = 0;
        int constantContact = 0;
        int counter = 0;
        double[] response;
        for (int i = 0; i < HASH_SIZE; ++i) {
            KnownVehicle next = head_[i];
            while (next != null) {
                response = next.getVehicle().getKnownVehiclesList().checkBeacons(monitoredID);
                if (response != null) {
                    if (response[0] > timePerStep_)
                        knownTime += (response[0] - timePerStep_);
                    else
                        knownTime += response[0];
                    constantContact += response[1];
                    counter++;
                }
                next = next.getNext();
            }
        }
        double[] responseValue = { ((double) knownTime / counter), ((double) constantContact / counter), (double) counter };
        return responseValue;
    }

    public double[] checkBeacons(long ID) {
        double[] returnValue = new double[3];
        int hash = (int) (ID % HASH_SIZE);
        if (hash < 0)
            hash = -hash;
        KnownVehicle next = head_[hash];
        while (next != null) {
            if (next.getID() == ID) {
                returnValue[0] = next.getLastUpdate() - VALID_TIME - next.getFirstContact_();
                if (returnValue[0] < 0)
                    returnValue[0] = 0;
                returnValue[1] = next.getPersistentContactCount();
                returnValue[2] = 0;
                return returnValue;
            }
            next = next.getNext();
        }
        return null;
    }

    public int[] hasBeenSeenWaitingFor(long ID) {
        int[] returnValue = { -1, -1 };
        int hash = (int) (ID % HASH_SIZE);
        if (hash < 0)
            hash = -hash;
        KnownVehicle next = head_[hash];
        while (next != null) {
            if (next.getID() == ID) {
                return next.getTimeStanding();
            }
            next = next.getNext();
        }
        return returnValue;
    }

    public void showSpeedData(long ID) {
        int hash = (int) (ID % HASH_SIZE);
        if (hash < 0)
            hash = -hash;
        KnownVehicle next = head_[hash];
        while (next != null) {
            if (next.getID() == ID) {
                next.showSpeedData();
            }
            next = next.getNext();
        }
    }

    public double[] getSpecificSpeedDataSet(long ID, int index1) {
        int hash = (int) (ID % HASH_SIZE);
        if (hash < 0)
            hash = -hash;
        KnownVehicle next = head_[hash];
        while (next != null) {
            if (next.getID() == ID) {
                return next.getSpecificSpeedDataSet(index1);
            }
            next = next.getNext();
        }
        return null;
    }

    public KnownVehicle[] getFirstKnownVehicle() {
        return head_;
    }

    public int getSize() {
        return size_;
    }

    public static void setTimePassed(int time) {
        timePassed_ = time;
    }

    public static void setTimePerStep_(int timePerStep) {
        timePerStep_ = timePerStep;
    }

    public static int getHashSize() {
        return HASH_SIZE;
    }

    public void clear() {
        head_ = new KnownVehicle[HASH_SIZE];
        size_ = 0;
    }
}
