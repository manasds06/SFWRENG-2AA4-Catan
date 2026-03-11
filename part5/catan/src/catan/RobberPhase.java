package catan;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Activated when a 7 is rolled. Applies the hand-limit penalty (R2.5),
 * moves the Robber to a random non-desert hex, then steals one card from
 * a randomly chosen player adjacent to the new Robber location.
 */
public class RobberPhase implements TurnState {

	@Override
	public void handleTurn(CatanSimulator context, Agent a) {
		Board board = context.getBoard();
		List<Agent> agents = context.getAgents();
		Robber robber = board.getRobber();

		// 1. All agents with more than 7 cards discard half (R2.5)
		robber.applyPenalty(agents);
		context.logAction(context.getCurrentRound(), a.getId(), "Robber: penalty applied (7 rolled)");

		// 2. Move robber to a random non-desert, non-current hex
		robber.moveRandomly(board);
		Hex newHex = robber.getCurrentHex();
		context.logAction(context.getCurrentRound(), a.getId(),
				"Robber: moved to hex " + newHex.getId() + " (" + newHex.getTerrain() + ")");

		// 3. Find eligible victims: adjacent settlement/city owners (not the roller)
		List<Agent> victims = new ArrayList<>();
		for (Node n : newHex.getCorners()) {
			if (n.getOwner() != null && n.getOwner() != a) {
				if (!victims.contains(n.getOwner())) {
					victims.add(n.getOwner());
				}
			}
		}

		// 4. Pick one victim at random and steal a card
		if (!victims.isEmpty()) {
			SecureRandom rng = new SecureRandom();
			Agent victim = victims.get(rng.nextInt(victims.size()));
			robber.stealResource(a, victim);
			context.logAction(context.getCurrentRound(), a.getId(),
					"Robber: stole a resource from Player " + victim.getId());
		}

		// 5. Proceed to the action phase
		context.setState(new ActionPhase());
		context.getState().handleTurn(context, a);
	}
}

