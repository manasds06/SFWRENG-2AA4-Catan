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

    /**
     * Boundary test: agent with only partial resources cannot afford a settlement.
     */
    // BOUNDARY TESTING
    @Test
    void testCannotAffordSettlementWithPartialResources() {
        RandomAgent agent = makeAgent(1);
        agent.addResource(ResourceType.WOOD, 1);
        agent.addResource(ResourceType.BRICK, 1);
        // Missing SHEEP and WHEAT
        assertFalse(agent.canAfford(Cost.SETTLEMENT),
                "Agent missing SHEEP and WHEAT should not afford a settlement");
    }

    /**
     * Boundary test: checkHandLimit returns false at exactly 7 cards, true at 8.
     */
    // BOUNDARY TESTING
    @Test
    void testCheckHandLimitBoundary() {
        RandomAgent agent = makeAgent(2);
        // Add exactly 7 cards
        agent.addResource(ResourceType.WOOD, 7);
        assertFalse(agent.checkHandLimit(), "7 cards should NOT exceed hand limit");

        // Add 1 more to reach 8
        agent.addResource(ResourceType.WOOD, 1);
        assertTrue(agent.checkHandLimit(), "8 cards should exceed hand limit");
    }

    /**
     * Partition test: paying for a settlement deducts WOOD, BRICK, SHEEP, WHEAT.
     */
    // PARTITION TESTING
    @Test
    void testPaySettlementCost() {
        RandomAgent agent = makeAgent(3);
        agent.addResource(ResourceType.WOOD, 2);
        agent.addResource(ResourceType.BRICK, 2);
        agent.addResource(ResourceType.SHEEP, 1);
        agent.addResource(ResourceType.WHEAT, 1);

        agent.pay(Cost.SETTLEMENT);

        assertTrue(agent.canAfford(Cost.ROAD),
                "After paying settlement (1W,1B,1S,1Wh) from (2W,2B,1S,1Wh), should still afford road (1W,1B)");
        assertFalse(agent.canAfford(Cost.SETTLEMENT),
                "Should not afford a second settlement");
    }

    /**
     * Partition test: paying for a city deducts 3 ORE and 2 WHEAT.
     */
    // PARTITION TESTING
    @Test
    void testPayCityCost() {
        RandomAgent agent = makeAgent(4);
        agent.addResource(ResourceType.ORE, 3);
        agent.addResource(ResourceType.WHEAT, 2);

        assertTrue(agent.canAfford(Cost.CITY), "Agent should afford city");
        agent.pay(Cost.CITY);
        assertFalse(agent.canAfford(Cost.CITY), "Agent should not afford city after paying");
    }

    /**
     * Partition test: an agent with zero resources cannot afford anything.
     */
    // PARTITION TESTING
    @Test
    void testCannotAffordAnythingWithNoResources() {
        RandomAgent agent = makeAgent(5);
        assertFalse(agent.canAfford(Cost.ROAD), "No resources: cannot afford road");
        assertFalse(agent.canAfford(Cost.SETTLEMENT), "No resources: cannot afford settlement");
        assertFalse(agent.canAfford(Cost.CITY), "No resources: cannot afford city");
    }
}
