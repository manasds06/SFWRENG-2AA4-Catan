package catan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Board {
	private Map<Integer, Hex> hexes;
	private Map<Integer, Node> nodes;
	private Map<Integer, Edge> edges;

	// Map layout is defined in map.txt — edit that file to change the board
	private static final String MAP_FILE = "src/catan/map.txt";

	public void setupMap() {
		hexes = new HashMap<>();
		nodes = new HashMap<>();
		edges = new HashMap<>();

		int nodeCount = 54;
		List<int[]> hexDefs = new ArrayList<>(); // each entry: [id, token, n0..n5]
		List<TerrainType> hexTerrains = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(MAP_FILE))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) continue;

				if (line.startsWith("node_count:")) {
					nodeCount = Integer.parseInt(line.substring("node_count:".length()).trim());

				} else if (line.startsWith("hex:")) {
					String[] parts = line.substring("hex:".length()).trim().split("\\s+");
					// parts: id terrain token n0 n1 n2 n3 n4 n5
					int id      = Integer.parseInt(parts[0]);
					TerrainType terrain = TerrainType.valueOf(parts[1]);
					int token   = Integer.parseInt(parts[2]);
					int[] def   = new int[8]; // [id, token, n0..n5]
					def[0] = id;
					def[1] = token;
					for (int i = 0; i < 6; i++) def[2 + i] = Integer.parseInt(parts[3 + i]);
					hexDefs.add(def);
					hexTerrains.add(terrain);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to load map file: " + MAP_FILE, e);
		}

		// Create all nodes
		for (int i = 0; i < nodeCount; i++) {
			nodes.put(i, new Node(i));
		}

		// Create hexes and wire corners
		for (int i = 0; i < hexDefs.size(); i++) {
			int[] def = hexDefs.get(i);
			int id = def[0];
			Hex hex = new Hex(id, hexTerrains.get(i), def[1]);
			for (int c = 0; c < 6; c++) {
				hex.getCorners().add(nodes.get(def[2 + c]));
			}
			hexes.put(id, hex);
		}

		// Derive edges from hex corners (each pair of consecutive corners shares an edge)
		Set<String> seen = new LinkedHashSet<>();
		List<int[]> edgeList = new ArrayList<>();
		for (int[] def : hexDefs) {
			for (int i = 0; i < 6; i++) {
				int a = def[2 + i];
				int b = def[2 + (i + 1) % 6];
				int lo = Math.min(a, b);
				int hi = Math.max(a, b);
				if (seen.add(lo + "-" + hi)) {
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
				if (neighbor.building != BuildingType.NONE) { distOk = false; break; }
			}
			if (!distOk) continue;
			// Road connectivity required
			boolean connected = false;
			for (Edge e : n.edges) {
				if (e.owner == a) { connected = true; break; }
			}
			if (!connected) continue;
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
				if (neighbor.building != BuildingType.NONE) { distOk = false; break; }
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
			boolean connected = (na.owner == a || nb.owner == a);
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
