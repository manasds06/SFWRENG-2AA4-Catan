package catan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A concrete agent that makes decisions randomly.
 * Randomly selects from all valid actions (build settlement, road, upgrade to
 * city).
 * If no actions are affordable or available, returns null (pass).
 */
public class RandomAgent extends Agent {
    private Random rng;

    public RandomAgent(int id) {
        super(id);
        this.rng = new Random();
    }

    @Override
    public Action chooseAction(Board b) {
        List<Action> possibleActions = new ArrayList<>();

        // Check if agent can build a settlement
        if (canAfford(Cost.settlementCost())) {
            List<Node> availableNodes = b.getAvailableNodesForSettlement(this);
            for (Node n : availableNodes) {
                possibleActions.add(new BuildSettlementAction(n));
            }
        }

        // Check if agent can build a road
        if (canAfford(Cost.roadCost())) {
            List<Edge> availableEdges = b.getAvailableEdgesForRoad(this);
            for (Edge e : availableEdges) {
                possibleActions.add(new BuildRoadAction(e));
            }
        }

        // Check if agent can upgrade a settlement to a city
        if (canAfford(Cost.cityCost())) {
            for (Node n : b.getNodes().values()) {
                if (b.canUpgradeToCity(this, n)) {
                    possibleActions.add(new UpgradeToCityAction(n));
                }
            }
        }

        // Randomly pick one action from the list, or return null if empty
        if (possibleActions.isEmpty()) {
            return null;
        }
        return possibleActions.get(rng.nextInt(possibleActions.size()));
    }
}
