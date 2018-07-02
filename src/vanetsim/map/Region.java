package vanetsim.map;

import java.util.ArrayList;
import vanetsim.gui.Renderer;
import vanetsim.gui.helpers.PrivacyLogWriter;
import vanetsim.scenario.Vehicle;
 
import vanetsim.simulation.WorkerThread;

public final class Region {

    private static final Vehicle[] EMPTY_VEHICLE = new Vehicle[0];

    private final int x_;

    private final int y_;

    private final int leftBoundary_;

    private final int rightBoundary_;

    private final int upperBoundary_;

    private final int lowerBoundary_;

    private Node[] nodes_ = new Node[0];

 

    private Node[] mixZoneNodes_ = new Node[0];

    private Street[] streets_ = new Street[0];

    private ArrayList<Vehicle> vehicles_;

    private Vehicle[] vehiclesArray_;

    private WorkerThread thread_ = null;

    private int numberInThread_ = -1;

    private boolean vehiclesDirty_ = true;

    public ArrayList<String> xxx = new ArrayList<String>();

    public ArrayList<String> yyy = new ArrayList<String>();

    public ArrayList<String> nnn = new ArrayList<String>();

    public Region(int x, int y, int leftBoundary, int rightBoundary, int upperBoundary, int lowerBoundary) {
        vehicles_ = new ArrayList<Vehicle>(1);
        x_ = x;
        y_ = y;
        leftBoundary_ = leftBoundary;
        rightBoundary_ = rightBoundary;
        upperBoundary_ = upperBoundary;
        lowerBoundary_ = lowerBoundary;
    }

    public Node addNode(Node node, boolean doCheck) {
        if (doCheck) {
            Node curNode, foundNode = null;
            int x = node.getX();
            int y = node.getY();
            for (int i = 0; i < nodes_.length; ++i) {
                curNode = nodes_[i];
                if (curNode.getX() == x && curNode.getY() == y) {
                    foundNode = curNode;
                    break;
                }
            }
            if (foundNode != null)
                return foundNode;
        }
        Node[] newArray = new Node[nodes_.length + 1];
        System.arraycopy(nodes_, 0, newArray, 0, nodes_.length);
        newArray[nodes_.length] = node;
        nodes_ = newArray;
        return node;
    }

    public void delNode(Node node) {
        for (int i = 0; i < nodes_.length; ++i) {
            if (nodes_[i] == node) {
                Node[] newArray = new Node[nodes_.length - 1];
                if (i > 0) {
                    System.arraycopy(nodes_, 0, newArray, 0, i);
                    System.arraycopy(nodes_, i + 1, newArray, i, nodes_.length - i - 1);
                } else
                    System.arraycopy(nodes_, 1, newArray, 0, nodes_.length - 1);
                nodes_ = newArray;
            }
        }
    }

 
    public void addStreet(Street street, boolean doCheck) {
        boolean foundstreet = false;
        boolean createBridges = false;
        if (Map.getInstance().getReadyState() == true)
            createBridges = true;
        if (streets_.length > 0 && (doCheck || createBridges)) {
            Street otherStreet;
            int color1, color2;
            for (int i = 0; i < streets_.length; ++i) {
                otherStreet = streets_[i];
                if ((street.getStartNode() == otherStreet.getStartNode() || street.getStartNode() == otherStreet.getEndNode()) && (street.getEndNode() == otherStreet.getEndNode() || street.getEndNode() == otherStreet.getStartNode()))
                    foundstreet = true;
                if (createBridges) {
                    color1 = street.getDisplayColor().getRGB();
                    color2 = otherStreet.getDisplayColor().getRGB();
                    if (color1 != color2) {
                        if (color1 < color2)
                            MapHelper.calculateBridges(otherStreet, street);
                        else
                            MapHelper.calculateBridges(street, otherStreet);
                    } else {
                        if (street.getBridgePaintLines() != null || street.getBridgePaintPolygons() != null)
                            MapHelper.calculateBridges(street, otherStreet);
                        else if (otherStreet.getBridgePaintLines() != null || otherStreet.getBridgePaintPolygons() != null)
                            MapHelper.calculateBridges(otherStreet, street);
                        else if (street.getSpeed() > otherStreet.getSpeed())
                            MapHelper.calculateBridges(otherStreet, street);
                        else
                            MapHelper.calculateBridges(street, otherStreet);
                    }
                }
            }
        }
        if (!doCheck || !foundstreet) {
            Street[] newArray = new Street[streets_.length + 1];
            System.arraycopy(streets_, 0, newArray, 0, streets_.length);
            newArray[streets_.length] = street;
            streets_ = newArray;
        }
    }

