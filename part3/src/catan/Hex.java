package catan;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a hexagonal tile on the Catan board.
 * Each hex has a terrain type, a number token (for resource production),
 * and references to its 6 corner nodes.
 */
public class Hex {
    private int id;
    private TerrainType terrain;
    private int numberToken;
    private List<Node> corners;

    public Hex(int id, TerrainType terrain, int numberToken) {
        this.id = id;
        this.terrain = terrain;
        this.numberToken = numberToken;
        this.corners = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public TerrainType getTerrain() {
        return terrain;
    }

    public int getNumberToken() {
        return numberToken;
    }

    public List<Node> getCorners() {
        return corners;
    }

    public void addCorner(Node node) {
        if (!corners.contains(node)) {
            corners.add(node);
        }
    }
}
