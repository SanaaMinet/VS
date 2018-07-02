package vanetsim.routing.A_Star;

import java.util.ArrayDeque;
import vanetsim.map.Node;
import vanetsim.map.Street;
import vanetsim.routing.RoutingAlgorithm;

public final class A_Star_Algorithm implements RoutingAlgorithm {

    public A_Star_Algorithm() {
    }

    private A_Star_Node computeRoute(int mode, int direction, Street startStreet, double startStreetPos, int targetX, int targetY, Street targetStreet, double targetStreetPos,  int additionalVar) {
        int distanceAdd;
        long dx, dy;
        double f, g, distance;
        boolean target1found = false, target2found = false, endNodeMayBeDestination;
        int speed;
        Node tmpNode;
        int i, j;
        A_Star_Node currentNode, successor, startNode;
        Street[] outgoingStreets;
        Street tmpStreet;
        A_Star_Queue openList = new A_Star_Queue();
        int[] tmp = new int[1];
        A_Star_LookupTable<Node, A_Star_Node> lookupTable = A_Star_LookupTableFactory.getTable(tmp);
        int counter = tmp[0];
        if (targetStreet.isOneway())
            endNodeMayBeDestination = false;
        else
            endNodeMayBeDestination = true;
        if (direction > -1) {
            startNode = lookupTable.get(startStreet.getStartNode());
            if (startNode == null) {
                startNode = new A_Star_Node(startStreet.getStartNode(), counter);
                lookupTable.put(startStreet.getStartNode(), startNode);
            } else {
                startNode.reset(counter);
                startNode.setPredecessor(null);
            }
            if (mode == 0) {
                startNode.setF(startStreetPos);
                startNode.setG(startStreetPos);
            } else {
                if (startStreet.getSpeed() > additionalVar)
                    speed = additionalVar;
                else
                    speed = startStreet.getSpeed();
                startNode.setF(startStreetPos / speed);
                startNode.setG(startStreetPos / speed);
            }
            startNode.setInOpenList(true);
            openList.add(startNode);
        }
        if (direction < 1) {
            startNode = lookupTable.get(startStreet.getEndNode());
            if (startNode == null) {
                startNode = new A_Star_Node(startStreet.getEndNode(), counter);
                lookupTable.put(startStreet.getEndNode(), startNode);
            } else {
                startNode.reset(counter);
                startNode.setPredecessor(null);
            }
            if (mode == 0) {
                startNode.setF(startStreet.getLength() - startStreetPos);
                startNode.setG(startStreet.getLength() - startStreetPos);
            } else {
                if (startStreet.getSpeed() > additionalVar)
                    speed = additionalVar;
                else
                    speed = startStreet.getSpeed();
                startNode.setF((startStreet.getLength() - startStreetPos) / speed);
                startNode.setG((startStreet.getLength() - startStreetPos) / speed);
            }
            startNode.setInOpenList(true);
            openList.add(startNode);
        }
        do {
            currentNode = openList.poll();
            if (endNodeMayBeDestination && currentNode.getRealNode() == targetStreet.getEndNode()) {
                if (target1found) {
                    A_Star_LookupTableFactory.putTable(counter, lookupTable);
                    return currentNode;
                } else {
                    if (mode == 0)
                        f = currentNode.getF() + (targetStreet.getLength() - targetStreetPos);
                    else {
                        if (targetStreet.getSpeed() > additionalVar)
                            speed = additionalVar;
                        else
                            speed = targetStreet.getSpeed();
                        f = currentNode.getF() + ((targetStreet.getLength() - targetStreetPos) / speed);
                    }
                    currentNode.setF(f);
                    currentNode.setG(f);
                    openList.add(currentNode);
                    target1found = true;
                }
            } else if (currentNode.getRealNode() == targetStreet.getStartNode()) {
                if (target2found) {
                    A_Star_LookupTableFactory.putTable(counter, lookupTable);
                    return currentNode;
                } else {
                    if (mode == 0)
                        f = currentNode.getF() + targetStreetPos;
                    else {
                        if (targetStreet.getSpeed() > additionalVar)
                            speed = additionalVar;
                        else
                            speed = targetStreet.getSpeed();
                        f = currentNode.getF() + (targetStreetPos / speed);
                    }
                    currentNode.setF(f);
                    currentNode.setG(f);
                    openList.add(currentNode);
                    target2found = true;
                }
            } else {
                outgoingStreets = currentNode.getRealNode().getOutgoingStreets();
                for (i = 0; i < outgoingStreets.length; ++i) {
                    tmpStreet = outgoingStreets[i];
                    tmpNode = tmpStreet.getStartNode();
                    if (tmpNode == currentNode.getRealNode())
                        tmpNode = tmpStreet.getEndNode();
                    successor = lookupTable.get(tmpNode);
                    if (successor == null) {
                        successor = new A_Star_Node(tmpNode, counter);
                        lookupTable.put(tmpNode, successor);
                    } else {
                        if (successor.getCounter() != counter)
                            successor.reset(counter);
                    }
                    if (successor.isInClosedList() == false) {
                        distanceAdd = 0;
 
                        dx = targetX - tmpNode.getX();
                        dy = targetY - tmpNode.getY();
                        distance = distanceAdd + Math.sqrt(dx * dx + dy * dy);
                        if (mode == 0) {
                            g = currentNode.getG() + tmpStreet.getLength();
                            f = g + distance;
                        } else {
                            if (tmpStreet.getSpeed() > additionalVar)
                                g = currentNode.getG() + (tmpStreet.getLength() / additionalVar);
                            else
                                g = currentNode.getG() + (tmpStreet.getLength() / tmpStreet.getSpeed());
                            f = g + (distance / additionalVar);
                        }
                        if (!successor.isInOpenList()) {
                            successor.setPredecessor(currentNode);
                            successor.setF(f);
                            successor.setG(g);
                            successor.setInOpenList(true);
                            openList.add(successor);
                        } else if (successor.getF() > f) {
                            if (target1found && successor.getRealNode() == targetStreet.getEndNode()) {
                                if (mode == 0)
                                    f = g + (targetStreet.getLength() - targetStreetPos);
                                else {
                                    if (targetStreet.getSpeed() > additionalVar)
                                        speed = additionalVar;
                                    else
                                        speed = targetStreet.getSpeed();
                                    f = g + ((targetStreet.getLength() - targetStreetPos) / speed);
                                }
                                if (successor.getF() > f) {
                                    successor.setPredecessor(currentNode);
                                    successor.setF(f);
                                    successor.setG(g);
                                    openList.signalDecreasedF(successor);
                                }
                            } else if (target2found && successor.getRealNode() == targetStreet.getStartNode()) {
                                if (mode == 0)
                                    f = g + targetStreetPos;
                                else {
                                    if (targetStreet.getSpeed() > additionalVar)
                                        speed = additionalVar;
                                    else
                                        speed = targetStreet.getSpeed();
                                    f = g + (targetStreetPos / speed);
                                }
                                if (successor.getF() > f) {
                                    successor.setPredecessor(currentNode);
                                    successor.setF(f);
                                    successor.setG(g);
                                    openList.signalDecreasedF(successor);
                                }
                            } else {
                                successor.setPredecessor(currentNode);
                                successor.setF(f);
                                successor.setG(g);
                                openList.signalDecreasedF(successor);
                            }
                        }
                    }
                }
                currentNode.setInClosedList(true);
                currentNode.setInOpenList(false);
            }
        } while (!openList.isEmpty());
        A_Star_LookupTableFactory.putTable(counter, lookupTable);
        return null;
    }

    public ArrayDeque<Node> getRouting(int mode, int direction, int startX, int startY, Street startStreet, double startStreetPos, int targetX, int targetY, Street targetStreet, double targetStreetPos, int additionalVar) {
        A_Star_Node curNode = computeRoute(mode, direction, startStreet, startStreetPos, targetX, targetY, targetStreet, targetStreetPos, additionalVar);
        ArrayDeque<Node> result = new ArrayDeque<Node>(255);
        while (curNode != null) {
            result.addFirst(curNode.getRealNode());
            curNode = curNode.getPredecessor();
        }
        return result;
    }

 
}
