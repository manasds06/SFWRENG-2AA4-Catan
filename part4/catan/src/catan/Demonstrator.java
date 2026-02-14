package catan;

/**
 * Runs the Catan simulator and outputs the actions taken and the final scores to console.
 * 
 * To change the number of rounds: edit src/catan/config.txt (turns: 1-8192).
 * To change the board layout:     edit src/catan/map.txt.
 *
 * Compile and run from the catan/ directory:
 *   javac -d bin src/catan/*.java
 *   java -cp bin catan.Demonstrator
 */
public class Demonstrator {
	public static void main(String[] args) {
		CatanSimulator sim = new CatanSimulator("src/catan/config.txt");
		sim.runSimulation();
	}
}
