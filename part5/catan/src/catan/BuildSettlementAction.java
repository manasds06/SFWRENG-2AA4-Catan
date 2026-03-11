package catan;

public class BuildSettlementAction extends Action {
	private Node target;

	public BuildSettlementAction(Node target) {
		this.target = target;
	}

	public boolean execute(Board b, Agent a) {
		a.pay(Cost.SETTLEMENT);
		return b.placeSettlement(a, target);
	}

	public String describe() {
		return "Built settlement at node " + target.getId();
	}
}
