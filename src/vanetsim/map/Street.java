package vanetsim.map;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import vanetsim.scenario.LaneContainer;
import vanetsim.scenario.LaneObject;

public final class Street {

    private final double length_;

    private final double xFactor_;

    private final double yFactor_;

    private final Region mainRegion_;

    private final LaneContainer startToEndLane_ = new LaneContainer(true);

    private final LaneContainer endToStartLane_ = new LaneContainer(false);

    private String name_;

    private Node startNode_;

    private Node endNode_;

    private int maxSpeed_;

    private boolean oneway_;

    private String streetType_;

    private int laneCount_;

    private Color displayColor_;

    private ArrayList<Point2D.Double> bridgePaintLines_ = null;

    private ArrayList<Point2D.Double> bridgePaintPolygons_ = null;

    private int startNodeTrafficLightState_ = -1;

    private int endNodeTrafficLightState_ = -1;

    private int trafficLightEndX_ = -1;

    private int trafficLightEndY_ = -1;

    private int trafficLightStartX_ = -1;

    private int trafficLightStartY_ = -1;

    private boolean priorityOnEndNode = false;

    private boolean priorityOnStartNode = false;

    public Street(String name, Node startNode, Node endNode, String streetType, int oneway, int lanes, Color displayColor, Region mainRegion, int maxSpeed) {
        name_ = name;
        streetType_ = streetType;
        displayColor_ = displayColor;
        laneCount_ = lanes;
        mainRegion_ = mainRegion;
        maxSpeed_ = maxSpeed;
        if (oneway == 0) {
            startNode_ = startNode;
            endNode_ = endNode;
            startNode_.addOutgoingStreet(this);
            endNode_.addOutgoingStreet(this);
            startNode_.addCrossingStreet(this);
            endNode_.addCrossingStreet(this);
            oneway_ = false;
        } else if (oneway == 1) {
            startNode_ = startNode;
            endNode_ = endNode;
            startNode_.addOutgoingStreet(this);
            startNode_.addCrossingStreet(this);
            endNode_.addCrossingStreet(this);
            oneway_ = true;
        } else {
            endNode_ = startNode;
            startNode_ = endNode;
            startNode_.addOutgoingStreet(this);
            startNode_.addCrossingStreet(this);
            endNode_.addCrossingStreet(this);
            oneway_ = true;
        }
        long dx = endNode_.getX() - startNode_.getX();
        long dy = endNode_.getY() - startNode_.getY();
        length_ = Math.sqrt(dx * dx + dy * dy);
        double[] result = new double[2];
        MapHelper.getXYParallelRight(startNode_.getX(), startNode_.getY(), endNode_.getX(), endNode_.getY(), Map.LANE_WIDTH, result);
        xFactor_ = result[0];
        yFactor_ = result[1];
    }

    public double getLength() {
        return length_;
    }

    public double getXFactor() {
        return xFactor_;
    }

    public double getYFactor() {
        return yFactor_;
    }

    public int getSpeed() {
        return maxSpeed_;
    }

    public void setSpeed(int maxSpeed) {
        maxSpeed_ = maxSpeed;
    }

    public void changeOneWay(int oneway) {
        startNode_.delOutgoingStreet(this);
        endNode_.delOutgoingStreet(this);
        if (oneway == 0) {
            oneway_ = false;
            startNode_.addOutgoingStreet(this);
            endNode_.addOutgoingStreet(this);
        } else if (oneway == 1) {
            oneway_ = true;
            startNode_.addOutgoingStreet(this);
            endNode_.delOutgoingStreet(this);
        } else {
            oneway_ = true;
            Node tmpNode = endNode_;
            endNode_ = startNode_;
            startNode_ = tmpNode;
            startNode_.addOutgoingStreet(this);
            endNode_.delOutgoingStreet(this);
        }
    }

    public int getLanesCount() {
        return laneCount_;
    }

    public void setLanesCount(int laneCount) {
        laneCount_ = laneCount;
    }

    public LaneObject getFirstLaneObject(boolean direction) {
        if (direction)
            return startToEndLane_.getHead();
        else
            return endToStartLane_.getHead();
    }

    public LaneObject getLastLaneObject(boolean direction) {
        if (direction)
            return startToEndLane_.getTail();
        else
            return endToStartLane_.getTail();
    }

    public void addLaneObject(LaneObject object, boolean direction) {
        if (direction)
            startToEndLane_.addSorted(object);
        else
            endToStartLane_.addSorted(object);
    }

    public void delLaneObject(LaneObject object, boolean direction) {
        if (direction)
            startToEndLane_.remove(object);
        else
            endToStartLane_.remove(object);
    }

    public void updateLaneObject(LaneObject object, boolean direction, double newPosition) {
        if (direction)
            startToEndLane_.updatePosition(object, newPosition);
        else
            endToStartLane_.updatePosition(object, newPosition);
    }

