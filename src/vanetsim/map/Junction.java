package vanetsim.map;

import vanetsim.gui.Renderer;
import vanetsim.scenario.LaneObject;
import vanetsim.scenario.Vehicle;

public final class Junction {

    private static final double JUNCTION_PASS_TIME_FREE = 1.5;

    private static final int JUNCTION_QUEUES_CLEANUP_INTERVAL = 1000;

    private static final int MAXIMUM_TIME_ON_JUNCTION = 1000;

    private static final Renderer renderer_ = Renderer.getInstance();

    private final Node node_;

    private final Street[] priorityStreets_;

    private Node[] rulesSourceNodes_ = null;

    private Node[] rulesTargetNodes_ = null;

    private int[] rulesPriorities_ = null;

    private JunctionQueue junctionQueuePriority3_ = new JunctionQueue();

    private JunctionQueue junctionQueuePriority4_ = new JunctionQueue();

    public Vehicle vehicleAllowedThisStep_ = null;

    public int vehicleAllowedSetTime_ = 0;

    private boolean vehicleOnJunction_ = false;

    private int vehicleOnJunctionSince_ = -1;

    private int nextJunctionQueueCleanUp_ = JUNCTION_QUEUES_CLEANUP_INTERVAL;

    public Junction(Node node, Street[] priorityStreets) {
        node_ = node;
        priorityStreets_ = priorityStreets;
    }

    public void addJunctionRule(Node startNode, Node targetNode, int priority) {
        Node[] newArray;
        int[] newArray2;
        if (rulesSourceNodes_ == null) {
            newArray = new Node[1];
            newArray[0] = startNode;
        } else {
            newArray = new Node[rulesSourceNodes_.length + 1];
            System.arraycopy(rulesSourceNodes_, 0, newArray, 0, rulesSourceNodes_.length);
            newArray[rulesSourceNodes_.length] = startNode;
        }
        rulesSourceNodes_ = newArray;
        if (rulesTargetNodes_ == null) {
            newArray = new Node[1];
            newArray[0] = targetNode;
        } else {
            newArray = new Node[rulesTargetNodes_.length + 1];
            System.arraycopy(rulesTargetNodes_, 0, newArray, 0, rulesTargetNodes_.length);
            newArray[rulesTargetNodes_.length] = targetNode;
        }
        rulesTargetNodes_ = newArray;
        if (rulesPriorities_ == null) {
            newArray2 = new int[1];
            newArray2[0] = priority;
        } else {
            newArray2 = new int[rulesPriorities_.length + 1];
            System.arraycopy(rulesPriorities_, 0, newArray2, 0, rulesPriorities_.length);
            newArray2[rulesPriorities_.length] = priority;
        }
        rulesPriorities_ = newArray2;
    }

    public int getJunctionPriority(Node startNode, Node targetNode) {
        int i, length = rulesPriorities_.length;
        for (i = 0; i < length; ++i) {
            if (rulesSourceNodes_[i] == startNode && rulesTargetNodes_[i] == targetNode)
                return rulesPriorities_[i];
        }
        return 5;
    }

    public synchronized void addWaitingVehicle(Vehicle vehicle, int priority) {
        int curTime = renderer_.getTimePassed();
        if (curTime > vehicleAllowedSetTime_) {
            vehicleAllowedSetTime_ = curTime;
            if (vehicleOnJunction_ && vehicleOnJunctionSince_ > curTime - MAXIMUM_TIME_ON_JUNCTION)
                vehicleAllowedThisStep_ = null;
            else {
                vehicleAllowedThisStep_ = junctionQueuePriority3_.getFirstVehicle();
                if (vehicleAllowedThisStep_ == null)
                    vehicleAllowedThisStep_ = junctionQueuePriority4_.getFirstVehicle();
            }
        }
        if (curTime >= nextJunctionQueueCleanUp_) {
            nextJunctionQueueCleanUp_ = curTime + JUNCTION_QUEUES_CLEANUP_INTERVAL;
            junctionQueuePriority3_.cleanUp();
            junctionQueuePriority4_.cleanUp();
        }
        if (priority == 3)
            junctionQueuePriority3_.addVehicle(vehicle);
        else
            junctionQueuePriority4_.addVehicle(vehicle);
    }

    public void allowOtherVehicle() {
        vehicleOnJunction_ = false;
    }

    public synchronized boolean canPassTrafficLight(Vehicle vehicle, Street tmpStreet, Node nextNode) {
        if (node_.isHasTrafficSignal_()) {
            if (tmpStreet.getStartNode().equals(node_) && tmpStreet.getStartNodeTrafficLightState() < 1)
                return true;
            else if (tmpStreet.getEndNode().equals(node_) && tmpStreet.getEndNodeTrafficLightState() < 1)
                return true;
        }
        return false;
    }

