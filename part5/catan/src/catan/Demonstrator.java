package catan;

/**
 * Entry point. Two modes:
 *   java -cp bin catan.Demonstrator
 *   java -cp bin catan.Demonstrator --human
 *
 * Config: src/catan/config.txt (turns: 1-8192), src/catan/map.txt
 */
public class Demonstrator {

	public static void main(String[] args) {
		boolean humanMode = args.length > 0 && args[0].equalsIgnoreCase("--human");
		if (humanMode) runHumanDemo();
		else           runAutomatedDemo();
	}

	private static void runAutomatedDemo() {
		System.out.println("Starting automated simulation...");

		CatanSimulator sim = new CatanSimulator("src/catan/config.txt");
		sim.attach(new ConsoleLogger());
		sim.attach(new JSONStateExporter("2aa4-2026-base/assignments/visualize/state.json"));
		sim.runSimulation();

		System.out.println("\n[Done] See state.json for the final state.");
	}

	private static void runHumanDemo() {
		System.out.println("Starting human step-forward simulation...");
		System.out.println("\nCommands on your turn:");
		System.out.println("  roll                        – roll dice and collect resources");
		System.out.println("  list                        – show board state and your hand");
		System.out.println("  build settlement <nodeId>   – place a settlement");
		System.out.println("  build city <nodeId>         – upgrade settlement to city");
		System.out.println("  build road <id1> <id2>      – place a road between two nodes");
		System.out.println("  undo                        – undo your last build action");
		System.out.println("  redo                        – redo a previously undone action");
		System.out.println("  go                          – end your turn\n");
		System.out.println("Between every turn type 'go' to step forward.\n");

		CatanSimulator sim = new CatanSimulator("src/catan/config.txt", true);
		sim.attach(new ConsoleLogger());
		sim.attach(new JSONStateExporter("2aa4-2026-base/assignments/visualize/state.json"));
		sim.runSimulation();

		System.out.println("\n[Done] Human simulation complete.");
	}
}


