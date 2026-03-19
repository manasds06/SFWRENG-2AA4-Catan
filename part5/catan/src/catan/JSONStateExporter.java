package catan;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class JSONStateExporter implements Observer {

	private String outputFilePath;

	public JSONStateExporter(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	@Override
	public void update(Board b, List<Agent> agents) {
		writeToJson(b, agents);
	}

	private void writeToJson(Board b, List<Agent> agents) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");

		sb.append("  \"agents\": [\n");
		for (int i = 0; i < agents.size(); i++) {
			Agent a = agents.get(i);
			sb.append("    {\n");
			sb.append("      \"id\": ").append(a.getId()).append(",\n");
			sb.append("      \"victoryPoints\": ").append(a.getVictoryPoints()).append(",\n");
			sb.append("      \"hand\": {");
			ResourceType[] types = ResourceType.values();
			for (int j = 0; j < types.length; j++) {
				sb.append("\"").append(types[j]).append("\": ").append(a.getHandCount(types[j]));
				if (j < types.length - 1) sb.append(", ");
			}
			sb.append("}\n");
			sb.append("    }");
			if (i < agents.size() - 1) sb.append(",");
			sb.append("\n");
		}
		sb.append("  ],\n");

		Robber robber = b.getRobber();
		int robberHexId = (robber != null) ? robber.getCurrentHex().getId() : -1;

		sb.append("  \"hexes\": [\n");
		List<Hex> hexList = new ArrayList<>(b.getHexes().values());
		hexList.sort(Comparator.comparingInt(Hex::getId));
		for (int i = 0; i < hexList.size(); i++) {
			Hex h = hexList.get(i);
			sb.append("    { \"id\": ").append(h.getId())
			  .append(", \"terrain\": \"").append(h.getTerrain()).append("\"")
			  .append(", \"token\": ").append(h.getNumberToken())
			  .append(", \"robber\": ").append(h.getId() == robberHexId)
			  .append(" }");
			if (i < hexList.size() - 1) sb.append(",");
			sb.append("\n");
		}
		sb.append("  ],\n");

		sb.append("  \"robberHex\": ").append(robberHexId).append(",\n");

		sb.append("  \"roads\": [\n");
		List<Edge> roads = new ArrayList<>();
		for (Edge e : b.getEdges().values()) {
			if (e.getOwner() != null) roads.add(e);
		}
		for (int i = 0; i < roads.size(); i++) {
			Edge e = roads.get(i);
			sb.append("    { \"a\": ").append(e.getA().getId())
			  .append(", \"b\": ").append(e.getB().getId())
			  .append(", \"owner\": \"").append(getColorForAgent(e.getOwner().getId())).append("\" }");
			if (i < roads.size() - 1) sb.append(",");
			sb.append("\n");
		}
		sb.append("  ],\n");

		sb.append("  \"buildings\": [\n");
		List<Node> buildings = new ArrayList<>();
		for (Node n : b.getNodes().values()) {
			if (n.getOwner() != null && n.getBuilding() != BuildingType.NONE) buildings.add(n);
		}
		for (int i = 0; i < buildings.size(); i++) {
			Node n = buildings.get(i);
			sb.append("    { \"node\": ").append(n.getId())
			  .append(", \"owner\": \"").append(getColorForAgent(n.getOwner().getId()))
			  .append("\", \"type\": \"").append(n.getBuilding()).append("\" }");
			if (i < buildings.size() - 1) sb.append(",");
			sb.append("\n");
		}
		sb.append("  ]\n");

		sb.append("}\n");

		try {
			File outFile = new File(outputFilePath);
			File parentDir = outFile.getParentFile();
			if (parentDir != null) {
				parentDir.mkdirs();
			}
			FileWriter fw = new FileWriter(outFile);
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			System.err.println("JSONStateExporter: failed to write " + outputFilePath + " — " + e.getMessage());
		}
	}

	private String getColorForAgent(int id) {
		switch (id) {
			case 0: return "BLUE";
			case 1: return "RED";
			case 2: return "ORANGE";
			case 3: return "WHITE";
			default: return "BLUE";
		}
	}
}


