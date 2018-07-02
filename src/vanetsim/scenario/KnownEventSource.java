package vanetsim.scenario;

public class KnownEventSource {

    private final Vehicle vehicle_;

    private final long ID_;

    private final int firstContact_;

    private int x_;

    private int y_;

    private double speed_;

    private int lastUpdate_;

    private int fakeMessageCounter_;

    private int realMessageCounter_;

    protected KnownEventSource previous_;

    protected KnownEventSource next_;

    private int updates_ = 0;

    private static boolean spamCheck_ = false;

    private static int spammingThreshold_ = 3;

    private static int spammingTimeThreshold_ = 240000;

    private int spamCounter_ = 0;

    public KnownEventSource(Vehicle vehicle, long ID, int x, int y, double speed, int timePassed, boolean isFake) {
        vehicle_ = vehicle;
        ID_ = ID;
        x_ = x;
        y_ = y;
        speed_ = speed;
        lastUpdate_ = timePassed;
        firstContact_ = timePassed;
        if (isFake)
            fakeMessageCounter_++;
        else
            realMessageCounter_++;
    }

    public void setX(int x) {
        x_ = x;
    }

    public void setY(int y) {
        y_ = y;
    }

    public void setLastUpdate(int time) {
        lastUpdate_ = time;
    }

    public void setSpeed(double speed) {
        speed_ = speed;
    }

    public int getX() {
        return x_;
    }

    public int getY() {
        return y_;
    }

    public long getID() {
        return ID_;
    }

    public double getSpeed() {
        return speed_;
    }

    public Vehicle getVehicle() {
        return vehicle_;
    }

    public int getLastUpdate() {
        return lastUpdate_;
    }

    public KnownEventSource getNext() {
        return next_;
    }

    public KnownEventSource getPrevious() {
        return previous_;
    }

    public void setNext(KnownEventSource next) {
        next_ = next;
    }

    public void setPrevious(KnownEventSource previous) {
        previous_ = previous;
    }

    public int getFirstContact_() {
        return firstContact_;
    }

    public int getFakeMessageCounter_() {
        return fakeMessageCounter_;
    }

    public void setFakeMessageCounter_(int fakeMessageCounter_) {
        this.fakeMessageCounter_ = fakeMessageCounter_;
    }

    public int getRealMessageCounter_() {
        return realMessageCounter_;
    }

    public void setRealMessageCounter_(int realMessageCounter_) {
        this.realMessageCounter_ = realMessageCounter_;
    }

    public int getUpdates_() {
        return updates_;
    }

    public void setUpdates_(int updates) {
        updates_ = updates;
        if (spamCheck_) {
            if (updates_ >= (spammingThreshold_ - 1) && (lastUpdate_ - firstContact_ - (updates_ * 80)) <= updates_ * spammingTimeThreshold_) {
                spamCounter_++;
            }
        }
    }

    public int getSpamCounter_() {
        return spamCounter_;
    }

    public void setSpamCounter_(int spamCounter_) {
        this.spamCounter_ = spamCounter_;
    }

    public static int getSpammingthreshold() {
        return spammingThreshold_;
    }

    public static int getSpammingtimethreshold() {
        return spammingTimeThreshold_;
    }

    public static void setSpammingThreshold_(int spammingThreshold_) {
        KnownEventSource.spammingThreshold_ = spammingThreshold_;
    }

    public static void setSpammingTimeThreshold_(int spammingTimeThreshold_) {
        KnownEventSource.spammingTimeThreshold_ = spammingTimeThreshold_;
    }

    public static boolean isSpamcheck() {
        return spamCheck_;
    }

    public static void setSpamCheck_(boolean spamCheck_) {
        KnownEventSource.spamCheck_ = spamCheck_;
    }
}
