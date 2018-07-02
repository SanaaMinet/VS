package vanetsim.scenario;

import vanetsim.gui.controlpanels.ClusteringControlPanel;
import vanetsim.map.Street;

public class LaneObject {

    protected LaneObject previous_;

    protected LaneObject next_;

    protected int curX_;

    protected int curY_;

    protected double curSpeed_ = 0.0;

    protected double curPosition_;

    protected int curLane_ = 1;

    protected Street curStreet_;

    protected boolean curDirection_ = true;

    protected void calculatePosition() 
    {
        double addX = 0, addY = 0;
        double rightmost;
        if (curStreet_.isOneway()) {
            if (curStreet_.getLanesCount() % 2 == 0)
                rightmost = curStreet_.getLanesCount() / 2 + 0.5;
            else
                rightmost = curStreet_.getLanesCount() / 2 + 1;
        } else
            rightmost = curStreet_.getLanesCount() + 0.5;
        if (curDirection_) {
            addX = curStreet_.getXFactor() * (rightmost - curLane_);
            addY = curStreet_.getYFactor() * (rightmost - curLane_);
        } else {
            addX = -curStreet_.getXFactor() * (rightmost - curLane_);
            addY = -curStreet_.getYFactor() * (rightmost - curLane_);
        }
        double percentOnStreet = curPosition_ / curStreet_.getLength();
        curX_ = (int) StrictMath.floor(0.5d + addX + curStreet_.getStartNode().getX() + ((curStreet_.getEndNode().getX() - curStreet_.getStartNode().getX()) * percentOnStreet));
        curY_ = (int) StrictMath.floor(0.5d + addY + curStreet_.getStartNode().getY() + ((curStreet_.getEndNode().getY() - curStreet_.getStartNode().getY()) * percentOnStreet));
           
    }

    public int getX() {
        return curX_;
    }

    public int getY() {
        return curY_;
    }

    public int getCurSpeed() {
        return (int) Math.round(curSpeed_);
    }

    public double getCurPosition() {
        return curPosition_;
    }

    public int getCurLane() {
        return curLane_;
    }

    public Street getCurStreet() {
        return curStreet_;
    }

    public boolean getCurDirection() {
        return curDirection_;
    }

    public LaneObject getNext() {
        return next_;
    }

    public LaneObject getPrevious() {
        return previous_;
    }

    public void setNext(LaneObject next) {
        next_ = next;
    }

    public void setPrevious(LaneObject previous) {
        previous_ = previous;
    }
}
