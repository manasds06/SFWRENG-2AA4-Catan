package catan;

import java.util.EnumMap;
import java.util.Map;

/**
 * Represents the resource cost of a building action.
 * Provides static factory methods for standard Catan building costs.
 */
public class Cost {
    private Map<ResourceType, Integer> required;

    public Cost(Map<ResourceType, Integer> required) {
        this.required = required;
    }

    public Map<ResourceType, Integer> getRequired() {
        return required;
    }

    /**
     * Cost of building a settlement: 1 WOOD, 1 BRICK, 1 SHEEP, 1 WHEAT.
     */
    public static Cost settlementCost() {
        Map<ResourceType, Integer> map = new EnumMap<>(ResourceType.class);
        map.put(ResourceType.WOOD, 1);
        map.put(ResourceType.BRICK, 1);
        map.put(ResourceType.SHEEP, 1);
        map.put(ResourceType.WHEAT, 1);
        return new Cost(map);
    }

    /**
     * Cost of building a road: 1 WOOD, 1 BRICK.
     */
    public static Cost roadCost() {
        Map<ResourceType, Integer> map = new EnumMap<>(ResourceType.class);
        map.put(ResourceType.WOOD, 1);
        map.put(ResourceType.BRICK, 1);
        return new Cost(map);
    }

    /**
     * Cost of upgrading a settlement to a city: 2 WHEAT, 3 ORE.
     */
    public static Cost cityCost() {
        Map<ResourceType, Integer> map = new EnumMap<>(ResourceType.class);
        map.put(ResourceType.WHEAT, 2);
        map.put(ResourceType.ORE, 3);
        return new Cost(map);
    }
}
