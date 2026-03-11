package catan;

public class EndTurnAction extends Action {

	@Override
	public boolean execute(Board b, Agent a) {
		// TODO: signal the simulator that the current player's turn is over
		return true;
	}

	@Override
	public String describe() {
		return "Ended turn";
	}
}
