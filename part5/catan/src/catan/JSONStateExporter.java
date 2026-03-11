package catan;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Observer that serialises the current game state to a JSON file after every turn.
 * The output feeds the instructor-provided visualizer (R2.2 / R2.3).
 *
 * No external JSON library is used; the file is built with a StringBuilder.
 */
public class JSONStateExporter implements Observer {

	private String outputFilePath;

	public JSONStateExporter(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	@Override
	public void update(Board b, List<Agent> agents) {
		writeToJson(b, agents);
	}

	/**
	 * Writes game state to {@code outputFilePath}, overwriting any previous content.
	 * Format:
	 * <pre>
	 * {
	 *   "agents": [ { "id":0, "victoryPoints":2, "hand":{"WOOD":1,...} }, ... ],
	 *   "hexes":  [ { "id":0, "terrain":"WOOD", "token":6, "robber":false }, ... ],
	 *   "robberHex": 18
	 * }
	 * </pre>
	 */
	private void writeToJson(Board b, List<Agent> agents) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");

		// ── agents ──────────────────────────────────────────────────────────
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

		// ── hexes ────────────────────────────────────────────────────────────
		Robber robber = b.getRobber();
		int robberHexId = (robber != null) ? robber.getCurrentHex().getId() : -1;

		sb.append("  \"hexes\": [\n");
		List<Hex> hexList = new java.util.ArrayList<>(b.getHexes().values());
		hexList.sort(java.util.Comparator.comparingInt(Hex::getId));
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

		// ── robberHex ────────────────────────────────────────────────────────
		sb.append("  \"robberHex\": ").append(robberHexId).append("\n");
		sb.append("}\n");

		// Write to file
		try (FileWriter fw = new FileWriter(outputFilePath)) {
			fw.write(sb.toString());
		} catch (IOException e) {
			System.err.println("JSONStateExporter: failed to write to " + outputFilePath + " — " + e.getMessage());
		}
	}
}

