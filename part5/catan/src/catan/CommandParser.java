package catan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

	// ── Compiled patterns (case-insensitive) ───────────────────────────────
	private static final Pattern P_ROLL       = Pattern.compile("^roll$", Pattern.CASE_INSENSITIVE);
	private static final Pattern P_GO         = Pattern.compile("^go$",   Pattern.CASE_INSENSITIVE);
	private static final Pattern P_LIST       = Pattern.compile("^list$", Pattern.CASE_INSENSITIVE);
	private static final Pattern P_SETTLEMENT = Pattern.compile("^build\\s+settlement\\s+(\\d+)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern P_CITY       = Pattern.compile("^build\\s+city\\s+(\\d+)$",       Pattern.CASE_INSENSITIVE);
	private static final Pattern P_ROAD       = Pattern.compile("^build\\s+road\\s+(\\d+)\\s+(\\d+)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern P_UNDO       = Pattern.compile("^undo$", Pattern.CASE_INSENSITIVE);
	private static final Pattern P_REDO       = Pattern.compile("^redo$", Pattern.CASE_INSENSITIVE);

	private Board board;
	private CatanSimulator context;

	public void setBoard(Board board)             { this.board   = board;   }
	public void setContext(CatanSimulator context) { this.context = context; }

	public boolean isUndo(String input) {
		return input != null && P_UNDO.matcher(input.trim()).matches();
	}

	public boolean isRedo(String input) {
		return input != null && P_REDO.matcher(input.trim()).matches();
	}

	public Action parse(String input) {
		if (input == null || input.isBlank()) {
			return null;
		}
		String s = input.trim();

		if (P_ROLL.matcher(s).matches()) {
			return new RollDiceAction(context);
		}
		if (P_GO.matcher(s).matches()) {
			return new EndTurnAction();
		}
		if (P_LIST.matcher(s).matches()) {
			return new ListBoardAction();
		}

		Matcher m;

		m = P_SETTLEMENT.matcher(s);
		if (m.matches()) {
			int nodeId = Integer.parseInt(m.group(1));
			Node n = board.getNodes().get(nodeId);
			if (n == null) {
				System.out.println("Invalid node ID: " + nodeId);
				return null;
			}
			return new BuildSettlementAction(n);
		}

		m = P_CITY.matcher(s);
		if (m.matches()) {
			int nodeId = Integer.parseInt(m.group(1));
			Node n = board.getNodes().get(nodeId);
			if (n == null) {
				System.out.println("Invalid node ID: " + nodeId);
				return null;
			}
			return new UpgradeToCityAction(n);
		}

		m = P_ROAD.matcher(s);
		if (m.matches()) {
			int fromId = Integer.parseInt(m.group(1));
			int toId   = Integer.parseInt(m.group(2));
			// Find edge whose endpoints match the two node IDs (either order)
			for (Edge e : board.getEdges().values()) {
				int a = e.getA().getId(), b = e.getB().getId();
				if ((a == fromId && b == toId) || (a == toId && b == fromId)) {
					return new BuildRoadAction(e);
				}
			}
			System.out.println("No edge between node " + fromId + " and node " + toId);
			return null;
		}

		System.out.println("Unknown command: \"" + s + "\"");
		System.out.println("Commands: roll | go | list | undo | redo | build settlement <id> | build city <id> | build road <id1> <id2>");
		return null;
	}
}
