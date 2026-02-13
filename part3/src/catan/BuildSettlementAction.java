package catan;

/**
 * Action to build a settlement on a target node.
 * Cost: 1 WOOD, 1 BRICK, 1 SHEEP, 1 WHEAT. Awards 1 victory point.
 */
public class BuildSettlementAction extends Action {
    private Node target;

    public BuildSettlementAction(Node target) {
        this.target = target;
    }

    @Override
    public boolean execute(Board b, Agent a) {
        return b.placeSettlement(a, target);
    }

    @Override
    public String describe() {
        return "builds settlement on node " + target.getId();
    }
}
