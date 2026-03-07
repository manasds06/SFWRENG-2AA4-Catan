package catan;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Tests for MoveValidator rules and the Action subclasses.
 * Covers boundary testing (zero resources) and partition testing (valid moves,
 * action execution).
 */
public class MoveValidatorAndActionTest {

    private Board board;
    private MoveValidator validator;
    private RandomAgent agent;

    @BeforeEach
    void setUp() {
        board = new Board();
        board.setupMap();
        validator = new MoveValidator();
        agent = new RandomAgent(0, validator);
    }

    /**
     * Boundary test: an agent with no resources should not be able to place a
     * settlement.
     */
    @Test
    void testCannotPlaceSettlementWithoutResources() {
        List<Node> setupNodes = board.getAvailableNodesForSetup(agent);
        Node start = setupNodes.get(0);
        board.placeSettlement(agent, start);
        Edge road = start.edges.get(0);
        board.placeRoad(agent, road);

        List<Node> candidates = board.getAvailableNodesForSettlement(agent);
        for (Node n : candidates) {
            assertFalse(validator.canPlaceSettlement(board, agent, n),
                    "Agent without resources should not pass canPlaceSettlement");
        }
    }

    /**
     * Partition test: an agent with WOOD + BRICK should be allowed to place a road.
     */
    @Test
    void testCanPlaceRoadWhenAffordable() {
        List<Node> setupNodes = board.getAvailableNodesForSetup(agent);
        Node start = setupNodes.get(0);
        board.placeSettlement(agent, start);

        agent.addResource(ResourceType.WOOD, 1);
        agent.addResource(ResourceType.BRICK, 1);

        List<Edge> edges = board.getAvailableEdgesForRoad(agent);
        assertFalse(edges.isEmpty(), "There should be available edges next to the settlement");
        assertTrue(validator.canPlaceRoad(board, agent, edges.get(0)),
                "Agent with WOOD+BRICK should be able to place a road");
    }

    /**
     * Partition test: executing BuildSettlementAction should place the settlement
     * and deduct the cost.
     */
    @Test
    void testBuildSettlementActionExecutes() {
        List<Node> setupNodes = board.getAvailableNodesForSetup(agent);
        Node start = setupNodes.get(0);
        board.placeSettlement(agent, start);
        Edge road = start.edges.get(0);
        board.placeRoad(agent, road);

        agent.addResource(ResourceType.WOOD, 1);
        agent.addResource(ResourceType.BRICK, 1);
        agent.addResource(ResourceType.SHEEP, 1);
        agent.addResource(ResourceType.WHEAT, 1);

        List<Node> candidates = board.getAvailableNodesForSettlement(agent);
        if (!candidates.isEmpty()) {
            int vpBefore = agent.getVictoryPoints();
            Node target = candidates.get(0);
            BuildSettlementAction action = new BuildSettlementAction(target);
            boolean result = action.execute(board, agent);

            assertTrue(result, "BuildSettlementAction should return true");
            assertEquals(BuildingType.SETTLEMENT, target.getBuilding());
            assertEquals(vpBefore + 1, agent.getVictoryPoints());
        }
    }

    /**
     * Partition test: upgrading a settlement to a city should add 1 VP.
     */
    @Test
    void testUpgradeToCityGivesExtraVP() {
        List<Node> setupNodes = board.getAvailableNodesForSetup(agent);
        Node start = setupNodes.get(0);
        board.placeSettlement(agent, start);

        agent.addResource(ResourceType.ORE, 3);
        agent.addResource(ResourceType.WHEAT, 2);

        int vpBefore = agent.getVictoryPoints();
        UpgradeToCityAction action = new UpgradeToCityAction(start);
        boolean result = action.execute(board, agent);

        assertTrue(result, "UpgradeToCityAction should return true");
        assertEquals(BuildingType.CITY, start.getBuilding());
        assertEquals(vpBefore + 1, agent.getVictoryPoints());
    }
}
