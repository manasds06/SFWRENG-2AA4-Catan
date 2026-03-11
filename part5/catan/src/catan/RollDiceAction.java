package catan;

public class RollDiceAction extends Action {

	@Override
	public boolean execute(Board b, Agent a) {
		// TODO: trigger a dice roll through the simulator context
		return true;
	}

	@Override
	public String describe() {
		return "Rolled dice";
	}
}
