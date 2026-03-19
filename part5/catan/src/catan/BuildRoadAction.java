package catan;

public class BuildRoadAction extends Action {
	private Edge target;

	public BuildRoadAction(Edge target) {
		this.target = target;
	}

	public boolean execute(Board b, Agent a) {
		if (!a.canAfford(Cost.ROAD)) return false;
		a.pay(Cost.ROAD);
		return b.placeRoad(a, target);
	}

	@Override
	public boolean undo(Board b, Agent a) {
		target.owner = null;
		a.refund(Cost.ROAD);
		return true;
	}

	@Override
	public boolean isUndoable() { return true; }

	public String describe() {
		return "Built road at edge " + target.getId();
	}

	@Override
	public void accept(ActionVisitor v) {
		v.visit(this);
	}
}

