package catan;

/**
 * Action to build a road on a target edge.
 * Cost: 1 WOOD, 1 BRICK. Does not award victory points directly.
 */
public class BuildRoadAction extends Action {
    private Edge target;

    public BuildRoadAction(Edge target) {
        this.target = target;
    }

    @Override
    public boolean execute(Board b, Agent a) {
        return b.placeRoad(a, target);
    }

    @Override
    public String describe() {
        return "builds road on edge " + target.getId();
    }
}
