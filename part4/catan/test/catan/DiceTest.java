package catan;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Dice class.
 * Covers boundary testing (valid roll range) and partition testing
 * (distribution sanity).
 */
public class DiceTest {

    /**
     * Boundary test: every roll of two six-sided dice must be in [2, 12].
     */
    @Test
    void testRollRange() {
        Dice dice = new Dice();
        for (int i = 0; i < 1000; i++) {
            int result = dice.roll();
            assertTrue(result >= 2 && result <= 12,
                    "Roll " + result + " is outside the valid range [2,12]");
        }
    }

    /**
     * Partition test: with 2d6 the value 7 should appear more frequently than 2 or
     * 12.
     */
    @Test
    void testRollDistributionNotUniform() {
        Dice dice = new Dice();
        int countSeven = 0;
        int countTwo = 0;
        int trials = 10_000;
        for (int i = 0; i < trials; i++) {
            int r = dice.roll();
            if (r == 7)
                countSeven++;
            if (r == 2)
                countTwo++;
        }
        assertTrue(countSeven > countTwo,
                "7 should appear more often than 2 (got 7:" + countSeven + ", 2:" + countTwo + ")");
    }
}