    public void checkStreetsForBridges() {
        if (streets_.length > 0) {
            Street firstStreet, secondStreet;
            int color1, color2, size = streets_.length;
            for (int i = 0; i < size; ++i) {
                firstStreet = streets_[i];
                for (int j = i + 1; j < size; ++j) {
                    secondStreet = streets_[j];
                    color1 = firstStreet.getDisplayColor().getRGB();
                    color2 = secondStreet.getDisplayColor().getRGB();
                    if (color1 != color2) {
                        if (color1 < color2)
                            MapHelper.calculateBridges(secondStreet, firstStreet);
                        else
                            MapHelper.calculateBridges(firstStreet, secondStreet);
                    } else {
                        if (firstStreet.getBridgePaintLines() != null || firstStreet.getBridgePaintPolygons() != null)
                            MapHelper.calculateBridges(firstStreet, secondStreet);
                        else if (secondStreet.getBridgePaintLines() != null || secondStreet.getBridgePaintPolygons() != null)
                            MapHelper.calculateBridges(secondStreet, firstStreet);
                        else if (firstStreet.getSpeed() > secondStreet.getSpeed())
                            MapHelper.calculateBridges(secondStreet, firstStreet);
                        else
                            MapHelper.calculateBridges(firstStreet, secondStreet);
                    }
                }
            }
        }
    }

    public void delStreet(Street street) {
        for (int i = 0; i < streets_.length; ++i) {
            if (streets_[i] == street) {
                Street[] newArray = new Street[streets_.length - 1];
                if (i > 0) {
                    System.arraycopy(streets_, 0, newArray, 0, i);
                    System.arraycopy(streets_, i + 1, newArray, i, streets_.length - i - 1);
                } else
                    System.arraycopy(streets_, 1, newArray, 0, streets_.length - 1);
                streets_ = newArray;
            }
        }
    }

    public synchronized void addVehicle(Vehicle vehicle, boolean doCheck) {
        if (doCheck) {
            if (!vehicles_.contains(vehicle)) {
                vehicles_.add(vehicle);
                if (thread_ != null)
                    thread_.addChangedRegion(numberInThread_);
                vehiclesDirty_ = true;
            }
        } else {
            vehicles_.add(vehicle);
            if (thread_ != null)
                thread_.addChangedRegion(numberInThread_);
            vehiclesDirty_ = true;
        }
    }

    public synchronized void delVehicle(Vehicle vehicle) {
        vehicles_.remove(vehicle);
        if (thread_ != null)
            thread_.addChangedRegion(numberInThread_);
        vehiclesDirty_ = true;
    }

    public int getX() {
        return x_;
    }

    public int getY() {
        return y_;
    }

    public void calculateJunctions() {
        if (Renderer.getInstance().isAutoAddMixZones())
            mixZoneNodes_ = new Node[0];
        for (int i = 0; i < nodes_.length; ++i) {
            nodes_[i].calculateJunction();
            if (Renderer.getInstance().isAutoAddMixZones()) {
                if (nodes_[i].getJunction() != null) {
                    Node[] newArray = new Node[mixZoneNodes_.length + 1];
                    System.arraycopy(mixZoneNodes_, 0, newArray, 0, mixZoneNodes_.length);
                    newArray[mixZoneNodes_.length] = nodes_[i];
                    nodes_[i].setMixZoneRadius(Vehicle.getMixZoneRadius());
                    mixZoneNodes_ = newArray;
 
                }
            }
            if (nodes_[i].getJunction() != null && nodes_[i].getJunction().getNode().getTrafficLight_() == null && nodes_[i].isHasTrafficSignal_())
                new TrafficLight(nodes_[i].getJunction());
        }
        prepareLogs(nodes_);
    }

