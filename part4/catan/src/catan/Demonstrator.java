package catan;

/**
 * Demonstrator: entry point for running the Catan simulator.
 *
 * Config loading: reads turns from config.txt (format: "turns: int [1-8192]").
 * Map loading: reads map from map.txt (format: "hexes: ...\nedges: ...\nnodes: ...").
 * 
 */
public class Demonstrator {
	public static void main(String[] args) {
		String config = "src/catan/config.txt";

		System.out.println("=== Catan Simulator: Full Run (rounds from config.txt) ===");
		CatanSimulator sim = new CatanSimulator(config);
		sim.runSimulation();
	}
}
