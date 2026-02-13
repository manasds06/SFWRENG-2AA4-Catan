package catan;

import java.util.Random;

/**
 * Represents a pair of six-sided dice used in Catan.
 * Rolls two dice and returns their sum (range 2-12).
 */
public class Dice {
    private Random rng;

    public Dice() {
        this.rng = new Random();
    }

    /**
     * Rolls two six-sided dice and returns their sum.
     * 
     * @return sum of two dice rolls (2-12)
     */
    public int roll2d6() {
        int die1 = rng.nextInt(6) + 1;
        int die2 = rng.nextInt(6) + 1;
        return die1 + die2;
    }
}
