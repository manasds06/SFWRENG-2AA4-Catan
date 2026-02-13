package catan;

/**
 * Abstract base class for all game actions an agent can take.
 * Concrete subclasses: BuildSettlementAction, BuildRoadAction,
 * UpgradeToCityAction.
 */
public abstract class Action {

    /**
     * Executes this action on the given board for the given agent.
     * 
     * @param b the game board
     * @param a the agent performing the action
     * @return true if the action was successfully executed
     */
    public abstract boolean execute(Board b, Agent a);

    /**
     * Returns a human-readable description of this action.
     * Used for console logging (R1.7).
     */
    public abstract String describe();
}
