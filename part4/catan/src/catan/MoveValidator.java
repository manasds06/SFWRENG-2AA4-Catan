package catan;

public class MoveValidator {
	public boolean canPlaceSettlement(Board b, Agent a, Node n) {
		return b.getAvailableNodesForSettlement(a).contains(n) && a.canAfford(Cost.SETTLEMENT);
	}

	public boolean canPlaceRoad(Board b, Agent a, Edge e) {
		return b.getAvailableEdgesForRoad(a).contains(e) && a.canAfford(Cost.ROAD);
	}

	public boolean canUpgradeToCity(Board b, Agent a, Node n) {
		return b.canUpgradeToCity(a, n) && a.canAfford(Cost.CITY);
	}
}
