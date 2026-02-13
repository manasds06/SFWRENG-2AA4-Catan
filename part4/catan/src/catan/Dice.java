package catan;

import java.util.Random;

public class Dice {
	private Random rng;

	public Dice() {
		rng = new Random();
	}

	public int roll() {
		return rng.nextInt(6) + 1 + rng.nextInt(6) + 1;
	}
}
