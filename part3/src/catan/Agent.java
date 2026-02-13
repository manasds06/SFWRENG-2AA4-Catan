package catan;

import java.util.Map;

/**
 * Abstract base class for all players (agents) in the Catan simulation.
 * Each agent has an ID, a resource hand, and victory points.
 * Subclasses must implement the chooseAction method to define their strategy.
 */
public abstract class Agent {
    private int id;
    private ResourceHand hand;
    private int victoryPoints;

    public Agent(int id) {
        this.id = id;
        this.hand = new ResourceHand();
        this.victoryPoints = 0;
    }

    public int getId() {
        return id;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public void addVictoryPoints(int delta) {
        this.victoryPoints += delta;
    }

    public ResourceHand getHand() {
        return hand;
    }

    /**
     * Adds the specified amount of a resource to this agent's hand.
     */
    public void addResource(ResourceType r, int amount) {
        hand.add(r, amount);
    }

    /**
     * Checks whether this agent can afford the given cost.
     */
    public boolean canAfford(Cost c) {
        for (Map.Entry<ResourceType, Integer> entry : c.getRequired().entrySet()) {
            if (hand.get(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Pays the given cost by removing resources from the agent's hand.
     */
    public void pay(Cost c) {
        for (Map.Entry<ResourceType, Integer> entry : c.getRequired().entrySet()) {
            hand.remove(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Checks whether the agent has exceeded the hand limit (more than 7 cards).
     * Per R1.8, agents with more than 7 cards must try to spend them.
     * 
     * @return true if hand exceeds the limit
     */
    public boolean checkHandLimit() {
        return hand.getTotalCards() > 7;
    }

    /**
     * Chooses an action to perform on the given board.
     * Returns null if no action is available or the agent passes.
     * 
     * @param b the game board
     * @return the chosen action, or null to pass
     */
    public abstract Action chooseAction(Board b);
}
