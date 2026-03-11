package catan;

import java.security.SecureRandom;

public class Dice {
	private SecureRandom rng;

	public Dice() {
		rng = new SecureRandom();
	}

	public int roll() {
		return rng.nextInt(6) + 1 + rng.nextInt(6) + 1;
	}
}
