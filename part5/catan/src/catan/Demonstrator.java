package catan;

/**
 * Entry point for the Catan simulator (R2.6).
 *
 * Two demonstrations are available:
 *
 *   1. Automated simulation  (default)
 *      Runs a full game with 4 RandomAgents.
 *      A JSONStateExporter writes game state to game_state.json after every turn so
 *      the instructor visualizer (R2.2/R2.3) can track progress.
 *      A ConsoleLogger mirrors every update to stdout.
 *
 *   2. Human step-forward mode  (pass --human as the first argument)
 *      Runs a game with 3 RandomAgents and 1 HumanAgent (Player 3).
 *      The simulator pauses after each agent's turn and waits for a "go" command
 *      from the human player before advancing (R2.4).
 *      Human commands: roll | go | list | build settlement/city/road (R2.1).
 *
 * Configuration:
 *   - Number of rounds: edit src/catan/config.txt  (turns: 1-8192)
 *   - Board layout:     edit src/catan/map.txt
 *
 * Compile and run from the catan/ directory:
 *   javac -d bin src/catan/*.java
 *   java -cp bin catan.Demonstrator          # automated
 *   java -cp bin catan.Demonstrator --human  # human step-forward
 */
public class Demonstrator {

	public static void main(String[] args) {
		boolean humanMode = args.length > 0 && args[0].equalsIgnoreCase("--human");

		if (humanMode) {
			runHumanDemo();
		} else {
			runAutomatedDemo();
		}
	}

	// ─────────────────────────────────────────────────────────────────────────
	// Demo 1: Fully automated – 4 RandomAgents + Observer pattern in action
	// ─────────────────────────────────────────────────────────────────────────
	private static void runAutomatedDemo() {
		System.out.println("╔══════════════════════════════════════════╗");
		System.out.println("║  DEMO 1 — Automated simulation           ║");
		System.out.println("║  4 RandomAgents, JSON export enabled     ║");
		System.out.println("╚══════════════════════════════════════════╝\n");

		// Build the simulator (reads turn count from config.txt)
		CatanSimulator sim = new CatanSimulator("src/catan/config.txt");

		// Attach observers (Observer pattern):
		//   - ConsoleLogger prints a one-line summary each turn
		//   - JSONStateExporter writes game_state.json (feeds the visualizer)
		sim.attach(new ConsoleLogger());
		sim.attach(new JSONStateExporter("game_state.json"));

		// Run the full game. The state machine (R2.5) handles Robber turns
		// automatically whenever a 7 is rolled.
		sim.runSimulation();

		System.out.println("\n[Done] Automated simulation complete. See game_state.json for the final state.");
	}

	// ─────────────────────────────────────────────────────────────────────────
	// Demo 2: Human step-forward – 3 RandomAgents + 1 HumanAgent
	// ─────────────────────────────────────────────────────────────────────────
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

		// Build a simulator in step-forward mode:
		// Agents 0-2 are bots; Agent 3 is the human player.
		CatanSimulator sim = new CatanSimulator("src/catan/config.txt", true /* humanMode */);

		// Observers keep the human informed after every turn
		sim.attach(new ConsoleLogger());
		sim.attach(new JSONStateExporter("game_state.json"));

		sim.runSimulation();

		System.out.println("\n[Done] Human simulation complete.");
	}
}

