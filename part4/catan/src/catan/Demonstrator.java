package catan;

/**
 * Demonstrator: entry point for running and showcasing the Catan simulator.
 *
 * Config loading: reads turns from config.txt (format: "turns: int [1-8192]").
 * Board setup: 19 hexes, 54 nodes, edges derived from hex corners (standard Catan layout).
 * Setup phase: each of the 4 RandomAgents places 2 settlements + 2 roads for free (snake order).
 *   The second settlement grants starting resources from each adjacent hex.
 * Main game loop: each round every agent rolls 2d6, collects resources, then
 *   optionally builds a settlement, road, or upgrades a settlement to a city.
 * R1.8: agents with more than 7 cards are forced to attempt a build ([hand limit] tag).
 * Win condition: first agent to reach 10 victory points wins.
 * Output format: [RoundNumber] / [PlayerID]: [Action description]
 *   VP totals are printed after every complete round.
 */
public class Demonstrator {
	public static void main(String[] args) {
		String config = "src/catan/config.txt";

		// Full simulation: turn count loaded from config.txt
		System.out.println("=== Catan Simulator: Full Run (rounds from config.txt) ===");
		CatanSimulator sim = new CatanSimulator(config);
		sim.runSimulation();

		// Short 5-round demo highlighting key mechanics
		System.out.println();
		System.out.println("=== Catan Simulator: 5-Round Demo ===");
		System.out.println("Key mechanics demonstrated:");
		System.out.println("  * Setup phase: free placements, starting resource grants");
		System.out.println("  * Dice roll -> resource distribution");
		System.out.println("  * Actions: BuildSettlement, BuildRoad, UpgradeToCity");
		System.out.println("  * R1.8: agents with >7 cards forced to build ([hand limit] tag)");
		System.out.println("  * Win condition: 10 VP ends the game");
		System.out.println();
		CatanSimulator demo = new CatanSimulator(config, 5);
		demo.runSimulation();
	}
}
