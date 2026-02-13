package catan;

/**
 * Action to upgrade an existing settlement to a city.
 * Cost: 2 WHEAT, 3 ORE. Awards an additional 1 victory point (city = 2 VP
 * total).
 */
public class UpgradeToCityAction extends Action {
    private Node target;

    public UpgradeToCityAction(Node target) {
        this.target = target;
    }

    @Override
    public boolean execute(Board b, Agent a) {
        return b.upgradeToCity(a, target);
    }

    @Override
    public String describe() {
        return "upgrades settlement to city on node " + target.getId();
    }
}
