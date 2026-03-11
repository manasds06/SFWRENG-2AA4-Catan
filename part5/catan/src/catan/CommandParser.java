package catan;

public class CommandParser {

	public Action parse(String input, Board board) {
		if (input == null || input.isBlank()) return null;

		String[] tokens = input.trim().split("\\s+");
		String cmd = tokens[0].toLowerCase();

		switch (cmd) {
			case "roll":
				return new RollDiceAction();

			case "go":
				return new EndTurnAction();

			case "list":
				return new ListBoardAction();

			case "build":
				return parseBuild(tokens, board);

			default:
				System.out.println("Unknown command: \"" + input + "\"");
				System.out.println("Commands: roll | go | list | build settlement <id> | build city <id> | build road <id1> <id2>");
				return null;
		}
	}

	private Action parseBuild(String[] tokens, Board board) {
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
					if (tokens.length < 4) {
						System.out.println("Usage: build road <fromNodeId> <toNodeId>");
						return null;
					}
					int fromId = Integer.parseInt(tokens[2]);
					int toId   = Integer.parseInt(tokens[3]);
					// Find the edge whose endpoints match these two node IDs
					for (Edge e : board.getEdges().values()) {
						int a = e.getA().getId();
						int b = e.getB().getId();
						if ((a == fromId && b == toId) || (a == toId && b == fromId)) {
							return new BuildRoadAction(e);
						}
					}
					System.out.println("No edge found between node " + fromId + " and node " + toId);
					return null;
				}
				default:
					System.out.println("Unknown build type: \"" + type + "\"  (settlement | city | road)");
					return null;
			}
		} catch (NumberFormatException e) {
			System.out.println("Node IDs must be integers.");
			return null;
		}
	}
}

