package catan;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Robber {

	private Hex currentHex;

	public Robber(Hex startingHex) {
		this.currentHex = startingHex;
	}

	public Hex getCurrentHex() {
		return currentHex;
	}

	/**
	 * Moves the robber to a random hex that is:
	 *  - not the hex it currently occupies
	 *  - not a DESERT hex (per simplified rulebook)
	 */
	public void moveRandomly(Board b) {
		List<Hex> candidates = new ArrayList<>();
		for (Hex h : b.getHexes().values()) {
			if (h == currentHex) continue;
			if (h.getTerrain() == TerrainType.DESERT) continue;
			candidates.add(h);
		}
		if (candidates.isEmpty()) return;
		SecureRandom rng = new SecureRandom();
		currentHex = candidates.get(rng.nextInt(candidates.size()));
	}

	/**
	 * Per R2.5: any agent holding more than 7 cards must discard half
	 * (rounded down) chosen at random.
	 */
	public void applyPenalty(List<Agent> agents) {
		for (Agent a : agents) {
			int total = a.getHandTotal();
			if (total > 7) {
				int toDiscard = total / 2;
				for (int i = 0; i < toDiscard; i++) {
					a.removeRandomResource();
				}
			}
		}
	}

	/**
	 * Takes one random resource card from the victim and gives it to the thief.
	 * Does nothing if the victim has no cards.
	 */
	public void stealResource(Agent thief, Agent victim) {
		ResourceType stolen = victim.removeRandomResource();
		if (stolen != null) {
			thief.addResource(stolen, 1);
		}
	}
}
