package catan;

import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;

public abstract class Agent {
	private int id;
	private ResourceHand hand;
	private int victoryPoints;

	public abstract Action chooseAction(Board b);

	public int getId() {
		return id;
	}

	public int getVictoryPoints() {
		return victoryPoints;
	}

	public void addVictoryPoints(int delta) {
		victoryPoints += delta;
	}

	public void addResource(ResourceType r, int amount) {
		hand.add(r, amount);
	}

	public void removeResource(ResourceType r, int amount) {
		hand.remove(r, amount);
	}

	public ResourceType removeRandomResource() {
		List<ResourceType> available = new ArrayList<>();
		for (ResourceType r : ResourceType.values()) {
			if (hand.get(r) > 0) available.add(r);
		}
		if (available.isEmpty()) return null;
		SecureRandom rng = new SecureRandom();
		ResourceType chosen = available.get(rng.nextInt(available.size()));
		hand.remove(chosen, 1);
		return chosen;
	}

	public boolean canAfford(Cost c) {
		for (java.util.Map.Entry<ResourceType, Integer> entry : c.getRequired().entrySet()) {
			if (hand.get(entry.getKey()) < entry.getValue()) return false;
		}
		return true;
	}

	public void pay(Cost c) {
		for (java.util.Map.Entry<ResourceType, Integer> entry : c.getRequired().entrySet()) {
			hand.remove(entry.getKey(), entry.getValue());
		}
	}

	public boolean checkHandLimit() {
		return hand.getTotalCards() > 7;
	}

	/** Returns the total number of resource cards in this agent's hand. */
	public int getHandTotal() {
		return hand.getTotalCards();
	}

	/** Returns the count of a specific resource in this agent's hand. */
	public int getHandCount(ResourceType r) {
		return hand.get(r);
	}

	/** Returns a human-readable summary of the agent's resource hand. */
	public String getHandSummary() {
		StringBuilder sb = new StringBuilder();
		for (ResourceType r : ResourceType.values()) {
			if (sb.length() > 0) sb.append(" ");
			sb.append(r).append(":").append(hand.get(r));
		}
		sb.append(" (total: ").append(hand.getTotalCards()).append(")");
		return sb.toString();
	}

	protected void initAgent(int id) {
		this.id = id;
		this.hand = new ResourceHand();
		this.victoryPoints = 0;
	}
}
