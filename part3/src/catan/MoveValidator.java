package catan;

/**
 * Validates whether moves are legal according to Catan rules (R1.6).
 * Checks distance rule, road connectivity, and city upgrade eligibility.
 */
public class MoveValidator {

    /**
     * Checks whether the agent can place a settlement on the given node.
     * Rules:
     * - Node must be empty (no existing building)
     * - Distance rule: no adjacent node can have any building
     * - Connectivity: node must be adjacent to a road owned by this agent
     * (not required during initial placement — use the Board method directly)
     */
    public boolean canPlaceSettlement(Board b, Agent a, Node n) {
        // Node must be unoccupied
        if (n.getBuilding() != BuildingType.NONE) {
            return false;
        }
        // Distance rule: no neighbor may have any building (R1.6)
        for (Node neighbor : n.getNeighbors()) {
            if (neighbor.getBuilding() != BuildingType.NONE) {
                return false;
            }
        }
        // Must be adjacent to one of the agent's roads
        boolean connectedToRoad = false;
        for (Edge e : n.getEdges()) {
            if (e.getOwner() == a) {
                connectedToRoad = true;
                break;
            }
        }
        return connectedToRoad;
    }

    /**
     * Checks whether the agent can place a road on the given edge.
     * Rules:
     * - Edge must be unoccupied
     * - Edge must be connected to one of the agent's existing roads or settlements
     */
    public boolean canPlaceRoad(Board b, Agent a, Edge e) {
        // Edge must be unoccupied
        if (e.getOwner() != null) {
            return false;
        }
        // Must be connected to agent's network (road, settlement, or city)
        Node nodeA = e.getA();
        Node nodeB = e.getB();

        // Check if either endpoint has the agent's building
        if ((nodeA.getOwner() == a) || (nodeB.getOwner() == a)) {
            return true;
        }
        // Check if either endpoint connects to the agent's road
        for (Edge adj : nodeA.getEdges()) {
            if (adj != e && adj.getOwner() == a) {
                return true;
            }
        }
        for (Edge adj : nodeB.getEdges()) {
            if (adj != e && adj.getOwner() == a) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the agent can upgrade a settlement to a city on the given
     * node.
     * Rules:
     * - Node must have a SETTLEMENT owned by this agent
     */
    public boolean canUpgradeToCity(Board b, Agent a, Node n) {
        return (n.getOwner() == a && n.getBuilding() == BuildingType.SETTLEMENT);
    }
}
