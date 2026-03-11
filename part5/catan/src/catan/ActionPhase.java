package catan;

public class ActionPhase implements TurnState {

	@Override
	public void handleTurn(CatanSimulator context, Agent a) {
		Board board = context.getBoard();

		if (a instanceof HumanAgent) {
			while (true) {
				Action action = a.chooseAction(board);
				if (action == null) continue;
				if (action instanceof EndTurnAction) {
					context.logAction(context.getCurrentRound(), a.getId(), "End turn");
					break;
				}
				boolean ok = action.execute(board, a);
				context.logAction(context.getCurrentRound(), a.getId(),
						action.describe() + (ok ? "" : " (failed)"));
			}
		} else {
			Action action = a.chooseAction(board);
			if (action != null) {
				boolean ok = action.execute(board, a);
				context.logAction(context.getCurrentRound(), a.getId(),
						action.describe() + (ok ? "" : " (failed)"));
			} else {
				context.logAction(context.getCurrentRound(), a.getId(), "No action");
			}
		}

		context.notifyObservers();
		context.setState(new RollingPhase());
	}
}


