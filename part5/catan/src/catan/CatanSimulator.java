package catan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;

public class CatanSimulator implements Subject {
	private int currentRound;
	private int maxRounds;
	private Board board;
	private Dice dice;
	private List<Agent> agents;
	private MoveValidator rules;
	private SecureRandom rng;
	private TurnState currentState;
	private List<Observer> observers;
	private boolean humanMode;
	private Agent currentAgent;
	private CommandHistory commandHistory;

	public CatanSimulator(String configPath) {
		this(configPath, false);
	}

	/**
	 * Constructs the simulator.
	 * @param configPath path to config.txt
	 * @param humanMode  when true, Player 3 becomes a HumanAgent and step-forward
	 *                   (WaitForGoState) is active between every turn (R2.4)
	 */
	public CatanSimulator(String configPath, boolean humanMode) {
		this.humanMode = humanMode;
		this.maxRounds = readTurnsFromConfig(configPath);
		this.currentRound = 0;
		this.board = new Board();
		this.dice = new Dice();
		this.rules = new MoveValidator();
		this.rng = new SecureRandom();
		this.agents = new ArrayList<>();
		this.observers = new ArrayList<>();
		this.commandHistory = new CommandHistory();

		// Create agents: mix of Strategic and Random AI, or human for seat 3
		agents.add(new StrategicAgent(0, rules));
		agents.add(new StrategicAgent(1, rules));
		agents.add(new RandomAgent(2, rules));
		if (humanMode) {
			agents.add(new HumanAgent(3));
			this.currentState = new WaitForGoState();
		} else {
			agents.add(new StrategicAgent(3, rules));
			this.currentState = new RollingPhase();
		}
	}

	private int readTurnsFromConfig(String configPath) {
		try (BufferedReader br = new BufferedReader(new FileReader(configPath))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("turns:")) {
					int val = Integer.parseInt(line.substring("turns:".length()).trim());
					if (val < 1) return 1;
					if (val > 8192) return 8192;
					return val;
				}
			}
		} catch (IOException | NumberFormatException e) {
			System.err.println("Warning: could not read config (" + e.getMessage() + "); defaulting to 100 rounds.");
		}
		return 100;
	}

	public void runSimulation() {
		board.setupMap();

		// Setup phase: each agent places 2 settlements and 2 roads
		for (Agent a : agents) {
			doSetupPlacement(a, false);
		}
		for (int i = agents.size() - 1; i >= 0; i--) {
			doSetupPlacement(agents.get(i), true);
		}

		// Main game loop
		for (currentRound = 1; currentRound <= maxRounds; currentRound++) {
			for (Agent a : agents) {
				runTurn(a);
			}
			printRoundSummary();
			if (checkWinCondition()) break;
		}
	}

	private void doSetupPlacement(Agent a, boolean grantResources) {
		List<Node> available = board.getAvailableNodesForSetup(a);
		if (!available.isEmpty()) {
			Node chosen = available.get(rng.nextInt(available.size()));
			chosen.owner = a;
			chosen.building = BuildingType.SETTLEMENT;
			a.addVictoryPoints(1);
			logAction(0, a.getId(), "Setup: placed settlement at node " + chosen.getId());

			if (grantResources) {
				// Second settlement yields one resource per adjacent non-desert hex
				for (Hex hex : board.getHexes().values()) {
					if (hex.terrain == TerrainType.DESERT) continue;
					if (hex.getCorners().contains(chosen)) {
						ResourceType res = terrainToResource(hex.terrain);
						if (res != null) a.addResource(res, 1);
					}
				}
			}

			List<Edge> adjacent = new ArrayList<>();
			for (Edge e : chosen.edges) {
				if (e.owner == null) adjacent.add(e);
			}
			if (!adjacent.isEmpty()) {
				Edge road = adjacent.get(rng.nextInt(adjacent.size()));
				road.owner = a;
				logAction(0, a.getId(), "Setup: placed road at edge " + road.getId());
			}
		}
	}

	private ResourceType terrainToResource(TerrainType t) {
		switch (t) {
			case WOOD:  return ResourceType.WOOD;
			case BRICK: return ResourceType.BRICK;
			case SHEEP: return ResourceType.SHEEP;
			case WHEAT: return ResourceType.WHEAT;
			case ORE:   return ResourceType.ORE;
			default:    return null;
		}
	}

	private void runTurn(Agent a) {
		this.currentAgent = a;
		currentState.handleTurn(this, a);
	}

	private boolean checkWinCondition() {
		for (Agent a : agents) {
			if (a.getVictoryPoints() >= 10) {
				System.out.println("Player " + a.getId() + " wins with " + a.getVictoryPoints() + " VP!");
				return true;
			}
		}
		return false;
	}

	private void printRoundSummary() {
		System.out.print("Round " + currentRound + " VP: ");
		for (Agent a : agents) {
			System.out.print("[P" + a.getId() + "=" + a.getVictoryPoints() + "] ");
		}
		System.out.println();
	}

	public void logAction(int round, int playerId, String action) {
		System.out.printf("[%d] / [%d]: %s%n", round, playerId, action);
	}

	// ── Subject ────────────────────────────────────────────────────

	@Override public void attach(Observer o)   { observers.add(o); }
	@Override public void detach(Observer o)   { observers.remove(o); }
	@Override public void notifyObservers()    { for (Observer o : observers) o.update(board, agents); }

	// ── State ──────────────────────────────────────────────────────

	public void setState(TurnState s)  { this.currentState = s; }
	public TurnState getState()        { return currentState; }

	public void stepForward() {
		if (!agents.isEmpty()) currentState.handleTurn(this, agents.get(currentRound % agents.size()));
	}

	// ── Undo / Redo (R3.1)

	void recordAction(Action a) {
		if (a.isUndoable()) {
			commandHistory.push(a);
		}
	}

	void resetHistory() {
		commandHistory = new CommandHistory();
	}

	public boolean undo() {
		Action action = commandHistory.undo();
		if (action == null) {
			System.out.println("Nothing to undo.");
			return false;
		}
		action.undo(board, currentAgent);
		logAction(currentRound, currentAgent.getId(), "Undo: " + action.describe());
		notifyObservers();
		return true;
	}

	public boolean redo() {
		Action action = commandHistory.redo();
		if (action == null) {
			System.out.println("Nothing to redo.");
			return false;
		}
		action.execute(board, currentAgent);
		logAction(currentRound, currentAgent.getId(), "Redo: " + action.describe());
		notifyObservers();
		return true;
	}

	// ── Accessors ──────────────────────────────────────────────────

	public Board getBoard()          { return board; }
	public Dice getDice()            { return dice; }
	public List<Agent> getAgents()   { return agents; }
	public int getCurrentRound()     { return currentRound; }
	public boolean isHumanMode()     { return humanMode; }
}