    public void clearLanes() {
        startToEndLane_.clear();
        endToStartLane_.clear();
    }

    public void addBridgePaintLine(double x1, double y1, double x2, double y2) {
        if (bridgePaintLines_ == null)
            bridgePaintLines_ = new ArrayList<Point2D.Double>(2);
        bridgePaintLines_.add(new Point2D.Double(x1, y1));
        bridgePaintLines_.add(new Point2D.Double(x2, y2));
    }

    public void addBridgePaintPolygon(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        if (bridgePaintPolygons_ == null)
            bridgePaintPolygons_ = new ArrayList<Point2D.Double>(2);
        bridgePaintPolygons_.add(new Point2D.Double(x1, y1));
        bridgePaintPolygons_.add(new Point2D.Double(x2, y2));
        bridgePaintPolygons_.add(new Point2D.Double(x3, y3));
        bridgePaintPolygons_.add(new Point2D.Double(x4, y4));
    }

    public ArrayList<Point2D.Double> getBridgePaintLines() {
        return bridgePaintLines_;
    }

    public ArrayList<Point2D.Double> getBridgePaintPolygons() {
        return bridgePaintPolygons_;
    }

    public Node getStartNode() {
        return startNode_;
    }

    public void setStartNode(Node startNode) {
        startNode_ = startNode;
    }

    public Node getEndNode() {
        return endNode_;
    }

    public void setEndNode(Node endNode) {
        endNode_ = endNode;
    }

    public Color getDisplayColor() {
        return displayColor_;
    }

    public void setDisplayColor(Color displayColor) {
        displayColor_ = displayColor;
    }

    public Region getMainRegion() {
        return mainRegion_;
    }

    public void setName(String name) {
        name_ = name;
    }

    public String getName() {
        return name_;
    }

    public boolean isOneway() {
        return oneway_;
    }

    public boolean equals(Object other) {
        if (other == null)
            return false;
        else if (!this.getClass().equals(other.getClass()))
            return false;
        else {
            Street otherstreet = (Street) other;
            if (otherstreet.getStartNode() == startNode_ && otherstreet.getEndNode() == endNode_)
                return true;
            else
                return false;
        }
    }

    public int hashCode() {
        return new Long(((long) startNode_.getX() - startNode_.getY() + endNode_.getY() - endNode_.getX()) % Integer.MAX_VALUE).intValue();
    }

    public String getStreetType_() {
        return streetType_;
    }

    public void setStreetType_(String streetType_) {
        this.streetType_ = streetType_;
    }

    public int getStartNodeTrafficLightState() {
        return startNodeTrafficLightState_;
    }

    public void setStartNodeTrafficLightState(int startNodeTrafficLightState_) {
        this.startNodeTrafficLightState_ = startNodeTrafficLightState_;
    }

    public int getEndNodeTrafficLightState() {
        return endNodeTrafficLightState_;
    }

    public void setEndNodeTrafficLightState(int endNodeTrafficLightState_) {
        this.endNodeTrafficLightState_ = endNodeTrafficLightState_;
    }

    public void updateStartNodeTrafficLightState() {
        startNodeTrafficLightState_ = (startNodeTrafficLightState_ + 1) % 8;
    }

    public void updateEndNodeTrafficLightState() {
        endNodeTrafficLightState_ = (endNodeTrafficLightState_ + 1) % 8;
    }

    public void setTrafficLightEndX_(int trafficLightEndX_) {
        this.trafficLightEndX_ = trafficLightEndX_;
    }

    public int getTrafficLightEndX_() {
        return trafficLightEndX_;
    }

    public void setTrafficLightStartX_(int trafficLightStartX_) {
        this.trafficLightStartX_ = trafficLightStartX_;
    }

    public int getTrafficLightStartX_() {
        return trafficLightStartX_;
    }

    public void setTrafficLightStartY_(int trafficLightStartY_) {
        this.trafficLightStartY_ = trafficLightStartY_;
    }

    public int getTrafficLightStartY_() {
        return trafficLightStartY_;
    }

    public void setTrafficLightEndY_(int trafficLightEndY_) {
        this.trafficLightEndY_ = trafficLightEndY_;
    }

    public int getTrafficLightEndY_() {
        return trafficLightEndY_;
    }

    public void setPriorityOnEndNode(boolean priorityOnEndNode) {
        this.priorityOnEndNode = priorityOnEndNode;
    }

    public boolean isPriorityOnEndNode() {
        return priorityOnEndNode;
    }

    public void setPriorityOnStartNode(boolean priorityOnStartNode) {
        this.priorityOnStartNode = priorityOnStartNode;
    }

    public boolean isPriorityOnStartNode() {
        return priorityOnStartNode;
    }
}
