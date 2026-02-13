package catan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomAgent extends Agent {
	private Random rng;
	private MoveValidator validator;

	public RandomAgent(int id, MoveValidator validator) {
		initAgent(id);
		this.rng = new Random();
		this.validator = validator;
	}

	public Action chooseAction(Board b) {
		List<Action> possible = new ArrayList<>();

		for (Node n : b.getAvailableNodesForSettlement(this)) {
			if (validator.canPlaceSettlement(b, this, n)) {
				possible.add(new BuildSettlementAction(n));
			}
		}

		for (Edge e : b.getAvailableEdgesForRoad(this)) {
			if (validator.canPlaceRoad(b, this, e)) {
				possible.add(new BuildRoadAction(e));
			}
		}

		for (Node n : b.getNodes().values()) {
			if (validator.canUpgradeToCity(b, this, n)) {
				possible.add(new UpgradeToCityAction(n));
			}
		}

		if (possible.isEmpty()) return null;
		return possible.get(rng.nextInt(possible.size()));
	}
}
