package catan;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Tests for the Board class.
 * Covers boundary testing (exact element counts) and partition testing
 * (desert hex properties, distance-rule invariant R1.6).
 */
public class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
        board.setupMap();
    }

    /**
     * Boundary test: the standard Catan board has exactly 19 hex tiles.
     */
    @Test
    void testBoardHas19Hexes() {
        assertEquals(19, board.getHexes().size(), "Board should have exactly 19 hexes");
    }

    /**
     * Boundary test: the standard Catan board has exactly 54 intersection nodes.
     */
    @Test
    void testBoardHas54Nodes() {
        assertEquals(54, board.getNodes().size(), "Board should have exactly 54 nodes");
    }

    /**
     * Partition test: hex 18 is defined as DESERT with number token 0.
     */
    @Test
    void testDesertHexHasNoToken() {
        Hex desert = board.getHexes().get(18);
        assertNotNull(desert, "Hex 18 should exist");
        assertEquals(TerrainType.DESERT, desert.getTerrain(), "Hex 18 should be DESERT");
        assertEquals(0, desert.getNumberToken(), "Desert hex should have number token 0");
    }

    /**
     * Partition test (R1.6 distance rule): after placing a settlement during setup,
     * the adjacent nodes must NOT appear in the available-setup list.
     */
    @Test
    void testDistanceRuleOnSetup() {
        RandomAgent agent = new RandomAgent(0, new MoveValidator());

        List<Node> before = board.getAvailableNodesForSetup(agent);
        assertFalse(before.isEmpty(), "There should be available nodes on an empty board");

        Node chosen = before.get(0);
        chosen.owner = agent;
        chosen.building = BuildingType.SETTLEMENT;

        List<Node> after = board.getAvailableNodesForSetup(agent);
        assertFalse(after.contains(chosen), "The placed node should no longer be available");

        for (Edge e : chosen.edges) {
            Node neighbour = (e.getA() == chosen) ? e.getB() : e.getA();
            assertFalse(after.contains(neighbour),
                    "Neighbour node " + neighbour.getId() + " should be excluded by distance rule");
        }
    }
}
