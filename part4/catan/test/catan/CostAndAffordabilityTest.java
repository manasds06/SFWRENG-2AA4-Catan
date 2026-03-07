package catan;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Tests for Cost constants and the Agent affordability / payment logic.
 * Uses partition testing (each cost type) and boundary testing (exact
 * affordability threshold).
 */
public class CostAndAffordabilityTest {

    /**
     * Helper: create a RandomAgent with a MoveValidator so initAgent() is called.
     */
    private RandomAgent makeAgent(int id) {
        return new RandomAgent(id, new MoveValidator());
    }

    /**
     * Partition test: Cost.SETTLEMENT should require 1 WOOD, 1 BRICK, 1 SHEEP, 1
     * WHEAT.
     */
    @Test
    void testSettlementCostContents() {
        assertEquals(1, Cost.SETTLEMENT.getRequired().get(ResourceType.WOOD));
        assertEquals(1, Cost.SETTLEMENT.getRequired().get(ResourceType.BRICK));
        assertEquals(1, Cost.SETTLEMENT.getRequired().get(ResourceType.SHEEP));
        assertEquals(1, Cost.SETTLEMENT.getRequired().get(ResourceType.WHEAT));
        assertNull(Cost.SETTLEMENT.getRequired().get(ResourceType.ORE),
                "Settlement should not require ORE");
    }

    /**
     * Partition test: Cost.ROAD should require 1 WOOD, 1 BRICK.
     */
    @Test
    void testRoadCostContents() {
        assertEquals(1, Cost.ROAD.getRequired().get(ResourceType.WOOD));
        assertEquals(1, Cost.ROAD.getRequired().get(ResourceType.BRICK));
        assertEquals(2, Cost.ROAD.getRequired().size(), "Road cost should only have 2 entries");
    }

    /**
     * Partition test: Cost.CITY should require 3 ORE, 2 WHEAT.
     */
    @Test
    void testCityCostContents() {
        assertEquals(3, Cost.CITY.getRequired().get(ResourceType.ORE));
        assertEquals(2, Cost.CITY.getRequired().get(ResourceType.WHEAT));
        assertEquals(2, Cost.CITY.getRequired().size(), "City cost should only have 2 entries");
    }

    /**
     * Boundary test: agent with exactly enough resources can afford a road;
     * after paying, the agent can no longer afford it.
     */
    @Test
    void testCanAffordAndPay() {
        RandomAgent agent = makeAgent(0);
        agent.addResource(ResourceType.WOOD, 1);
        agent.addResource(ResourceType.BRICK, 1);

        assertTrue(agent.canAfford(Cost.ROAD), "Agent with exact resources should afford a road");

        agent.pay(Cost.ROAD);

        assertFalse(agent.canAfford(Cost.ROAD), "Agent should not afford a second road after paying");
    }
}
