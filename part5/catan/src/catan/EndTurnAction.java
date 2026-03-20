package catan;

public class EndTurnAction extends Action {

	@Override
	public boolean execute(Board b, Agent a) {
		return true;
	}

	@Override
	public boolean undo(Board b, Agent a) { return false; }

	@Override
	public String describe() {
		return "Ended turn";
	}
}

