package vanetsim.map;

import java.awt.Color;
import java.util.ArrayList;
 

public final class Node {

    private static int counter_ = 0;

    private final int nodeID_;

    private int x_;

    private int y_;

    private boolean hasTrafficSignal_;

    private Street[] outgoingStreets_ = new Street[0];

    private Street[] crossingStreets_ = new Street[0];

    private Region region_;

    private Junction junction_ = null;

    private int mixZoneRadius_ = 0;

    private TrafficLight trafficLight_ = null;

    private int[] streetHasException_ = null;

    private String amenity_ = "";

    private Color nodeColor = Color.black;

    public Node(int x, int y) {
        x_ = x;
        y_ = y;
        hasTrafficSignal_ = false;
        nodeID_ = counter_;
        ++counter_;
    }

    public Node(int x, int y, boolean hasTrafficSignal) {
        x_ = x;
        y_ = y;
        hasTrafficSignal_ = hasTrafficSignal;
        nodeID_ = counter_;
        ++counter_;
    }

    public void calculateJunction() {
        if (crossingStreets_.length < 3)
            junction_ = null;
        else {
            int size = crossingStreets_.length;
            Street tmpStreet;
            int i, count = 0;
            for (i = 0; i < size; ++i) {
                tmpStreet = crossingStreets_[i];
                if (!tmpStreet.isOneway() || tmpStreet.getEndNode() == this)
                    ++count;
            }
            if (count < 2)
                junction_ = null;
            else {
                ArrayList<Street> priorityStreets = new ArrayList<Street>();
                String[] namesArray = new String[size];
                int[] foundCount = new int[size];
                int[] maxSpeedArray = new int[size];
                int j = 0;
                count = 0;
                boolean alreadyExisted, foundContinuing = false;
                ArrayList<Node> sourceNodes = new ArrayList<Node>();
                for (i = 0; i < size; ++i) {
                    tmpStreet = crossingStreets_[i];
                    if (!tmpStreet.isOneway()) {
                        if (tmpStreet.getStartNode() != this)
                            sourceNodes.add(tmpStreet.getStartNode());
                        else
                            sourceNodes.add(tmpStreet.getEndNode());
                    } else if (tmpStreet.getStartNode() != this)
                        sourceNodes.add(tmpStreet.getStartNode());
                    alreadyExisted = false;
                    for (j = 0; j < count; ++j) {
                        if (namesArray[j].equals(tmpStreet.getName())) {
                            alreadyExisted = true;
                            foundContinuing = true;
                            ++foundCount[j];
                            if (maxSpeedArray[j] < tmpStreet.getSpeed())
                                maxSpeedArray[j] = tmpStreet.getSpeed();
                        }
                    }
                    if (!alreadyExisted) {
                        namesArray[count] = tmpStreet.getName();
                        foundCount[count] = 1;
                        maxSpeedArray[count] = tmpStreet.getSpeed();
                        ++count;
                    }
                }
                if (foundContinuing) {
                    int k = -1, maxSpeed = 0;
                    for (i = 0; i < count; ++i) {
                        if (foundCount[i] > 1 && maxSpeedArray[i] > maxSpeed) {
                            k = i;
                            maxSpeed = maxSpeedArray[i];
                        }
                    }
                    if (k != -1) {
                        for (i = 0; i < size; ++i) {
                            tmpStreet = crossingStreets_[i];
                            if (tmpStreet.getName().equals(namesArray[k]))
                                priorityStreets.add(tmpStreet);
                        }
                    }
                } else {
                    Street fastestStreet = null, secondFastestStreet = null, thirdFastestStreet = null;
                    int fastestSpeed = 0, secondFastestSpeed = 0, thirdFastestSpeed = 0;
                    for (i = 0; i < size; ++i) {
                        tmpStreet = crossingStreets_[i];
                        if (tmpStreet.getSpeed() > fastestSpeed) {
                            thirdFastestSpeed = secondFastestSpeed;
                            thirdFastestStreet = secondFastestStreet;
                            secondFastestSpeed = fastestSpeed;
                            secondFastestStreet = fastestStreet;
                            fastestSpeed = tmpStreet.getSpeed();
                            fastestStreet = tmpStreet;
                        } else if (tmpStreet.getSpeed() > secondFastestSpeed) {
                            secondFastestSpeed = fastestSpeed;
                            secondFastestStreet = fastestStreet;
                            fastestSpeed = tmpStreet.getSpeed();
                            fastestStreet = tmpStreet;
                        } else if (tmpStreet.getSpeed() > thirdFastestSpeed) {
                            thirdFastestSpeed = tmpStreet.getSpeed();
                            thirdFastestStreet = secondFastestStreet;
                        }
                    }
                    priorityStreets.add(fastestStreet);
                    priorityStreets.add(secondFastestStreet);
                    if (thirdFastestStreet != null && ((fastestStreet.isOneway() && secondFastestStreet.isOneway()) || (secondFastestStreet.isOneway() && thirdFastestStreet != null && thirdFastestStreet.isOneway()) || (fastestStreet.isOneway() && thirdFastestStreet != null && thirdFastestStreet.isOneway()))) {
                        priorityStreets.add(thirdFastestStreet);
                    }
                }
                count = 2;
                if (priorityStreets.size() > 2) {
                    Street twoWayStreet = null;
                    Street firstOnewayStreet = null;
                    Street secondOnewayStreet = null;
                    for (i = 0; i < 3; ++i) {
                        if (priorityStreets.get(i).isOneway()) {
                            if (firstOnewayStreet == null)
                                firstOnewayStreet = priorityStreets.get(i);
                            else if (secondOnewayStreet == null)
                                secondOnewayStreet = priorityStreets.get(i);
                        } else if (twoWayStreet == null)
                            twoWayStreet = priorityStreets.get(i);
                    }
                    if (twoWayStreet != null && firstOnewayStreet != null && secondOnewayStreet != null) {
                        if ((firstOnewayStreet.getEndNode() == this && secondOnewayStreet.getEndNode() != this) || (firstOnewayStreet.getEndNode() != this && secondOnewayStreet.getEndNode() == this))
                            count = 3;
                    }
                }
                while (priorityStreets.size() > count) priorityStreets.remove(priorityStreets.size() - 1);
                size = sourceNodes.size();
                Node tmpNode, priorityEndNode, turnOffNode;
                Street sourceStreet = null, targetStreet;
                Street priorityEndStreet;
                junction_ = new Junction(this, priorityStreets.toArray(new Street[1]));
                for (i = 0; i < size; ++i) {
                    tmpNode = sourceNodes.get(i);
                    for (j = 0; j < crossingStreets_.length; ++j) {
                        if (crossingStreets_[j].getStartNode() == tmpNode || crossingStreets_[j].getEndNode() == tmpNode) {
                            sourceStreet = crossingStreets_[j];
                            break;
                        }
                    }
                    priorityEndNode = null;
                    priorityEndStreet = null;
                    if (count == 2) {
                        for (j = 0; j < count; ++j) {
                            if (priorityStreets.get(j) != sourceStreet)
                                priorityEndStreet = priorityStreets.get(j);
                        }
                    } else {
                        if (!sourceStreet.isOneway()) {
                            for (j = 0; j < count; ++j) {
                                if (priorityStreets.get(j) != sourceStreet && priorityStreets.get(j).getStartNode() == this) {
                                    priorityEndStreet = priorityStreets.get(j);
                                }
                            }
                        } else {
                            for (j = 0; j < count; ++j) {
                                if (!priorityStreets.get(j).isOneway()) {
                                    priorityEndStreet = priorityStreets.get(j);
                                }
                            }
                        }
                    }
                    if (priorityEndStreet.getStartNode() != this)
                        priorityEndNode = priorityEndStreet.getStartNode();
                    else
                        priorityEndNode = priorityEndStreet.getEndNode();
                    for (j = 0; j < outgoingStreets_.length; ++j) {
                        targetStreet = outgoingStreets_[j];
                        if (targetStreet != sourceStreet) {
                            if (targetStreet.getStartNode() != this)
                                turnOffNode = targetStreet.getStartNode();
                            else
                                turnOffNode = targetStreet.getEndNode();
                            if (priorityStreets.contains(sourceStreet)) {
                                if (targetStreet == priorityEndStreet) {
                                    junction_.addJunctionRule(tmpNode, turnOffNode, 1);
                                } else if (isLineRight(tmpNode.getX(), tmpNode.getY(), priorityEndNode.getX(), priorityEndNode.getY(), turnOffNode.getX(), turnOffNode.getY())) {
                                    junction_.addJunctionRule(tmpNode, turnOffNode, 2);
                                } else {
                                    junction_.addJunctionRule(tmpNode, turnOffNode, 3);
                                }
                            } else {
                                if (priorityStreets.contains(targetStreet)) {
                                    int k;
                                    if (count == 2) {
                                        for (k = 0; k < count; ++k) {
                                            if (priorityStreets.get(k) != targetStreet)
                                                priorityEndStreet = priorityStreets.get(k);
                                        }
                                    } else {
                                        if (!sourceStreet.isOneway()) {
                                            for (k = 0; k < count; ++k) {
                                                if (priorityStreets.get(k) != targetStreet && priorityStreets.get(k).getStartNode() == this) {
                                                    priorityEndStreet = priorityStreets.get(k);
                                                }
                                            }
                                        } else {
                                            for (k = 0; k < count; ++k) {
                                                if (!priorityStreets.get(k).isOneway()) {
                                                    priorityEndStreet = priorityStreets.get(k);
                                                }
                                            }
                                        }
                                    }
                                    if (priorityEndStreet.getStartNode() != this)
                                        priorityEndNode = priorityEndStreet.getStartNode();
                                    else
                                        priorityEndNode = priorityEndStreet.getEndNode();
                                    if (isLineRight(tmpNode.getX(), tmpNode.getY(), priorityEndNode.getX(), priorityEndNode.getY(), turnOffNode.getX(), turnOffNode.getY())) {
                                        junction_.addJunctionRule(tmpNode, turnOffNode, 4);
                                    } else {
                                        junction_.addJunctionRule(tmpNode, turnOffNode, 5);
                                    }
                                } else {
                                    junction_.addJunctionRule(tmpNode, turnOffNode, 5);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isLineRight(int startX, int starty, int endX, int endY, int turnoffX, int turnoffY) {
        double startLineAngle = (Math.atan2(starty - y_, startX - x_) + Math.PI);
        double endLineAngle = (Math.atan2(endY - y_, endX - x_) + Math.PI);
        double turnoffLineAngle = (Math.atan2(turnoffY - y_, turnoffX - x_) + Math.PI);
        double diffTurnoff = turnoffLineAngle - startLineAngle;
        if (diffTurnoff < 0)
            diffTurnoff += 2 * Math.PI;
        double diffEnd = endLineAngle - startLineAngle;
        if (diffEnd < 0)
            diffEnd += 2 * Math.PI;
        if (diffTurnoff > diffEnd)
            return true;
        else
            return false;
    }

    public Junction getJunction() {
        return junction_;
    }

    public void addOutgoingStreet(Street street) {
        boolean found = false;
        for (int i = 0; i < outgoingStreets_.length; ++i) {
            if (outgoingStreets_[i] == street) {
                found = true;
                break;
            }
        }
        if (!found) {
            Street[] newArray = new Street[outgoingStreets_.length + 1];
            System.arraycopy(outgoingStreets_, 0, newArray, 0, outgoingStreets_.length);
            newArray[outgoingStreets_.length] = street;
            outgoingStreets_ = newArray;
        }
    }

    public boolean delOutgoingStreet(Street street) {
        for (int i = 0; i < outgoingStreets_.length; ++i) {
            if (outgoingStreets_[i] == street) {
                Street[] newArray = new Street[outgoingStreets_.length - 1];
                if (i > 0) {
                    System.arraycopy(outgoingStreets_, 0, newArray, 0, i);
                    System.arraycopy(outgoingStreets_, i + 1, newArray, i, outgoingStreets_.length - i - 1);
                } else
                    System.arraycopy(outgoingStreets_, 1, newArray, 0, outgoingStreets_.length - 1);
                outgoingStreets_ = newArray;
                return true;
            }
        }
        return false;
    }

    public Street[] getOutgoingStreets() {
        return outgoingStreets_;
    }

    public int getOutgoingStreetsCount() {
        return outgoingStreets_.length;
    }

    public void addCrossingStreet(Street street) {
        boolean found = false;
        for (int i = 0; i < crossingStreets_.length; ++i) {
            if (crossingStreets_[i] == street) {
                found = true;
                break;
            }
        }
        if (!found) {
            Street[] newArray = new Street[crossingStreets_.length + 1];
            System.arraycopy(crossingStreets_, 0, newArray, 0, crossingStreets_.length);
            newArray[crossingStreets_.length] = street;
            crossingStreets_ = newArray;
        }
    }

    public boolean delCrossingStreet(Street street) {
        for (int i = 0; i < crossingStreets_.length; ++i) {
            if (crossingStreets_[i] == street) {
                Street[] newArray = new Street[crossingStreets_.length - 1];
                if (i > 0) {
                    System.arraycopy(crossingStreets_, 0, newArray, 0, i);
                    System.arraycopy(crossingStreets_, i + 1, newArray, i, crossingStreets_.length - i - 1);
                } else
                    System.arraycopy(crossingStreets_, 1, newArray, 0, crossingStreets_.length - 1);
                crossingStreets_ = newArray;
                return true;
            }
        }
        return false;
    }

    public Street[] getCrossingStreets() {
        return crossingStreets_;
    }

    public int getCrossingStreetsCount() {
        return crossingStreets_.length;
    }

    public int getX() {
        return x_;
    }

    public void setX(int x) {
        x_ = x;
    }

    public int getY() {
        return y_;
    }

    public void setY(int y) {
        y_ = y;
    }

    public void setRegion(Region region) {
        region_ = region;
    }

    public Region getRegion() {
        return region_;
    }

    public boolean equals(Object other) {
        if (other == null)
            return false;
        else if (!this.getClass().equals(other.getClass()))
            return false;
        else {
            Node othernode = (Node) other;
            if (othernode.getX() == x_ && othernode.getY() == y_)
                return true;
            else
                return false;
        }
    }

    public int getNodeID() {
        return nodeID_;
    }

    public static int getMaxNodeID() {
        return counter_;
    }

    public static void resetNodeID() {
        counter_ = 0;
    }

    @Override
    public int hashCode() {
        return (x_ - y_);
    }

    public void setMixZoneRadius(int mixZoneRadius) {
        mixZoneRadius_ = mixZoneRadius;
    }

    public int getMixZoneRadius() {
        return mixZoneRadius_;
    }

 

    public boolean isHasTrafficSignal_() {
        return hasTrafficSignal_;
    }

    public void setHasTrafficSignal_(boolean hasTrafficSignal_) {
        this.hasTrafficSignal_ = hasTrafficSignal_;
    }

    public void setTrafficLight_(TrafficLight trafficLight_) {
        this.trafficLight_ = trafficLight_;
    }

    public TrafficLight getTrafficLight_() {
        return trafficLight_;
    }

    public int[] getStreetHasException_() {
        return streetHasException_;
    }

    public void setStreetHasException_(int[] streetHasException_) {
        this.streetHasException_ = streetHasException_;
    }

    public void addSignalExceptionsOfString(String arrayString) {
        String[] tmpArray = arrayString.split(":");
        streetHasException_ = new int[tmpArray.length];
        for (int i = 0; i < tmpArray.length; i++) streetHasException_[i] = Integer.parseInt(tmpArray[i]);
    }

    public boolean hasNonDefaultSettings() {
        if (streetHasException_ == null)
            return false;
        boolean tmpReturn = false;
        for (int i : streetHasException_) if (i != 1)
            tmpReturn = true;
        return tmpReturn;
    }

    public String getSignalExceptionsInString() {
        String tmpReturn = "";
        for (int i : streetHasException_) tmpReturn += i + ":";
        if (tmpReturn.length() > 0)
            tmpReturn = tmpReturn.substring(0, tmpReturn.length() - 1);
        return tmpReturn;
    }

    public String getAmenity_() {
        return amenity_;
    }

    public void setAmenity_(String amenity_) {
        this.amenity_ = amenity_;
    }

    public Color getNodeColor() {
        return nodeColor;
    }

    public void setNodeColor(Color nodeColor) {
        this.nodeColor = nodeColor;
    }
}
