package vanetsim.scenario.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

public final class EventList {

    private static final EventList INSTANCE = new EventList();

    private final PriorityQueue<Event> allEvents_ = new PriorityQueue<Event>(16);

    private final ArrayList<StartBlocking> currentBlockings_ = new ArrayList<StartBlocking>(16);

    private EventList() {
    }

    public static EventList getInstance() {
        return INSTANCE;
    }

    public void addEvent(Event event) {
        allEvents_.add(event);
    }

    public void delEvent(Event event) {
        event.destroy();
        allEvents_.remove(event);
    }

    public void clearEvents() {
        allEvents_.clear();
        currentBlockings_.clear();
    }

    public Iterator<Event> getIterator() {
        return allEvents_.iterator();
    }

    public ArrayList<StartBlocking> getAllBlockingsArrayList() {
        Iterator<Event> events = allEvents_.iterator();
        ArrayList<StartBlocking> result = new ArrayList<StartBlocking>(16);
        Event event;
        while (events.hasNext()) {
            event = events.next();
            if (event.getClass() == StartBlocking.class) {
                result.add((StartBlocking) event);
            }
        }
        return result;
    }

    public ArrayList<StartBlocking> getCurrentBlockingsArrayList() {
        return currentBlockings_;
    }

    public void addCurrentBlockings(StartBlocking event) {
        currentBlockings_.add(event);
    }

    public void delCurrentBlockings(StartBlocking event) {
        currentBlockings_.remove(event);
    }

    public void processEvents(int time) {
        Event tmpEvent;
        while (allEvents_.size() > 0) {
            tmpEvent = allEvents_.peek();
            if (tmpEvent.getTime() <= time) {
                allEvents_.poll();
                tmpEvent.execute();
            } else
                break;
        }
    }
}
