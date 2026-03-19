package catan;

public class RollDiceAction extends Action {

	private final CatanSimulator context;

	// Actions need the simulator context to roll dice and change state
	public RollDiceAction(CatanSimulator context) {
		this.context = context;
	}

	@Override
	public boolean execute(Board b, Agent a) {
		int roll = context.getDice().roll2d6();
		context.logAction(context.getCurrentRound(), a.getId(), "Rolled " + roll);
		if (roll == 7) {
			context.setState(new RobberPhase());
			context.getState().handleTurn(context, a);
		} else {
			b.distributeResources(roll);
		}
		return true;
	}

	@Override
	public String describe() {
		return "Rolled dice";
	}

	@Override
	public void accept(ActionVisitor v) {
		// Human-mode action — not evaluated by AI visitors
	}
}

