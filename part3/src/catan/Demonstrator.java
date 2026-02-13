package catan;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Demonstrator class for the Catan Simulator (R1.9).
 *
 * This class contains the main entry point for the simulation.
 * It reads configuration from config.properties and runs a demonstration
 * of the Catan simulator with 4 randomly acting agents.
 *
 * The simulation follows the rules outlined in the SFWRENG 2AA4 assignment:
 * - 19-hex board with hard-coded terrain and number tokens (R1.1)
 * - 4 random agents playing the game (R1.2)
 * - Standard Catan rules minus trading, robber, dev cards, harbours (R1.3)
 * - Configurable max rounds up to 8192 (R1.4)
 * - Halts on 10 VP or max rounds (R1.5)
 * - Distance rule, road connectivity, city upgrade invariants (R1.6)
 * - Actions and VP printed to console each round (R1.7)
 * - Hand limit enforcement: agents with >7 cards try to build (R1.8)
 */
public class Demonstrator {

    /**
     * Main entry point. Reads maxRounds from config.properties and runs the
     * simulation.
     *
     * To run:
     * javac -d bin src/catan/*.java
     * java -cp bin catan.Demonstrator
     *
     * The config.properties file should be in the working directory and contain:
     * maxRounds=100
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   CATAN SIMULATOR - SFWRENG 2AA4");
        System.out.println("========================================");
        System.out.println();

        // Read configuration (R1.4)
        int maxRounds = 100; // default
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config.properties"));
            maxRounds = Integer.parseInt(props.getProperty("maxRounds", "100"));
            System.out.println("Configuration loaded: maxRounds = " + maxRounds);
        } catch (IOException e) {
            System.out.println("config.properties not found, using default maxRounds = " + maxRounds);
        } catch (NumberFormatException e) {
            System.out.println("Invalid maxRounds in config, using default = " + maxRounds);
        }

        // Validate maxRounds (R1.4: maximum 8192)
        if (maxRounds > 8192) {
            System.out.println("Warning: maxRounds capped at 8192.");
            maxRounds = 8192;
        }

        System.out.println();

        // Create and run the simulation (R1.2, R1.3)
        // The simulator will:
        // 1. Set up the board with 19 hexes, 54 nodes, 72 edges (R1.1)
        // 2. Create 4 RandomAgents that make random but legal moves (R1.2)
        // 3. Run the initial placement phase (snake draft: 1→4, then 4→1)
        // 4. Run game rounds until 10 VP or maxRounds (R1.4, R1.5)
        // 5. Print actions and VP summaries to console (R1.7)
        CatanSimulator simulator = new CatanSimulator(maxRounds);
        simulator.runSimulation();

        System.out.println();
        System.out.println("========================================");
        System.out.println("   SIMULATION COMPLETE");
        System.out.println("========================================");
    }
}
