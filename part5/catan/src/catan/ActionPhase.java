package catan;

/**
 * Action phase: the current agent builds, lists, or passes.
 * - HumanAgent: keeps reading commands until an EndTurnAction ("go") is received.
 * - RandomAgent: chooses and executes one action, then the turn ends.
 * After all actions the observers are notified and the win condition is checked.
 */
public class ActionPhase implements TurnState {

	@Override
	public void handleTurn(CatanSimulator context, Agent a) {
		Board board = context.getBoard();

		if (a instanceof HumanAgent) {
			// Human player loops until they type "go"
			while (true) {
				Action action = a.chooseAction(board);
				if (action == null) continue;              // unrecognised command, re-prompt

				if (action instanceof EndTurnAction) {
					context.logAction(context.getCurrentRound(), a.getId(), "End turn");
					break;
				}

				boolean ok = action.execute(board, a);
				context.logAction(context.getCurrentRound(), a.getId(),
						action.describe() + (ok ? "" : " (failed)"));
			}
		} else {
			// Automated agent acts once
			Action action = a.chooseAction(board);
			if (action != null) {
				boolean ok = action.execute(board, a);
				context.logAction(context.getCurrentRound(), a.getId(),
						action.describe() + (ok ? "" : " (failed)"));
			} else {
				context.logAction(context.getCurrentRound(), a.getId(), "No action");
			}
		}

		// Notify observers (e.g. JSONStateExporter, ConsoleLogger) after every turn
		context.notifyObservers();

		// Reset to RollingPhase for the next turn
		context.setState(new RollingPhase());
	}
}

