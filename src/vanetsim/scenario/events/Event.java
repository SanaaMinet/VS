package vanetsim.scenario.events;

import java.awt.Color;

public abstract class Event implements Comparable<Event> {

    protected int time_;

    protected Color color_;

    public abstract String getText();

    public abstract void destroy();

    public abstract void execute();

    public int getTime() {
        return time_;
    }

    public Color getTextColor() {
        return color_;
    }
}
