package catan;

public class ActionPhase implements TurnState {

	@Override
	public void handleTurn(CatanSimulator context, Agent a) {
		Board board = context.getBoard();

		if (a instanceof HumanAgent) {
			HumanAgent human = (HumanAgent) a;
			boolean rolled = false;
			context.resetHistory();

			while (true) {
				String input = human.readInput();

				if (human.getParser().isUndo(input)) {
					context.undo();
					continue;
				}
				if (human.getParser().isRedo(input)) {
					context.redo();
					continue;
				}
				if (human.getParser().isRoll(input)) {
					if (!rolled) {
						int roll = context.getDice().roll2d6();
						context.logAction(context.getCurrentRound(), a.getId(), "Rolled " + roll);
						if (roll == 7) {
							context.setState(new RobberPhase());
							context.getState().handleTurn(context, a);
						} else {
							board.distributeResources(roll);
						}
						rolled = true;
					} else {
						System.out.println("Already rolled this turn.");
					}
					continue;
				}

				Action action = human.parseAction(input, board);
				if (action == null) continue;

				if (!rolled) {
					System.out.println("You must roll first. Type: roll");
					continue;
				}

				if (action instanceof EndTurnAction) {
					context.logAction(context.getCurrentRound(), a.getId(), "End turn");
					break;
				}

				if (action instanceof ListBoardAction) {
					action.execute(board, a);
					continue;
				}

				boolean ok = action.execute(board, a);
				if (ok) {
					context.recordAction(action);
				}
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



