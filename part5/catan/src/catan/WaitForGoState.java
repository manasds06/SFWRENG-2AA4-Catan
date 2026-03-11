package catan;

import java.util.Scanner;

public class WaitForGoState implements TurnState {

	private static final Scanner SCANNER = new Scanner(System.in);

	@Override
	public void handleTurn(CatanSimulator context, Agent a) {
		System.out.println("[Step] Type 'go' to advance to the next turn...");
		while (SCANNER.hasNextLine()) {
			if (SCANNER.nextLine().trim().equalsIgnoreCase("go")) break;
			System.out.println("[Step] Waiting for 'go'...");
		}
		context.setState(new RollingPhase());
	}
}


