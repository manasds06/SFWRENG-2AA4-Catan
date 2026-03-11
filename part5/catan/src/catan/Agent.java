package catan;

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

	protected void initAgent(int id) {
		this.id = id;
		this.hand = new ResourceHand();
		this.victoryPoints = 0;
	}
}
