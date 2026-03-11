package catan;

import java.util.HashMap;
import java.util.Map;

public class ResourceHand {
	private Map<ResourceType, Integer> counts;

	public ResourceHand() {
		counts = new HashMap<>();
		for (ResourceType r : ResourceType.values()) {
			counts.put(r, 0);
		}
	}

	public int getTotalCards() {
		int total = 0;
		for (int v : counts.values()) total += v;
		return total;
	}

	public void add(ResourceType r, int amount) {
		counts.put(r, counts.get(r) + amount);
	}

	public void remove(ResourceType r, int amount) {
		counts.put(r, counts.get(r) - amount);
	}

	public int get(ResourceType r) {
		return counts.get(r);
	}
}
