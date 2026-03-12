package catan;

/**
 * Entry point. Two modes:
 *   java -cp bin catan.Demonstrator          -- automated (4 bots)
 *   java -cp bin catan.Demonstrator --human  -- step-forward (you are Player 3)
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
		System.out.println("╔══════════════════════════════════════════╗");
		System.out.println("║  DEMO 1 — Automated simulation           ║");
		System.out.println("║  4 RandomAgents, JSON export enabled     ║");
		System.out.println("╚══════════════════════════════════════════╝\n");

		CatanSimulator sim = new CatanSimulator("src/catan/config.txt");
		sim.attach(new ConsoleLogger());
		sim.attach(new JSONStateExporter("state.json"));
		sim.runSimulation();

		System.out.println("\n[Done] See state.json for the final state.");
	}

	private static void runHumanDemo() {
		System.out.println("╔══════════════════════════════════════════╗");
		System.out.println("║  DEMO 2 — Human step-forward mode       ║");
		System.out.println("║  You are Player 3 (last in turn order)  ║");
		System.out.println("╚══════════════════════════════════════════╝");
		System.out.println("\nCommands on your turn:");
		System.out.println("  roll                        – roll dice and collect resources");
		System.out.println("  list                        – show board state and your hand");
		System.out.println("  build settlement <nodeId>   – place a settlement");
		System.out.println("  build city <nodeId>         – upgrade settlement to city");
		System.out.println("  build road <id1> <id2>      – place a road between two nodes");
		System.out.println("  go                          – end your turn\n");
		System.out.println("Between every turn type 'go' to step forward.\n");

		CatanSimulator sim = new CatanSimulator("src/catan/config.txt", true);
		sim.attach(new ConsoleLogger());
		sim.attach(new JSONStateExporter("state.json"));
		sim.runSimulation();

		System.out.println("\n[Done] Human simulation complete.");
	}
}


