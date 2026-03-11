package catan;

/**
 * Prints a human-readable snapshot of the board and the acting agent's hand.
 * Triggered by the "list" command (R2.1).
 */
public class ListBoardAction extends Action {

	@Override
	public boolean execute(Board b, Agent a) {
		System.out.println("=== Board State ===");
		System.out.println("  Hexes : " + b.getHexes().size());
		System.out.println("  Nodes : " + b.getNodes().size());
		System.out.println("  Edges : " + b.getEdges().size());
		Robber robber = b.getRobber();
		if (robber != null) {
			Hex rh = robber.getCurrentHex();
			System.out.println("  Robber: hex " + rh.getId() + " (" + rh.getTerrain() + ")");
		}
		System.out.println("=== Your Hand ===");
		System.out.println("  " + a.getHandSummary());
		return true;
	}

	@Override
	public String describe() {
		return "Listed board and hand";
	}
}

