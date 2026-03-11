package catan;

import java.util.ArrayList;
import java.util.List;

public class Hex {
	private int id;
	TerrainType terrain;
	int numberToken;
	List<Node> corners;

	public Hex(int id, TerrainType terrain, int numberToken) {
		this.id = id;
		this.terrain = terrain;
		this.numberToken = numberToken;
		this.corners = new ArrayList<>();
	}

	public int getId() { return id; }
	public TerrainType getTerrain() { return terrain; }
	public int getNumberToken() { return numberToken; }
	public List<Node> getCorners() { return corners; }
}
