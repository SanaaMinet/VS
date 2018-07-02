package vanetsim.gui.helpers;

import vanetsim.scenario.Vehicle;

public class MonitoredVehicle {

    private final Vehicle vehicle_;

    private int lastUpdate_;

    private long ID;

    private int[] lane;

    private int[] distance;

    private double[] speed;

    private int actualIndex = 0;

    protected MonitoredVehicle previous_;

    protected MonitoredVehicle next_;

    public MonitoredVehicle(Vehicle vehicle, int theLane, int x1, int x2, int y1, int y2, double theSpeed, long theID) {
        vehicle_ = vehicle;
        ID = theID;
        lane[actualIndex] = theLane;
        distance[actualIndex] = (int) Math.sqrt(x1 * x2 + y1 * y2);
        speed[actualIndex] = theSpeed;
        actualIndex++;
        actualIndex = actualIndex % 3;
    }

    public void updateVehicle(int theLane, int x1, int x2, int y1, int y2, double theSpeed) {
        lane[actualIndex] = theLane;
        distance[actualIndex] = (int) Math.sqrt(x1 * x2 + y1 * y2);
        speed[actualIndex] = theSpeed;
        actualIndex++;
        actualIndex = actualIndex % 3;
    }

    public void setLastUpdate(int time) {
        lastUpdate_ = time;
    }

    public long getID() {
        return ID;
    }

    public void setID(long iD) {
        ID = iD;
    }

    public int[] getLane() {
        return lane;
    }

    public void setLane(int[] lane) {
        this.lane = lane;
    }

    public int[] getDistance() {
        return distance;
    }

    public void setDistance(int[] distance) {
        this.distance = distance;
    }

    public double[] getSpeed() {
        return speed;
    }

    public void setSpeed(double[] speed) {
        this.speed = speed;
    }

    public int getActualIndex() {
        return actualIndex;
    }

    public void setActualIndex(int actualIndex) {
        this.actualIndex = actualIndex;
    }

    public int getLastUpdate_() {
        return lastUpdate_;
    }

    public void setLastUpdate_(int lastUpdate_) {
        this.lastUpdate_ = lastUpdate_;
    }

    public MonitoredVehicle getNext_() {
        return next_;
    }

    public void setNext_(MonitoredVehicle next_) {
        this.next_ = next_;
    }

    public MonitoredVehicle getPrevious_() {
        return previous_;
    }

    public void setPrevious_(MonitoredVehicle previous_) {
        this.previous_ = previous_;
    }

    public Vehicle getVehicle_() {
        return vehicle_;
    }
}
