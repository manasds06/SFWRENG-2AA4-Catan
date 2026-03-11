package catan;

public class UpgradeToCityAction extends Action {
	private Node target;

	public UpgradeToCityAction(Node target) {
		this.target = target;
	}

	public boolean execute(Board b, Agent a) {
		if (!a.canAfford(Cost.CITY)) return false;
		a.pay(Cost.CITY);
		return b.upgradeToCity(a, target);
	}

	public String describe() {
		return "Upgraded to city at node " + target.getId();
	}
}

