package catan;

public class ListBoardAction extends Action {

	@Override
	public boolean execute(Board b, Agent a) {
		// TODO: print a human-readable board summary to stdout
		System.out.println("=== Board State ===");
		System.out.println("Hexes: " + b.getHexes().size());
		System.out.println("Nodes: " + b.getNodes().size());
		System.out.println("Edges: " + b.getEdges().size());
		return true;
	}

	@Override
	public String describe() {
		return "Listed board";
	}
}
