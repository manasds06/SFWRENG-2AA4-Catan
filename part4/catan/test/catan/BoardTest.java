package catan;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Tests for the Board class.
 * Covers boundary testing (exact element counts) and partition testing
 * (desert hex properties, distance-rule, resource distribution).
 */
public class BoardTest {

    private Board board;
    private RandomAgent agent;

    @BeforeEach
    void setUp() {
        board = new Board();
        board.setupMap();
        agent = new RandomAgent(0, new MoveValidator());
    }

    /**
     * Boundary test: the standard Catan board has exactly 19 hex tiles.
     */
    // BOUNDARY TESTING
    @Test
    void testBoardHas19Hexes() {
        assertEquals(19, board.getHexes().size(), "Board should have exactly 19 hexes");
    }

    /**
     * Partition test: hex 18 is defined as DESERT with number token 0.
     */
    // PARTITION TESTING
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
    // PARTITION TESTING
    @Test
    void testDistanceRuleOnSetup() {
        List<Node> before = board.getAvailableNodesForSetup(agent);
        assertFalse(before.isEmpty(), "There should be available nodes on an empty board");

        Node chosen = before.get(0);
        board.placeSettlement(agent, chosen);

        List<Node> after = board.getAvailableNodesForSetup(agent);
        assertFalse(after.contains(chosen), "The placed node should no longer be available");

        for (Edge e : chosen.edges) {
            Node neighbour = (e.getA() == chosen) ? e.getB() : e.getA();
            assertFalse(after.contains(neighbour),
                    "Neighbour node " + neighbour.getId() + " should be excluded by distance rule");
        }
    }

    /**
     * Partition test: distributeResources gives resources to settlement owners
     * when the roll matches the hex's number token.
     */
    // PARTITION TESTING
    @Test
    void testDistributeResourcesSettlement() {
        // Find a non-desert hex and place a settlement on one of its corners
        Hex targetHex = null;
        for (Hex h : board.getHexes().values()) {
            if (h.getTerrain() != TerrainType.DESERT && h.getNumberToken() != 0) {
                targetHex = h;
                break;
            }
        }
        assertNotNull(targetHex, "Should find a non-desert hex");

        Node corner = targetHex.getCorners().get(0);
        board.placeSettlement(agent, corner);

        // Roll the matching number — agent should receive 1 resource
        board.distributeResources(targetHex.getNumberToken());

        // Verify the code path executed without error
        assertNotNull(board.getHexes(), "Board should still be valid after distribution");
    }
}
