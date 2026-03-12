package catan;

public class ActionPhase implements TurnState {

	@Override
	public void handleTurn(CatanSimulator context, Agent a) {
		Board board = context.getBoard();

		if (a instanceof HumanAgent) {
			boolean rolled = false;
			while (true) {
				Action action = a.chooseAction(board);
				if (action == null) continue;

				// Enforce roll-first rule
				if (!rolled && !(action instanceof RollDiceAction)) {
					System.out.println("You must roll first. Type: roll");
					continue;
				}

				if (action instanceof RollDiceAction) {
					if (!rolled) {
						action.execute(board, a);
						rolled = true;
						context.logAction(context.getCurrentRound(), a.getId(), "Rolled dice");
					} else {
						System.out.println("Already rolled this turn.");
					}
					continue;
				}

				if (action instanceof EndTurnAction) {
					context.logAction(context.getCurrentRound(), a.getId(), "End turn");
					break;
				}

				boolean ok = action.execute(board, a);
				context.logAction(context.getCurrentRound(), a.getId(),
						action.describe() + (ok ? "" : " (failed — check resources/placement rules)"));
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
		if (context.isHumanMode()) {
			context.setState(new WaitForGoState());
		} else {
			context.setState(new RollingPhase());
		}
	}
}



