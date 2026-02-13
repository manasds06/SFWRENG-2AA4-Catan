package catan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the Catan game board.
 * Contains 19 hex tiles, 54 nodes (intersections), and 72 edges (paths).
 * Handles board setup, resource distribution, and building placement.
 *
 * The board layout uses a standard Catan arrangement with rows of 3-4-5-4-3
 * hexes. Terrain and number tokens are hard-coded for consistency (R1.1).
 */
public class Board {
    private Map<Integer, Hex> hexes;
    private Map<Integer, Node> nodes;
    private Map<Integer, Edge> edges;

    // Hard-coded terrain types for each hex (R1.1)
    // Distribution: 4 WOOD, 3 BRICK, 4 SHEEP, 4 WHEAT, 3 ORE, 1 DESERT
    private static final TerrainType[] TERRAIN_LAYOUT = {
            TerrainType.WOOD, TerrainType.SHEEP, TerrainType.WHEAT, // row 0
            TerrainType.BRICK, TerrainType.ORE, TerrainType.WOOD, TerrainType.SHEEP, // row 1
            TerrainType.WHEAT, TerrainType.BRICK, TerrainType.DESERT,
            TerrainType.ORE, TerrainType.WOOD, // row 2
            TerrainType.SHEEP, TerrainType.WHEAT, TerrainType.BRICK, TerrainType.WOOD, // row 3
            TerrainType.ORE, TerrainType.SHEEP, TerrainType.WHEAT // row 4
    };

    // Number tokens for each hex (0 = no token, i.e. desert)
    // Standard set: 2,3,3,4,4,5,5,6,6,8,8,9,9,10,10,11,11,12
    private static final int[] NUMBER_TOKENS = {
            2, 3, 3, // row 0
            4, 4, 5, 5, // row 1
            6, 6, 0, 8, 8, // row 2 (hex 9 is desert)
            9, 9, 10, 10, // row 3
            11, 11, 12 // row 4
    };

    public Board() {
        this.hexes = new HashMap<>();
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
    }

    public Map<Integer, Hex> getHexes() {
        return hexes;
    }

    public Map<Integer, Node> getNodes() {
        return nodes;
    }

    public Map<Integer, Edge> getEdges() {
        return edges;
    }

