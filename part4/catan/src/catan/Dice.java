package catan;

import java.util.Random;

public class Dice {
	private Random rng;

	public Dice() {
		rng = new Random();
	}

	public int roll2d6() {
		return rng.nextInt(6) + 1 + rng.nextInt(6) + 1;
	}
}
