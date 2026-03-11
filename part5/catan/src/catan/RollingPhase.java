package catan;

/**
 * First phase of every turn: roll the two dice.
 * On a 7 the Robber is triggered; otherwise resources are distributed
 * to all settlement/city owners whose hex token matches the roll.
 */
public class RollingPhase implements TurnState {

	@Override
	public void handleTurn(CatanSimulator context, Agent a) {
		int roll = context.getDice().roll2d6();
		context.logAction(context.getCurrentRound(), a.getId(), "Rolled " + roll);

		if (roll == 7) {
			// Robber is activated — hand off to RobberPhase
			context.setState(new RobberPhase());
			context.getState().handleTurn(context, a);
		} else {
			context.getBoard().distributeResources(roll);
			context.setState(new ActionPhase());
			context.getState().handleTurn(context, a);
		}
	}
}
