package vanetsim.routing;

import java.util.ArrayDeque;
import vanetsim.map.Node;
import vanetsim.map.Street;

public interface RoutingAlgorithm {

    public abstract ArrayDeque<Node> getRouting(int mode, int direction, int startX, int startY, Street startStreet, double startStreetPos, int targetX, int targetY, Street targetStreet, double targetStreetPos, int additionalVar);
}