    public void addMixZone(Node node, int radius) {
        boolean found = false;
        int x = node.getX();
        int y = node.getY();
        for (int i = 0; i < mixZoneNodes_.length; i++) {
            if (x == mixZoneNodes_[i].getX() && y == mixZoneNodes_[i].getY())
                found = true;
        }
        if (!found) {
            Node[] newArray = new Node[mixZoneNodes_.length + 1];
            System.arraycopy(mixZoneNodes_, 0, newArray, 0, mixZoneNodes_.length);
            newArray[mixZoneNodes_.length] = node;
            node.setMixZoneRadius(radius);
            mixZoneNodes_ = newArray;
 
        }
    }

    public void deleteMixZone(Node node) {
        for (int i = 0; i < mixZoneNodes_.length; ++i) {
            if (mixZoneNodes_[i] == node) {
                Node[] newArray = new Node[mixZoneNodes_.length - 1];
                if (i > 0) {
                    System.arraycopy(mixZoneNodes_, 0, newArray, 0, i);
                    System.arraycopy(mixZoneNodes_, i + 1, newArray, i, mixZoneNodes_.length - i - 1);
                } else
                    System.arraycopy(mixZoneNodes_, 1, newArray, 0, mixZoneNodes_.length - 1);
                mixZoneNodes_ = newArray;
            }
        }
 
    }

    public void prepareLogs(Node[] nodes) {
        String coordinates[] = null;
        Node node = null;
        for (int k = 0; k < nodes.length; ++k) {
            node = nodes[k];
            if (node.getMixZoneRadius() > 0) {
                coordinates = getIntersectionPoints(node, this);
                if (coordinates != null) {
                    String[] xxx2 = coordinates[0].split(":");
                    String[] yyy2 = coordinates[1].split(":");
                    for (int i = 5; i < xxx2.length; i++) {
                        xxx.add(xxx2[i]);
                        yyy.add(yyy2[i]);
                        nnn.add("" + (i - 4));
                    }
                }
                for (String s : coordinates) PrivacyLogWriter.log(s);
            }
        }
    }

