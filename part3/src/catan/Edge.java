package catan;

/**
 * Represents a path (edge) between two nodes on the Catan board.
 * Roads are placed on edges. Each edge connects exactly two nodes.
 */
public class Edge {
    private int id;
    private Agent owner;
    private Node a;
    private Node b;

    public Edge(int id, Node a, Node b) {
        this.id = id;
        this.owner = null;
        this.a = a;
        this.b = b;
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

    public Node getA() {
        return a;
    }

    public Node getB() {
        return b;
    }
}
