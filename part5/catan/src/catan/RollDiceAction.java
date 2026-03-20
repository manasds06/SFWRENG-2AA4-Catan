package catan;

public class RollDiceAction extends Action {

	private int rolledValue;

	public RollDiceAction(int rolledValue) {
		this.rolledValue = rolledValue;
	}

	@Override
	public boolean execute(Board b, Agent a) {
		b.distributeResources(rolledValue);
		return true;
	}

	@Override
	public boolean undo(Board b, Agent a) { return false; }

	@Override
	public String describe() {
		return "Rolled " + rolledValue;
	}
}