    public String[] getIntersectionPoints(Node mixNode, Region region12) {
        Region[][] regions = Map.getInstance().getRegions();
        String[] returnArray = new String[2];
        returnArray[0] = "Mix-Zone(x):Node ID:" + mixNode.getNodeID() + ":Radius:" + mixNode.getMixZoneRadius();
        returnArray[1] = "Mix-Zone(y):Node ID:" + mixNode.getNodeID() + ":Radius:" + mixNode.getMixZoneRadius();
        Street[] streets;
        Street street;
        double y1 = -1;
        double x1 = -1;
        double y2 = -1;
        double x2 = -1;
        double m = -1;
        double t = -1;
        double xNode = -1;
        double yNode = -1;
        double r = -1;
        double result = -1;
        double result1 = -1;
        double result2 = -1;
        ArrayList<Street> blackList = new ArrayList<Street>();
        ArrayList<Street> blackList2 = new ArrayList<Street>();
        boolean blackListed = false;
        boolean blackListed2 = false;
        for (int i = 0; i < regions.length; i++) {
            for (int j = 0; j < regions[i].length; j++) {
                streets = regions[i][j].getStreets();
                for (int k = 0; k < streets.length; k++) {
                    street = streets[k];
                    blackListed = false;
                    blackListed2 = false;
                    if (street.getLanesCount() > 1) {
                        if (blackList.contains(street)) {
                            blackListed = true;
                        }
                        if (!blackListed)
                            blackList.add(street);
                    }
                    if (blackList2.contains(street)) {
                        blackListed2 = true;
                    }
                    if (!blackListed2)
                        blackList2.add(street);
                    if (!blackListed && !blackListed2) {
                        y1 = street.getEndNode().getY();
                        x1 = street.getEndNode().getX();
                        y2 = street.getStartNode().getY();
                        x2 = street.getStartNode().getX();
                        xNode = mixNode.getX();
                        yNode = mixNode.getY();
                        m = ((y1 - y2) / (x1 - x2));
                        t = y1 - (m * x1);
                        r = mixNode.getMixZoneRadius();
                        if ((-yNode * yNode + 2 * xNode * yNode * m - xNode * xNode * m * m + r * r + m * m * r * r + 2 * yNode * t - 2 * xNode * m * t - t * t) < 0) {
                        } else if ((-yNode * yNode + 2 * xNode * yNode * m - xNode * xNode * m * m + r * r + m * m * r * r + 2 * yNode * t - 2 * xNode * m * t - t * t) > 0) {
                            result1 = (xNode + yNode * m - m * t - Math.sqrt(-yNode * yNode + 2 * xNode * yNode * m - xNode * xNode * m * m + r * r + m * m * r * r + 2 * yNode * t - 2 * xNode * m * t - t * t)) / (1 + m * m);
                            result2 = (xNode + yNode * m - m * t + Math.sqrt(-yNode * yNode + 2 * xNode * yNode * m - xNode * xNode * m * m + r * r + m * m * r * r + 2 * yNode * t - 2 * xNode * m * t - t * t)) / (1 + m * m);
                            if ((result1 >= x1 && result1 <= x2) || (result1 <= x1 && result1 >= x2)) {
                                returnArray[0] += ":" + String.valueOf((int) result1);
                                returnArray[1] += ":" + String.valueOf((int) ((m * result1 + t)));
                            }
                            if ((result2 >= x1 && result2 <= x2) || (result2 <= x1 && result2 >= x2)) {
                                returnArray[0] += ":" + String.valueOf((int) result2);
                                returnArray[1] += ":" + String.valueOf((int) ((m * result2 + t)));
                            }
                        } else if ((-yNode * yNode + 2 * xNode * yNode * m - xNode * xNode * m * m + r * r + m * m * r * r + 2 * yNode * t - 2 * xNode * m * t - t * t) == 0) {
                            result = (xNode + yNode * m - m * t) / (1 + m * m);
                            double dx1 = xNode - x1;
                            double dy1 = yNode - y1;
                            double distanceSquared1 = dx1 * dx1 + dy1 * dy1;
                            double dx2 = xNode - x2;
                            double dy2 = yNode - y2;
                            double distanceSquared2 = dx2 * dx2 + dy2 * dy2;
                            if (((distanceSquared1 <= r * r) || (distanceSquared2 <= r * r)) && ((distanceSquared1 > r * r) || (distanceSquared2 > r * r))) {
                                returnArray[0] += ":" + String.valueOf((int) result);
                                returnArray[1] += ":" + String.valueOf((int) (((m * result + t))));
                            }
                        }
                    }
                }
            }
        }
        return returnArray;
    }

    public void clearMixZones() {
        for (Node mixNode : mixZoneNodes_) deleteMixZone(mixNode);
        mixZoneNodes_ = new Node[0];
    }

 

    public void cleanVehicles() {
        vehicles_ = new ArrayList<Vehicle>(1);
        for (int i = 0; i < streets_.length; ++i) {
            streets_[i].clearLanes();
        }
        vehiclesDirty_ = true;
    }

    public void clearTrafficLights() {
        for (int i = 0; i < nodes_.length; i++) {
            if (nodes_[i].getJunction() != null) {
                nodes_[i].getJunction().delTrafficLight();
            }
        }
    }

    public Node[] getNodes() {
        return nodes_;
    }

    public Node[] getMixZoneNodes() {
        return mixZoneNodes_;
    }

 

    public Street[] getStreets() {
        return streets_;
    }

    public ArrayList<Vehicle> getVehicleArrayList() {
        return vehicles_;
    }

    public Vehicle[] getVehicleArray() {
        if (vehiclesDirty_) {
            if (vehicles_.size() == 0)
                vehiclesArray_ = EMPTY_VEHICLE;
            else
                vehiclesArray_ = vehicles_.toArray(EMPTY_VEHICLE);
            vehiclesDirty_ = false;
        }
        return vehiclesArray_;
    }

    public void createBacklink(WorkerThread thread, int numberinThread) {
        thread_ = thread;
        numberInThread_ = numberinThread;
    }

    public int getLeftBoundary() {
        return leftBoundary_;
    }

    public int getRightBoundary() {
        return rightBoundary_;
    }

    public int getUpperBoundary() {
        return upperBoundary_;
    }

    public int getLowerBoundary() {
        return lowerBoundary_;
    }
}
