package catan;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Tests for MoveValidator rules and the Action subclasses.
 * Covers boundary testing (zero resources) and partition testing (valid moves,
 * action execution, describe output, RandomAgent behaviour).
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

    // ----------------------------------------------------------------
    // MoveValidator tests
    // ----------------------------------------------------------------

    /**
     * Boundary test: an agent with no resources should not be able to place a
     * settlement.
     */
    // BOUNDARY TESTING
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
    // PARTITION TESTING
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
     * Boundary test: an agent without resources cannot place a road.
     */
    // BOUNDARY TESTING
    @Test
    void testCannotPlaceRoadWithoutResources() {
        List<Node> setupNodes = board.getAvailableNodesForSetup(agent);
        Node start = setupNodes.get(0);
        board.placeSettlement(agent, start);

        List<Edge> edges = board.getAvailableEdgesForRoad(agent);
        if (!edges.isEmpty()) {
            assertFalse(validator.canPlaceRoad(board, agent, edges.get(0)),
                    "Agent without resources should not be able to place a road");
        }
    }

    /**
     * Partition test: canUpgradeToCity returns true when agent owns a settlement
     * and can afford the city cost.
     */
    // PARTITION TESTING
    @Test
    void testCanUpgradeToCityWhenAffordable() {
        List<Node> setupNodes = board.getAvailableNodesForSetup(agent);
        Node start = setupNodes.get(0);
        board.placeSettlement(agent, start);

        agent.addResource(ResourceType.ORE, 3);
        agent.addResource(ResourceType.WHEAT, 2);

        assertTrue(validator.canUpgradeToCity(board, agent, start),
                "Agent with ORE+WHEAT owning a settlement should be able to upgrade");
    }

    /**
     * Boundary test: canUpgradeToCity returns false when agent cannot afford it.
     */
    // BOUNDARY TESTING
    @Test
    void testCannotUpgradeToCityWithoutResources() {
        List<Node> setupNodes = board.getAvailableNodesForSetup(agent);
        Node start = setupNodes.get(0);
        board.placeSettlement(agent, start);

        assertFalse(validator.canUpgradeToCity(board, agent, start),
                "Agent without ORE+WHEAT should not be able to upgrade to city");
    }

    // ----------------------------------------------------------------
    // Action execute() tests
    // ----------------------------------------------------------------

    /**
     * Partition test: executing BuildSettlementAction should place the settlement
     * and deduct the cost.
     */
    // PARTITION TESTING
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
     * Partition test: executing BuildRoadAction should place the road and
     * deduct the cost.
     */
    // PARTITION TESTING
    @Test
    void testBuildRoadActionExecutes() {
        List<Node> setupNodes = board.getAvailableNodesForSetup(agent);
        Node start = setupNodes.get(0);
        board.placeSettlement(agent, start);

        agent.addResource(ResourceType.WOOD, 1);
        agent.addResource(ResourceType.BRICK, 1);

        List<Edge> edges = board.getAvailableEdgesForRoad(agent);
        assertFalse(edges.isEmpty(), "Should have edges available");

        Edge target = edges.get(0);
        BuildRoadAction action = new BuildRoadAction(target);
        boolean result = action.execute(board, agent);

        assertTrue(result, "BuildRoadAction should return true");
        assertEquals(agent, target.getOwner(), "Edge should be owned by agent after BuildRoadAction");
    }

    /**
     * Partition test: upgrading a settlement to a city should add 1 VP.
     */
    // PARTITION TESTING
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

    // ----------------------------------------------------------------
    // Action describe() tests
    // ----------------------------------------------------------------

    /**
     * Partition test: BuildSettlementAction.describe() returns a non-empty string.
     */
    // PARTITION TESTING
    @Test
    void testBuildSettlementActionDescribe() {
        Node n = board.getNodes().get(0);
        BuildSettlementAction action = new BuildSettlementAction(n);
        String desc = action.describe();
        assertNotNull(desc, "describe() should not return null");
        assertTrue(desc.contains("settlement"), "describe() should mention 'settlement'");
    }

    /**
     * Partition test: BuildRoadAction.describe() returns a non-empty string.
     */
    // PARTITION TESTING
    @Test
    void testBuildRoadActionDescribe() {
        Edge e = board.getEdges().get(0);
        BuildRoadAction action = new BuildRoadAction(e);
        String desc = action.describe();
        assertNotNull(desc, "describe() should not return null");
        assertTrue(desc.contains("road"), "describe() should mention 'road'");
    }

    /**
     * Partition test: UpgradeToCityAction.describe() returns a non-empty string.
     */
    // PARTITION TESTING
    @Test
    void testUpgradeToCityActionDescribe() {
        Node n = board.getNodes().get(0);
        UpgradeToCityAction action = new UpgradeToCityAction(n);
        String desc = action.describe();
        assertNotNull(desc, "describe() should not return null");
        assertTrue(desc.contains("city"), "describe() should mention 'city'");
    }

    // ----------------------------------------------------------------
    // RandomAgent tests
    // ----------------------------------------------------------------

    /**
     * Boundary test: RandomAgent.chooseAction() returns null when agent has
     * no resources and no valid moves.
     */
    // BOUNDARY TESTING
    @Test
    void testChooseActionReturnsNullWithNoResources() {
        // Place a settlement so the agent is on the board, but give no resources
        List<Node> setupNodes = board.getAvailableNodesForSetup(agent);
        Node start = setupNodes.get(0);
        board.placeSettlement(agent, start);
        Edge road = start.edges.get(0);
        board.placeRoad(agent, road);

        Action action = agent.chooseAction(board);
        assertNull(action, "Agent without resources should have no valid moves and return null");
    }

    /**
     * Partition test: RandomAgent.chooseAction() returns a non-null action when
     * the agent has enough resources to build.
     */
    // PARTITION TESTING
    @Test
    void testChooseActionReturnsActionWithResources() {
        List<Node> setupNodes = board.getAvailableNodesForSetup(agent);
        Node start = setupNodes.get(0);
        board.placeSettlement(agent, start);
        Edge road = start.edges.get(0);
        board.placeRoad(agent, road);

        // Give enough for a road
        agent.addResource(ResourceType.WOOD, 1);
        agent.addResource(ResourceType.BRICK, 1);

        Action action = agent.chooseAction(board);
        assertNotNull(action, "Agent with resources should have a valid action");
    }

    /**
     * Partition test: Agent getId() returns the expected value.
     */
    // PARTITION TESTING
    @Test
    void testAgentGetId() {
        assertEquals(0, agent.getId(), "Agent created with id 0 should return 0");
        RandomAgent agent2 = new RandomAgent(5, validator);
        assertEquals(5, agent2.getId(), "Agent created with id 5 should return 5");
    }

    /**
     * Partition test: Agent addVictoryPoints and getVictoryPoints work correctly.
     */
    // PARTITION TESTING
    @Test
    void testAgentVictoryPoints() {
        assertEquals(0, agent.getVictoryPoints(), "Agent should start with 0 VP");
        agent.addVictoryPoints(3);
        assertEquals(3, agent.getVictoryPoints(), "Agent should have 3 VP after adding 3");
    }
}