    public synchronized boolean canPassJunction(Vehicle vehicle, int priority, Node nextNode) {
        if (vehicleAllowedThisStep_ == vehicle) {
            Street[] outgoingStreets;
            Street tmpStreet, tmpStreet2 = null;
            LaneObject tmpLaneObject;
            Node nextNode2 = null;
            boolean tmpDirection;
            double distance, neededFreeDistance;
            int i;
            for (int j = 0; j < priorityStreets_.length; ++j) {
                tmpStreet = priorityStreets_[j];
                if (tmpStreet != vehicle.getCurStreet() && (priority != 4 || (tmpStreet.getStartNode() != nextNode && tmpStreet.getEndNode() != nextNode))) {
                    if (tmpStreet.getStartNode() == node_)
                        tmpDirection = false;
                    else
                        tmpDirection = true;
                    neededFreeDistance = JUNCTION_PASS_TIME_FREE * tmpStreet.getSpeed();
                    LaneObject previous = tmpStreet.getLastLaneObject(tmpDirection);
                    distance = tmpStreet.getLength();
                    if (previous != null) {
                        if (previous.getCurLane() == 1) {
                            if ((tmpDirection && tmpStreet.getLength() - previous.getCurPosition() < neededFreeDistance) || (!tmpDirection && previous.getCurPosition() < neededFreeDistance)) {
                                if (previous.getCurSpeed() > 400)
                                    return false;
                            }
                        } else {
                            tmpLaneObject = previous.getPrevious();
                            while (tmpLaneObject != null) {
                                if (tmpLaneObject.getCurLane() == 1) {
                                    if ((tmpDirection && tmpStreet.getLength() - tmpLaneObject.getCurPosition() < neededFreeDistance) || (!tmpDirection && tmpLaneObject.getCurPosition() < neededFreeDistance)) {
                                        return false;
                                    }
                                    break;
                                }
                                tmpLaneObject = tmpLaneObject.getPrevious();
                            }
                        }
                    }
                    if (tmpStreet.getLength() < neededFreeDistance) {
                        while (true) {
                            if (tmpDirection)
                                nextNode2 = tmpStreet.getStartNode();
                            else
                                nextNode2 = tmpStreet.getEndNode();
                            if (nextNode2.getCrossingStreetsCount() != 2)
                                break;
                            else
                                outgoingStreets = nextNode2.getCrossingStreets();
                            for (i = 0; i < outgoingStreets.length; ++i) {
                                tmpStreet2 = outgoingStreets[i];
                                if (tmpStreet2 != tmpStreet) {
                                    tmpStreet = tmpStreet2;
                                    if (tmpStreet2.getStartNode() == nextNode2) {
                                        tmpDirection = true;
                                        break;
                                    } else {
                                        tmpDirection = false;
                                        break;
                                    }
                                }
                            }
                            tmpLaneObject = tmpStreet.getLastLaneObject(tmpDirection);
                            while (tmpLaneObject != null) {
                                if (tmpLaneObject.getCurLane() == 1) {
                                    if ((tmpDirection && tmpStreet.getLength() - tmpLaneObject.getCurPosition() + distance < neededFreeDistance) || (!tmpDirection && tmpLaneObject.getCurPosition() + distance < neededFreeDistance)) {
                                        return false;
                                    }
                                    break;
                                }
                                tmpLaneObject = tmpLaneObject.getNext();
                            }
                            distance += tmpStreet.getLength();
                            if (distance > neededFreeDistance)
                                break;
                        }
                    }
                }
            }
            if (priority == 3)
                junctionQueuePriority3_.delFirstVehicle();
            else
                junctionQueuePriority4_.delFirstVehicle();
            vehicleOnJunction_ = true;
            vehicleOnJunctionSince_ = renderer_.getTimePassed();
            return true;
        } else
            return false;
    }

    public void delTrafficLight() {
        this.getNode().setTrafficLight_(null);
        this.getNode().setStreetHasException_(null);
        Street[] tmpStreets = node_.getCrossingStreets();
        for (int i = 0; i < tmpStreets.length; i++) {
            if (tmpStreets[i].getStartNode() == node_)
                tmpStreets[i].setStartNodeTrafficLightState(-1);
            else
                tmpStreets[i].setEndNodeTrafficLightState(-1);
        }
        node_.setHasTrafficSignal_(false);
    }

    public Node getNode() {
        return node_;
    }

    public Street[] getPriorityStreets() {
        return priorityStreets_;
    }
}
