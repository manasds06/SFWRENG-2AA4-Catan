package catan;

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
	public boolean undo(Board b, Agent a) { return false; }

	@Override
	public boolean isUndoable() { return false; }

	@Override
	public String describe() {
		return "Listed board and hand";
	}

	@Override
	public void accept(ActionVisitor v) {
		// Human-mode action — not evaluated by AI visitors
	}
}


