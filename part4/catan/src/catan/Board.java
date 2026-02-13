package catan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
	private Map<Integer, Hex> hexes;
	private Map<Integer, Node> nodes;
	private Map<Integer, Edge> edges;

	// Each hex's 6 corner node IDs (in order)
	private static final int[][] HEX_CORNERS = {
		// Hex 0 (center): WHEAT/9
		{7, 8, 9, 19, 18, 17},
		// Hex 1: WOOD/11
		{0, 1, 2, 8, 7, 6},
		// Hex 2: SHEEP/12
		{2, 3, 4, 10, 9, 8},
		// Hex 3: BRICK/5
		{4, 5, 6, 12, 11, 10},
		// Hex 4: ORE/6
		{17, 18, 19, 29, 28, 27},
		// Hex 5: WHEAT/4
		{9, 10, 11, 21, 20, 19},
		// Hex 6: SHEEP/10
		{11, 12, 13, 23, 22, 21},
		// Hex 7: WOOD/3
		{1, 2, 3, 14, 13, 12},
		// Hex 8: BRICK/11
		{3, 4, 5, 16, 15, 14},
		// Hex 9: ORE/4
		{5, 6, 7, 18, 17, 16},
		// Hex 10: WHEAT/8
		{19, 20, 21, 31, 30, 29},
		// Hex 11: WOOD/8
		{21, 22, 23, 33, 32, 31},
		// Hex 12: SHEEP/9
		{23, 24, 25, 35, 34, 33},
		// Hex 13: BRICK/5
		{13, 14, 15, 25, 24, 23},
		// Hex 14: WOOD/6
		{15, 16, 17, 27, 26, 25},
		// Hex 15: ORE/3
		{29, 30, 31, 41, 40, 39},
		// Hex 16: SHEEP/10
		{31, 32, 33, 43, 42, 41},
		// Hex 17: WHEAT/2
		{33, 34, 35, 45, 44, 43},
		// Hex 18: DESERT/0
		{25, 26, 27, 37, 36, 35},
	};

	// Terrain and token for each hex
	private static final TerrainType[] HEX_TERRAIN = {
		TerrainType.WHEAT,  // 0
		TerrainType.WOOD,   // 1
		TerrainType.SHEEP,  // 2
		TerrainType.BRICK,  // 3
		TerrainType.ORE,    // 4
		TerrainType.WHEAT,  // 5
		TerrainType.SHEEP,  // 6
		TerrainType.WOOD,   // 7
		TerrainType.BRICK,  // 8
		TerrainType.ORE,    // 9
		TerrainType.WHEAT,  // 10
		TerrainType.WOOD,   // 11
		TerrainType.SHEEP,  // 12
		TerrainType.BRICK,  // 13
		TerrainType.WOOD,   // 14
		TerrainType.ORE,    // 15
		TerrainType.SHEEP,  // 16
		TerrainType.WHEAT,  // 17
		TerrainType.DESERT, // 18
	};

	private static final int[] HEX_TOKENS = {
		9, 11, 12, 5, 6, 4, 10, 3, 11, 4, 8, 8, 9, 5, 6, 3, 10, 2, 0
	};

	public void setupMap() {
		hexes = new HashMap<>();
		nodes = new HashMap<>();
		edges = new HashMap<>();

		// Create all 54 nodes
		for (int i = 0; i < 54; i++) {
			nodes.put(i, new Node(i));
		}

		// Create all hexes
		for (int h = 0; h < 19; h++) {
			Hex hex = new Hex(h, HEX_TERRAIN[h], HEX_TOKENS[h]);
			for (int c : HEX_CORNERS[h]) {
				hex.getCorners().add(nodes.get(c));
			}
			hexes.put(h, hex);
		}

		// Create edges and wire up node adjacency
		// Use canonical Catan adjacency: build edges from hex corners
		// Each hex has 6 edges between consecutive corners (wrap around)
		java.util.Set<String> edgeSet = new java.util.LinkedHashSet<>();
		List<int[]> edgeList = new ArrayList<>();
		for (int h = 0; h < 19; h++) {
			int[] corners = HEX_CORNERS[h];
			for (int i = 0; i < 6; i++) {
				int a = corners[i];
				int b = corners[(i + 1) % 6];
				int lo = Math.min(a, b);
				int hi = Math.max(a, b);
				String key = lo + "-" + hi;
				if (edgeSet.add(key)) {
					edgeList.add(new int[]{lo, hi});
				}
			}
		}

		for (int i = 0; i < edgeList.size(); i++) {
			int[] pair = edgeList.get(i);
			Node na = nodes.get(pair[0]);
			Node nb = nodes.get(pair[1]);
			Edge e = new Edge(i, na, nb);
			edges.put(i, e);
			na.edges.add(e);
			nb.edges.add(e);
		}
	}

	public void distributeResources(int rollValue) {
		for (Hex hex : hexes.values()) {
			if (hex.numberToken != rollValue) continue;
			if (hex.terrain == TerrainType.DESERT) continue;
			ResourceType res = terrainToResource(hex.terrain);
			for (Node node : hex.corners) {
				if (node.owner == null) continue;
				int amount = (node.building == BuildingType.CITY) ? 2 : 1;
				node.owner.addResource(res, amount);
			}
		}
	}

	private ResourceType terrainToResource(TerrainType t) {
		switch (t) {
			case WOOD:  return ResourceType.WOOD;
			case BRICK: return ResourceType.BRICK;
			case SHEEP: return ResourceType.SHEEP;
			case WHEAT: return ResourceType.WHEAT;
			case ORE:   return ResourceType.ORE;
			default:    return null;
		}
	}

	public List<Node> getAvailableNodesForSettlement(Agent a) {
		List<Node> result = new ArrayList<>();
		for (Node n : nodes.values()) {
			if (n.building != BuildingType.NONE) continue;
			// Distance rule: no adjacent node may have a building
			boolean distOk = true;
			for (Edge e : n.edges) {
				Node neighbor = (e.getA() == n) ? e.getB() : e.getA();
				if (neighbor.building != BuildingType.NONE) {
					distOk = false;
					break;
				}
			}
			if (!distOk) continue;
			// Connectivity: node must be reachable by agent's road (skip during setup when a==null)
			if (a != null) {
				boolean connected = false;
				for (Edge e : n.edges) {
					if (e.owner == a) { connected = true; break; }
				}
				if (!connected) continue;
			}
			result.add(n);
		}
		return result;
	}

	public List<Node> getAvailableNodesForSetup(Agent a) {
		List<Node> result = new ArrayList<>();
		for (Node n : nodes.values()) {
			if (n.building != BuildingType.NONE) continue;
			boolean distOk = true;
			for (Edge e : n.edges) {
				Node neighbor = (e.getA() == n) ? e.getB() : e.getA();
				if (neighbor.building != BuildingType.NONE) {
					distOk = false;
					break;
				}
			}
			if (distOk) result.add(n);
		}
		return result;
	}

	public List<Edge> getAvailableEdgesForRoad(Agent a) {
		List<Edge> result = new ArrayList<>();
		for (Edge e : edges.values()) {
			if (e.owner != null) continue;
			Node na = e.getA();
			Node nb = e.getB();
			boolean connected = false;
			// Connected if endpoint is agent's settlement
			if (na.owner == a || nb.owner == a) { connected = true; }
			// Or if endpoint is adjacent to agent's road
			if (!connected) {
				for (Edge adj : na.edges) {
					if (adj != e && adj.owner == a) { connected = true; break; }
				}
			}
			if (!connected) {
				for (Edge adj : nb.edges) {
					if (adj != e && adj.owner == a) { connected = true; break; }
				}
			}
			if (connected) result.add(e);
		}
		return result;
	}

	public boolean canUpgradeToCity(Agent a, Node n) {
		return n.owner == a && n.building == BuildingType.SETTLEMENT;
	}

	public boolean placeSettlement(Agent a, Node n) {
		n.owner = a;
		n.building = BuildingType.SETTLEMENT;
		a.addVictoryPoints(1);
		return true;
	}

	public boolean placeRoad(Agent a, Edge e) {
		e.owner = a;
		return true;
	}

	public boolean upgradeToCity(Agent a, Node n) {
		n.building = BuildingType.CITY;
		a.addVictoryPoints(1);
		return true;
	}

	public Map<Integer, Hex> getHexes() { return hexes; }
	public Map<Integer, Node> getNodes() { return nodes; }
	public Map<Integer, Edge> getEdges() { return edges; }
}
