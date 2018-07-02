package vanetsim.scenario.events;

import java.awt.Color;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import vanetsim.ErrorLog;
import vanetsim.localization.Messages;

public final class StopBlocking extends Event {

    private final StartBlocking startBlockingEvent_;

    public StopBlocking(int time, int x, int y) throws ParseException {
        time_ = time;
        color_ = new Color(9, 115, 33);
        long dx, dy, distance, bestdistance = Long.MAX_VALUE;
        ;
        Event event;
        StartBlocking tmpstartblock, beststartblock = null;
        Iterator<Event> eventIterator = EventList.getInstance().getIterator();
        while (eventIterator.hasNext()) {
            event = eventIterator.next();
            if (event.getTime() > time)
                break;
            if (event.getClass() == StartBlocking.class) {
                tmpstartblock = (StartBlocking) event;
                if (tmpstartblock.getStopBlockingEvent() == null) {
                    dx = tmpstartblock.getX() - x;
                    dy = tmpstartblock.getY() - y;
                    distance = dx * dx + dy * dy;
                    if (distance < 10000000000L && distance < bestdistance) {
                        bestdistance = distance;
                        beststartblock = tmpstartblock;
                    }
                }
            }
        }
        if (beststartblock != null) {
            startBlockingEvent_ = beststartblock;
            startBlockingEvent_.setStopBlockingEvent(this);
        } else
            throw new ParseException(Messages.getString("StopBlocking.noBlockingEventFOund"), 0);
    }

    public StopBlocking(int time, int x, int y, StartBlocking startBlockingEvent) {
        startBlockingEvent_ = startBlockingEvent;
        time_ = time;
        color_ = new Color(9, 115, 33);
        startBlockingEvent_.setStopBlockingEvent(this);
    }

    public int getX() {
        return startBlockingEvent_.getX();
    }

    public int getY() {
        return startBlockingEvent_.getY();
    }

    public String getText() {
        return ("<html>" + Messages.getString("StopBlocking.unblockingStreet") + startBlockingEvent_.getStreet().getName() + "<br>" + Messages.getString("StopBlocking.createdAt") + startBlockingEvent_.getTime() + " ms)");
    }

    public int compareTo(Event other) {
        if (other == this)
            return 0;
        else if (other.getTime() > time_)
            return -1;
        else if (other.getTime() < time_)
            return 1;
        else {
            if (other.getClass() == StartBlocking.class)
                return 1;
            else {
                if (other.hashCode() > hashCode())
                    return -1;
                else if (other.hashCode() < hashCode())
                    return 1;
                else {
                    ErrorLog.log(Messages.getString("StopBlocking.eventCompareError"), 7, StopBlocking.class.getName(), "compareTo", null);
                    return 0;
                }
            }
        }
    }

    public void execute() {
        ArrayList<BlockingObject> blockingObjects = startBlockingEvent_.getBlockingObjects();
        for (int i = 0; i < blockingObjects.size(); ++i) {
            blockingObjects.get(i).removeFromLane();
        }
        EventList.getInstance().delCurrentBlockings(startBlockingEvent_);
    }

    public void destroy() {
        startBlockingEvent_.setStopBlockingEvent(null);
    }
}
