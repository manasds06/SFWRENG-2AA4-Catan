package catan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Core simulation engine for the Catan game.
 * Manages the game loop: dice rolls, resource distribution, agent actions,
 * and win condition checks. Supports up to 8192 rounds with 4 agents (R1.4).
 */
public class CatanSimulator {
    private int currentRound;
    private int maxRounds;
    private Board board;
    private Dice dice;
    private List<Agent> agents;
    private MoveValidator rules;

    public CatanSimulator(int maxRounds) {
        this.currentRound = 0;
        this.maxRounds = Math.min(maxRounds, 8192); // R1.4: max 8192 rounds
        this.board = new Board();
        this.dice = new Dice();
        this.agents = new ArrayList<>();
        this.rules = new MoveValidator();
    }

    /**
     * Runs the full simulation: board setup, initial placement, game rounds.
     * Terminates when an agent reaches 10 VP or maxRounds is exceeded (R1.4, R1.5).
     */
    public void runSimulation() {
        // R1.1: Set up the board
        board.setupMap();

        // R1.2: Create 4 randomly acting agents
        for (int i = 1; i <= 4; i++) {
            agents.add(new RandomAgent(i));
        }

        // Initial placement phase (snake draft: 1→2→3→4, then 4→3→2→1)
        System.out.println("\n=== INITIAL PLACEMENT PHASE ===");
        initialPlacement();

        // R1.3/R1.4: Main game loop
        System.out.println("\n=== GAME BEGINS ===");
        while (currentRound < maxRounds) {
            currentRound++;
            System.out.println("\n--- Round " + currentRound + " ---");

            for (Agent agent : agents) {
                runTurn(agent);

                // R1.5: Check win condition after each turn
                if (checkWinCondition()) {
                    printRoundSummary();
                    System.out.println("\n*** GAME OVER! Agent " + agent.getId()
                            + " wins with " + agent.getVictoryPoints() + " VP! ***");
                    return;
                }
            }

            // R1.7: Print victory points at the end of each round
            printRoundSummary();
        }

        System.out.println("\n*** GAME OVER: Maximum rounds (" + maxRounds + ") reached. ***");
        printRoundSummary();
    }

    /**
     * Handles the initial placement phase (snake draft).
     * Each player places 2 settlements and 2 roads without resource cost.
     * Second settlement grants initial resources from adjacent hexes.
     */
    private void initialPlacement() {
        Random rng = new Random();

        // First round: players 1 → 2 → 3 → 4
        for (int i = 0; i < agents.size(); i++) {
            placeInitialSettlementAndRoad(agents.get(i), rng, false);
        }

        // Second round (reverse): players 4 → 3 → 2 → 1
        // Second settlement gives initial resources
        for (int i = agents.size() - 1; i >= 0; i--) {
            placeInitialSettlementAndRoad(agents.get(i), rng, true);
        }
    }

    /**
     * Places one initial settlement and one adjacent road for the given agent.
     * 
     * @param grantResources if true, grants resources from adjacent hexes
     */
    private void placeInitialSettlementAndRoad(Agent agent, Random rng, boolean grantResources) {
        // Pick a random available node for settlement
        List<Node> availableNodes = board.getAvailableNodesForInitialSettlement();
        if (availableNodes.isEmpty()) {
            System.out.println("  Agent " + agent.getId() + ": No available nodes for settlement!");
            return;
        }

        Node settlementNode = availableNodes.get(rng.nextInt(availableNodes.size()));
        board.placeInitialSettlement(agent, settlementNode);
        logAction(0, agent.getId(), "places initial settlement on node " + settlementNode.getId());

        // Grant resources from adjacent hexes (for second settlement only)
        if (grantResources) {
            for (Hex hex : board.getHexesAdjacentToNode(settlementNode)) {
                if (hex.getTerrain() != TerrainType.DESERT) {
                    ResourceType resource = terrainToResource(hex.getTerrain());
                    if (resource != null) {
                        agent.addResource(resource, 1);
                    }
                }
            }
        }

        // Place a road on an adjacent edge
        List<Edge> adjacentEdges = board.getEdgesAdjacentToNode(settlementNode);
        List<Edge> freeEdges = new ArrayList<>();
        for (Edge e : adjacentEdges) {
            if (e.getOwner() == null)
                freeEdges.add(e);
        }

        if (!freeEdges.isEmpty()) {
            Edge roadEdge = freeEdges.get(rng.nextInt(freeEdges.size()));
            board.placeInitialRoad(agent, roadEdge);
            logAction(0, agent.getId(), "places initial road on edge " + roadEdge.getId());
        }
    }

    /**
     * Runs a single turn for the given agent.
     * 1. Roll dice
     * 2. Distribute resources (unless 7)
     * 3. R1.8: If hand > 7 cards, try to build until under limit
     * 4. Agent chooses an action
     */
    public void runTurn(Agent a) {
        // Roll dice
        int rollValue = dice.roll2d6();
        logAction(currentRound, a.getId(), "rolls " + rollValue);

        // Distribute resources (7 = no production, no robber)
        if (rollValue != 7) {
            board.distributeResources(rollValue);
        }

        // R1.8: Agents with > 7 cards must try to spend them
        while (a.checkHandLimit()) {
            Action action = a.chooseAction(board);
            if (action == null)
                break; // Can't build anything
            if (action.execute(board, a)) {
                logAction(currentRound, a.getId(), action.describe());
            } else {
                break;
            }
        }

        // Normal action: agent chooses one action
        Action action = a.chooseAction(board);
        if (action != null) {
            if (action.execute(board, a)) {
                logAction(currentRound, a.getId(), action.describe());
            }
        }
    }

    /**
     * Checks if any agent has reached 10 or more victory points (R1.4).
     */
    public boolean checkWinCondition() {
        for (Agent a : agents) {
            if (a.getVictoryPoints() >= 10) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prints the current victory points for all agents (R1.7).
     */
    public void printRoundSummary() {
        StringBuilder sb = new StringBuilder("  VP: ");
        for (Agent a : agents) {
            sb.append("Agent ").append(a.getId()).append("=").append(a.getVictoryPoints());
            sb.append(" (").append(a.getHand().getTotalCards()).append(" cards)");
            sb.append("  ");
        }
        System.out.println(sb.toString());
    }

    /**
     * Logs an action to the console (R1.7).
     */
    public void logAction(int round, int playerId, String action) {
        System.out.println("  [Round " + round + "] Agent " + playerId + ": " + action);
    }

    /** Converts terrain type to resource type (utility for initial placement). */
    private ResourceType terrainToResource(TerrainType terrain) {
        switch (terrain) {
            case WOOD:
                return ResourceType.WOOD;
            case BRICK:
                return ResourceType.BRICK;
            case SHEEP:
                return ResourceType.SHEEP;
            case WHEAT:
                return ResourceType.WHEAT;
            case ORE:
                return ResourceType.ORE;
            default:
                return null;
        }
    }
}
