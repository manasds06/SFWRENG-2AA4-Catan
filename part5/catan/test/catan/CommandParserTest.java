package catan;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for CommandParser (R2.1 / Task 3).
 * Covers all six command patterns, boundary cases, and invalid input.
 * Uses partition testing (each command type) and boundary testing (edge IDs,
 * blank/garbage input).
 */
public class CommandParserTest {

    private CommandParser parser;
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
        board.setupMap();
        parser = new CommandParser();
        parser.setBoard(board);
        // context left null — only RollDiceAction needs it, tested separately
    }

    // ── Partition tests: one test per valid command ────────────────────────

    // PARTITION TESTING
    @Test
    void testRollParsed() {
        Action a = parser.parse("roll");
        assertInstanceOf(RollDiceAction.class, a, "\"roll\" should produce RollDiceAction");
    }

    // PARTITION TESTING
    @Test
    void testRollCaseInsensitive() {
        Action a = parser.parse("ROLL");
        assertInstanceOf(RollDiceAction.class, a, "\"ROLL\" (uppercase) should also parse correctly");
    }

    // PARTITION TESTING
    @Test
    void testGoParsed() {
        Action a = parser.parse("go");
        assertInstanceOf(EndTurnAction.class, a, "\"go\" should produce EndTurnAction");
    }

    // PARTITION TESTING
    @Test
    void testListParsed() {
        Action a = parser.parse("list");
        assertInstanceOf(ListBoardAction.class, a, "\"list\" should produce ListBoardAction");
    }

    // PARTITION TESTING
    @Test
    void testBuildSettlementValidNode() {
        // Node 0 exists on every board
        Action a = parser.parse("build settlement 0");
        assertInstanceOf(BuildSettlementAction.class, a, "\"build settlement 0\" should produce BuildSettlementAction");
    }

    // PARTITION TESTING
    @Test
    void testBuildCityValidNode() {
        Action a = parser.parse("build city 0");
        assertInstanceOf(UpgradeToCityAction.class, a, "\"build city 0\" should produce UpgradeToCityAction");
    }

    // PARTITION TESTING
    @Test
    void testBuildRoadValidEdge() {
        // Find a real edge and use its endpoint node IDs
        Edge edge = board.getEdges().values().iterator().next();
        int a = edge.getA().getId();
        int b = edge.getB().getId();
        Action action = parser.parse("build road " + a + " " + b);
        assertInstanceOf(BuildRoadAction.class, action, "build road with valid endpoint IDs should produce BuildRoadAction");
    }

    // ── Boundary tests ─────────────────────────────────────────────────────

    // BOUNDARY TESTING
    @Test
    void testNullInputReturnsNull() {
        assertNull(parser.parse(null), "null input should return null");
    }

    // BOUNDARY TESTING
    @Test
    void testBlankInputReturnsNull() {
        assertNull(parser.parse("   "), "blank/whitespace input should return null");
    }

    // BOUNDARY TESTING
    @Test
    void testUnknownCommandReturnsNull() {
        assertNull(parser.parse("trade"), "unknown command should return null");
    }

    // BOUNDARY TESTING
    @Test
    void testBuildSettlementInvalidNodeId() {
        // Node 9999 does not exist
        Action a = parser.parse("build settlement 9999");
        assertNull(a, "build settlement with non-existent node ID should return null");
    }

    // BOUNDARY TESTING
    @Test
    void testBuildRoadNonExistentEdge() {
        // Nodes 0 and 9999 are not connected
        Action a = parser.parse("build road 0 9999");
        assertNull(a, "build road between unconnected nodes should return null");
    }

    // BOUNDARY TESTING — extra whitespace should still parse
    @Test
    void testExtraWhitespaceRoll() {
        // The regex uses \\s+ so leading/trailing spaces are handled by trim()
        Action a = parser.parse("  roll  ");
        assertInstanceOf(RollDiceAction.class, a, "\"  roll  \" with surrounding spaces should parse correctly");
    }
}
