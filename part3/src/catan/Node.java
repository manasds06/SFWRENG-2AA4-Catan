package catan;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an intersection (vertex) on the Catan board.
 * Nodes are where settlements and cities can be placed.
 * Each node tracks its owner, building type, and incident edges.
 */
public class Node {
    private int id;
    private Agent owner;
    private BuildingType building;
    private List<Edge> edges;

    public Node(int id) {
        this.id = id;
        this.owner = null;
        this.building = BuildingType.NONE;
        this.edges = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public Agent getOwner() {
        return owner;
    }

    public void setOwner(Agent owner) {
        this.owner = owner;
    }

    public BuildingType getBuilding() {
        return building;
    }

    public void setBuilding(BuildingType building) {
        this.building = building;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void addEdge(Edge edge) {
        if (!edges.contains(edge)) {
            edges.add(edge);
        }
    }

    /**
     * Returns all nodes adjacent to this node (connected by an edge).
     */
    public List<Node> getNeighbors() {
        List<Node> neighbors = new ArrayList<>();
        for (Edge e : edges) {
            if (e.getA() == this) {
                neighbors.add(e.getB());
            } else {
                neighbors.add(e.getA());
            }
        }
        return neighbors;
    }
}
