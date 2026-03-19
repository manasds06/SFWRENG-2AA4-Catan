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

	@Override
	public boolean undo(Board b, Agent a) {
		target.building = BuildingType.SETTLEMENT;
		a.refund(Cost.CITY);
		a.addVictoryPoints(-1);
		return true;
	}

	@Override
	public boolean isUndoable() { return true; }

	public String describe() {
		return "Upgraded to city at node " + target.getId();
	}

	@Override
	public void accept(ActionVisitor v) {
		v.visit(this);
	}
}

