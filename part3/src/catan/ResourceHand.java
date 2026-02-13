package catan;

import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a player's collection of resource cards.
 * Internally uses an EnumMap to track the count of each resource type.
 */
public class ResourceHand {
    private Map<ResourceType, Integer> counts;

    public ResourceHand() {
        this.counts = new EnumMap<>(ResourceType.class);
        for (ResourceType r : ResourceType.values()) {
            counts.put(r, 0);
        }
    }

    /**
     * Returns the total number of resource cards in hand.
     */
    public int getTotalCards() {
        int total = 0;
        for (int v : counts.values()) {
            total += v;
        }
        return total;
    }

    /**
     * Adds the specified amount of a resource to the hand.
     */
    public void add(ResourceType r, int amount) {
        counts.put(r, counts.get(r) + amount);
    }

    /**
     * Removes the specified amount of a resource from the hand.
     * Does not allow going below zero.
     */
    public void remove(ResourceType r, int amount) {
        int current = counts.get(r);
        counts.put(r, Math.max(0, current - amount));
    }

    /**
     * Returns the count of a specific resource type.
     */
    public int get(ResourceType r) {
        return counts.get(r);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (ResourceType r : ResourceType.values()) {
            if (!first)
                sb.append(", ");
            sb.append(r.name()).append("=").append(counts.get(r));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }
}
