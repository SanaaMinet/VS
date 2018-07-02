/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vanetsim.scenario.events;

import java.awt.Color;
import java.text.ParseException;
import java.util.ArrayList;
import vanetsim.ErrorLog;
import vanetsim.localization.Messages;
import vanetsim.map.MapHelper;
import vanetsim.map.Street;

public final class StartBlocking extends Event {

    private final Street affectedStreet_;

    private final double affectedStreetPos_;

    private final int affectedDirection_;

    private final int affectedLanes_;

    private final boolean isFake_;

    private final String penaltyType_;

    private StopBlocking stopBlockingEvent_ = null;

    private ArrayList<BlockingObject> blockingObjects_ = null;

    private boolean isFirst_;

    public StartBlocking(int time, int x, int y, int direction, int lanes, boolean isFake, String penaltyType) throws ParseException {
        time_ = time;
        color_ = Color.red;
        affectedDirection_ = direction;
        affectedLanes_ = lanes;
        isFake_ = isFake;
        isFirst_ = true;
        penaltyType_ = penaltyType;
        int[] nearestpoint = new int[2];
        affectedStreet_ = MapHelper.findNearestStreet(x, y, 10000, new double[1], nearestpoint);
        if (affectedStreet_ != null) {
            long tmp1 = affectedStreet_.getStartNode().getX() - nearestpoint[0];
            long tmp2 = affectedStreet_.getStartNode().getY() - nearestpoint[1];
            affectedStreetPos_ = Math.sqrt(tmp1 * tmp1 + tmp2 * tmp2);
        } else
            throw new ParseException(Messages.getString("StartBlocking.snappingFailed"), 0);
    }

    public String getText() {
        String tmp;
        if (affectedDirection_ == 0)
            tmp = Messages.getString("StartBlocking.both");
        else if (affectedDirection_ == 1)
            tmp = Messages.getString("StartBlocking.fromStartNode");
        else
            tmp = Messages.getString("StartBlocking.fromEndNode");
        return ("<html>" + Messages.getString("StartBlocking.blockingStreet") + affectedStreet_.getName() + "<br>" + Messages.getString("StartBlocking.direction") + tmp + Messages.getString("StartBlocking.lanes") + affectedLanes_ + "<br>" + Messages.getString("StartBlocking.isFake") + isFake_ + "<br>" + Messages.getString("StartBlocking.penaltyType") + penaltyType_);
    }

    public int compareTo(Event other) {
        if (other == this)
            return 0;
        else if (other.getTime() > time_)
            return -1;
        else if (other.getTime() < time_)
            return 1;
        else {
            if (other.getClass() == StopBlocking.class)
                return -1;
            else {
                if (other.hashCode() > hashCode())
                    return -1;
                else if (other.hashCode() < hashCode())
                    return 1;
                else {
                    ErrorLog.log(Messages.getString("StartBlocking.eventCompareError"), 7, StartBlocking.class.getName(), "compareTo", null);
                    return 0;
                }
            }
        }
    }

    public void execute() {
        EventList.getInstance().addCurrentBlockings(this);
        if (blockingObjects_ == null)
            blockingObjects_ = new ArrayList<BlockingObject>(2);
        int lanes = 0;
        lanes = Math.min(affectedLanes_, affectedStreet_.getLanesCount() + 1);
        if (affectedDirection_ == 0 || affectedDirection_ == 1) {
            for (int i = 1; i <= lanes; ++i) {
                blockingObjects_.add(new BlockingObject(i, true, affectedStreet_, affectedStreetPos_, penaltyType_));
            }
        }
        if (!affectedStreet_.isOneway() && (affectedDirection_ == 0 || affectedDirection_ == -1)) {
            for (int i = 1; i <= lanes; ++i) {
                blockingObjects_.add(new BlockingObject(i, false, affectedStreet_, affectedStreetPos_, penaltyType_));
            }
        }
    }

    public void destroy() {
    }

    public int getX() {
        return (int) Math.round(-0.5 + affectedStreet_.getStartNode().getX() + ((affectedStreet_.getEndNode().getX() - affectedStreet_.getStartNode().getX()) * affectedStreetPos_ / affectedStreet_.getLength()));
    }

    public int getY() {
        return (int) Math.round(-0.5 + affectedStreet_.getStartNode().getY() + ((affectedStreet_.getEndNode().getY() - affectedStreet_.getStartNode().getY()) * affectedStreetPos_ / affectedStreet_.getLength()));
    }

    public StopBlocking getStopBlockingEvent() {
        return stopBlockingEvent_;
    }

    public void setStopBlockingEvent(StopBlocking event) {
        stopBlockingEvent_ = event;
    }

    public Street getStreet() {
        return affectedStreet_;
    }

    public int getAffectedDirection() {
        return affectedDirection_;
    }

    public ArrayList<BlockingObject> getBlockingObjects() {
        return blockingObjects_;
    }

    public int getAffectedLanes() {
        return affectedLanes_;
    }

    public boolean isFake_() {
        return isFake_;
    }

    public String getPenaltyType_() {
        return penaltyType_;
    }

    public boolean isFirst_() {
        return isFirst_;
    }

    public void setFirst_(boolean isFirst_) {
        this.isFirst_ = isFirst_;
    }
}

