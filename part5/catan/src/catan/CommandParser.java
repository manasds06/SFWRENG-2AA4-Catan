package catan;

public class CommandParser {

	private Board board;
	private CatanSimulator context;

	// Set by HumanAgent before each parse call
	public void setBoard(Board board) { this.board = board; }
	public void setContext(CatanSimulator context) { this.context = context; }

	public Action parse(String input) {
		if (input == null || input.isBlank()) return null;

		String[] tokens = input.trim().split("\\s+");
		String cmd = tokens[0].toLowerCase();

		switch (cmd) {
			case "roll":  return new RollDiceAction(context);
			case "go":    return new EndTurnAction();
			case "list":  return new ListBoardAction();
			case "build": return parseBuild(tokens);
			default:
				System.out.println("Unknown command: \"" + input + "\"");
				System.out.println("Commands: roll | go | list | build settlement <id> | build city <id> | build road <id1> <id2>");
				return null;
		}
	}

	private Action parseBuild(String[] tokens) {
		if (tokens.length < 3) {
			System.out.println("Usage: build [settlement|city|road] <id> [<id2>]");
			return null;
		}

		String type = tokens[1].toLowerCase();

		try {
			switch (type) {
				case "settlement": {
					int nodeId = Integer.parseInt(tokens[2]);
					Node n = board.getNodes().get(nodeId);
					if (n == null) { System.out.println("Invalid node ID: " + nodeId); return null; }
					return new BuildSettlementAction(n);
				}
				case "city": {
					int nodeId = Integer.parseInt(tokens[2]);
					Node n = board.getNodes().get(nodeId);
					if (n == null) { System.out.println("Invalid node ID: " + nodeId); return null; }
					return new UpgradeToCityAction(n);
				}
				case "road": {
					if (tokens.length < 4) { System.out.println("Usage: build road <fromNodeId> <toNodeId>"); return null; }
					int fromId = Integer.parseInt(tokens[2]);
					int toId   = Integer.parseInt(tokens[3]);
					// Find edge by matching both endpoint node IDs
					for (Edge e : board.getEdges().values()) {
						int a = e.getA().getId(), b = e.getB().getId();
						if ((a == fromId && b == toId) || (a == toId && b == fromId)) return new BuildRoadAction(e);
					}
					System.out.println("No edge between node " + fromId + " and node " + toId);
					return null;
				}
				default:
					System.out.println("Unknown build type: \"" + type + "\" (settlement | city | road)");
					return null;
			}
		} catch (NumberFormatException e) {
			System.out.println("Node IDs must be integers.");
			return null;
		}
	}
}
