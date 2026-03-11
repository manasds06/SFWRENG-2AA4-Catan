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

	// Excludes current hex and DESERT (per simplified rulebook)
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

	// Agents with >7 cards discard half (floor)
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

	public void stealResource(Agent thief, Agent victim) {
		ResourceType stolen = victim.removeRandomResource();
		if (stolen != null) thief.addResource(stolen, 1);
	}
}

