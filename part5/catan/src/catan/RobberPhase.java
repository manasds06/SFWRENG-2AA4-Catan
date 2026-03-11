package catan;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class RobberPhase implements TurnState {

	@Override
	public void handleTurn(CatanSimulator context, Agent a) {
		Board board = context.getBoard();
		List<Agent> agents = context.getAgents();
		Robber robber = board.getRobber();

		robber.applyPenalty(agents);
		context.logAction(context.getCurrentRound(), a.getId(), "Robber: penalty applied (7 rolled)");

		robber.moveRandomly(board);
		Hex newHex = robber.getCurrentHex();
		context.logAction(context.getCurrentRound(), a.getId(),
				"Robber: moved to hex " + newHex.getId() + " (" + newHex.getTerrain() + ")");

		// Collect unique adjacent owners, excluding the roller
		List<Agent> victims = new ArrayList<>();
		for (Node n : newHex.getCorners()) {
			if (n.getOwner() != null && n.getOwner() != a && !victims.contains(n.getOwner())) {
				victims.add(n.getOwner());
			}
		}

		if (!victims.isEmpty()) {
			SecureRandom rng = new SecureRandom();
			Agent victim = victims.get(rng.nextInt(victims.size()));
			robber.stealResource(a, victim);
			context.logAction(context.getCurrentRound(), a.getId(),
					"Robber: stole a resource from Player " + victim.getId());
		}

		context.setState(new ActionPhase());
		context.getState().handleTurn(context, a);
	}
}

