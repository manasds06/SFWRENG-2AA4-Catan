package catan;

import java.security.SecureRandom;

public class Dice {
	private SecureRandom rng;

	public Dice() {
		rng = new SecureRandom();
	}

	public int roll2d6() {
		return rng.nextInt(6) + 1 + rng.nextInt(6) + 1;
	}
}
