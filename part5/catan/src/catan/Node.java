package catan;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private int id;
	Agent owner;
	BuildingType building;
	public List<Edge> edges;

	public Node(int id) {
		this.id = id;
		this.owner = null;
		this.building = BuildingType.NONE;
		this.edges = new ArrayList<>();
	}

	public int getId() { return id; }
	public Agent getOwner() { return owner; }
	public BuildingType getBuilding() { return building; }
}