    /**
     * Sets up the board: creates all nodes, edges, and hexes with their adjacency.
     * Uses axial coordinate-based computation to build the standard Catan topology.
     *
     * Algorithm:
     * 1. Place 19 hexes at axial coordinates
     * 2. Compute each hex's 6 corner positions using pointy-top hex geometry
     * 3. Deduplicate shared corners to produce 54 unique nodes
     * 4. Create edges between adjacent corners of each hex (72 unique edges)
     */
    public void setupMap() {
        // Standard Catan hex positions in axial coordinates (q, r)
        int[][] hexPositions = {
                { 0, -2 }, { 1, -2 }, { 2, -2 }, // row 0: 3 hexes
                { -1, -1 }, { 0, -1 }, { 1, -1 }, { 2, -1 }, // row 1: 4 hexes
                { -2, 0 }, { -1, 0 }, { 0, 0 }, { 1, 0 }, { 2, 0 }, // row 2: 5 hexes
                { -2, 1 }, { -1, 1 }, { 0, 1 }, { 1, 1 }, // row 3: 4 hexes
                { -2, 2 }, { -1, 2 }, { 0, 2 } // row 4: 3 hexes
        };

        // Corner offsets for pointy-top hex (corner i at angle 60*i - 30 degrees)
        double[][] cornerOffsets = new double[6][2];
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60.0 * i - 30.0);
            cornerOffsets[i][0] = Math.cos(angle);
            cornerOffsets[i][1] = Math.sin(angle);
        }

        // Compute unique node positions from hex corners
        double sqrt3 = Math.sqrt(3.0);
        List<double[]> uniquePositions = new ArrayList<>();
        int[][] hexCorners = new int[19][6];

        for (int h = 0; h < 19; h++) {
            int q = hexPositions[h][0];
            int r = hexPositions[h][1];
            double cx = sqrt3 * (q + r / 2.0);
            double cy = 1.5 * r;

            for (int c = 0; c < 6; c++) {
                double px = cx + cornerOffsets[c][0];
                double py = cy + cornerOffsets[c][1];
                hexCorners[h][c] = findOrAddNode(uniquePositions, px, py);
            }
        }

        // Create Node objects for each unique position
        for (int i = 0; i < uniquePositions.size(); i++) {
            nodes.put(i, new Node(i));
        }

        // Create edges from adjacent corners of each hex
        int edgeId = 0;
        Map<String, Edge> edgeMap = new HashMap<>();

        for (int h = 0; h < 19; h++) {
            for (int c = 0; c < 6; c++) {
                int n1 = hexCorners[h][c];
                int n2 = hexCorners[h][(c + 1) % 6];
                String key = Math.min(n1, n2) + "-" + Math.max(n1, n2);
                if (!edgeMap.containsKey(key)) {
                    Edge edge = new Edge(edgeId, nodes.get(n1), nodes.get(n2));
                    edgeMap.put(key, edge);
                    edges.put(edgeId, edge);
                    nodes.get(n1).addEdge(edge);
                    nodes.get(n2).addEdge(edge);
                    edgeId++;
                }
            }
        }

        // Create Hex objects with terrain, number tokens, and corner references
        for (int h = 0; h < 19; h++) {
            Hex hex = new Hex(h, TERRAIN_LAYOUT[h], NUMBER_TOKENS[h]);
            for (int c = 0; c < 6; c++) {
                hex.addCorner(nodes.get(hexCorners[h][c]));
            }
            hexes.put(h, hex);
        }

        System.out.println("Board setup complete: " + nodes.size() + " nodes, "
                + edges.size() + " edges, " + hexes.size() + " hexes.");
    }

    /**
     * Finds an existing node at the given coordinates or adds a new one.
     * Uses epsilon comparison to handle floating point imprecision.
     */
    private int findOrAddNode(List<double[]> positions, double x, double y) {
        double epsilon = 0.01;
        for (int i = 0; i < positions.size(); i++) {
            if (Math.abs(positions.get(i)[0] - x) < epsilon &&
                    Math.abs(positions.get(i)[1] - y) < epsilon) {
                return i;
            }
        }
        positions.add(new double[] { x, y });
        return positions.size() - 1;
    }

    /**
     * Distributes resources to all agents with settlements/cities adjacent to
     * hexes matching the given dice roll value.
     * Settlements receive 1 resource, cities receive 2 resources.
     */
    public void distributeResources(int rollValue) {
        for (Hex hex : hexes.values()) {
            if (hex.getNumberToken() == rollValue && hex.getTerrain() != TerrainType.DESERT) {
                ResourceType resource = terrainToResource(hex.getTerrain());
                if (resource == null)
                    continue;

                for (Node corner : hex.getCorners()) {
                    if (corner.getOwner() != null) {
                        int amount = (corner.getBuilding() == BuildingType.CITY) ? 2 : 1;
                        corner.getOwner().addResource(resource, amount);
                    }
                }
            }
        }
    }

    /** Converts a terrain type to the corresponding resource type. */
    private ResourceType terrainToResource(TerrainType terrain) {
        switch (terrain) {
            case WOOD:
                return ResourceType.WOOD;
            case BRICK:
                return ResourceType.BRICK;
            case SHEEP:
                return ResourceType.SHEEP;
            case WHEAT:
                return ResourceType.WHEAT;
            case ORE:
                return ResourceType.ORE;
            default:
                return null;
        }
    }

    /**
     * Returns all nodes where the given agent can legally place a settlement.
     * Requires: empty node, distance rule satisfied, connected to agent's road.
     */
    public List<Node> getAvailableNodesForSettlement(Agent a) {
        List<Node> available = new ArrayList<>();
        MoveValidator validator = new MoveValidator();
        for (Node n : nodes.values()) {
            if (validator.canPlaceSettlement(this, a, n)) {
                available.add(n);
            }
        }
        return available;
    }

    /**
     * Returns nodes available for initial settlement placement (distance rule only,
     * no road connectivity required).
     */
    public List<Node> getAvailableNodesForInitialSettlement() {
        List<Node> available = new ArrayList<>();
        for (Node n : nodes.values()) {
            if (n.getBuilding() != BuildingType.NONE)
                continue;
            boolean tooClose = false;
            for (Node neighbor : n.getNeighbors()) {
                if (neighbor.getBuilding() != BuildingType.NONE) {
                    tooClose = true;
                    break;
                }
            }
            if (!tooClose)
                available.add(n);
        }
        return available;
    }

    /** Returns all edges where the given agent can legally place a road. */
    public List<Edge> getAvailableEdgesForRoad(Agent a) {
        List<Edge> available = new ArrayList<>();
        MoveValidator validator = new MoveValidator();
        for (Edge e : edges.values()) {
            if (validator.canPlaceRoad(this, a, e)) {
                available.add(e);
            }
        }
        return available;
    }

    /** Returns edges adjacent to a specific node. */
    public List<Edge> getEdgesAdjacentToNode(Node n) {
        return n.getEdges();
    }

    /**
     * Checks if the given agent can upgrade a settlement to a city on the given
     * node.
     */
    public boolean canUpgradeToCity(Agent a, Node n) {
        return new MoveValidator().canUpgradeToCity(this, a, n);
    }

    /**
     * Places a settlement for the given agent. Deducts resources, awards 1 VP.
     * 
     * @return true if successful
     */
    public boolean placeSettlement(Agent a, Node n) {
        if (!new MoveValidator().canPlaceSettlement(this, a, n))
            return false;
        a.pay(Cost.settlementCost());
        n.setOwner(a);
        n.setBuilding(BuildingType.SETTLEMENT);
        a.addVictoryPoints(1);
        return true;
    }

    /**
     * Places an initial settlement (no cost, no road connectivity required).
     * 
     * @return true if successful
     */
    public boolean placeInitialSettlement(Agent a, Node n) {
        if (n.getBuilding() != BuildingType.NONE)
            return false;
        for (Node neighbor : n.getNeighbors()) {
            if (neighbor.getBuilding() != BuildingType.NONE)
                return false;
        }
        n.setOwner(a);
        n.setBuilding(BuildingType.SETTLEMENT);
        a.addVictoryPoints(1);
        return true;
    }

    /**
     * Places a road for the given agent. Deducts resources.
     * 
     * @return true if successful
     */
    public boolean placeRoad(Agent a, Edge e) {
        if (!new MoveValidator().canPlaceRoad(this, a, e))
            return false;
        a.pay(Cost.roadCost());
        e.setOwner(a);
        return true;
    }

    /**
     * Places an initial road (no cost, must be adjacent to agent's settlement).
     * 
     * @return true if successful
     */
    public boolean placeInitialRoad(Agent a, Edge e) {
        if (e.getOwner() != null)
            return false;
        if (e.getA().getOwner() != a && e.getB().getOwner() != a)
            return false;
        e.setOwner(a);
        return true;
    }

    /**
     * Upgrades a settlement to a city. Deducts resources, awards +1 VP.
     * 
     * @return true if successful
     */
    public boolean upgradeToCity(Agent a, Node n) {
        if (!new MoveValidator().canUpgradeToCity(this, a, n))
            return false;
        a.pay(Cost.cityCost());
        n.setBuilding(BuildingType.CITY);
        a.addVictoryPoints(1);
        return true;
    }

    /** Returns all hexes adjacent to a given node (for initial resource grants). */
    public List<Hex> getHexesAdjacentToNode(Node n) {
        List<Hex> adjacent = new ArrayList<>();
        for (Hex hex : hexes.values()) {
            if (hex.getCorners().contains(n)) {
                adjacent.add(hex);
            }
        }
        return adjacent;
    }
}
