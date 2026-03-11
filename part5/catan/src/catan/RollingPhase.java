package catan;

public class RollingPhase implements TurnState {

	@Override
	public void handleTurn(CatanSimulator context, Agent a) {
		int roll = context.getDice().roll2d6();
		context.logAction(context.getCurrentRound(), a.getId(), "Rolled " + roll);

		if (roll == 7) {
			context.setState(new RobberPhase());
			context.getState().handleTurn(context, a);
		} else {
			context.getBoard().distributeResources(roll);
			context.setState(new ActionPhase());
			context.getState().handleTurn(context, a);
		}
	}
}
