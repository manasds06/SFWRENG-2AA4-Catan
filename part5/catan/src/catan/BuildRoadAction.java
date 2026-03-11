package catan;

public class BuildRoadAction extends Action {
	private Edge target;

	public BuildRoadAction(Edge target) {
		this.target = target;
	}

	public boolean execute(Board b, Agent a) {
		a.pay(Cost.ROAD);
		return b.placeRoad(a, target);
	}

	public String describe() {
		return "Built road at edge " + target.getId();
	}
}
