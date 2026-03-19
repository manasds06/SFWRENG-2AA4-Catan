package catan;

public class BuildSettlementAction extends Action {
	private Node target;

	public BuildSettlementAction(Node target) {
		this.target = target;
	}

	public boolean execute(Board b, Agent a) {
		if (!a.canAfford(Cost.SETTLEMENT)) return false;
		a.pay(Cost.SETTLEMENT);
		return b.placeSettlement(a, target);
	}

	@Override
	public boolean undo(Board b, Agent a) {
		target.owner = null;
		target.building = BuildingType.NONE;
		a.refund(Cost.SETTLEMENT);
		a.addVictoryPoints(-1);
		return true;
	}

	@Override
	public boolean isUndoable() { return true; }

	public String describe() {
		return "Built settlement at node " + target.getId();
	}

	@Override
	public void accept(ActionVisitor v) {
		v.visit(this);
	}
}

