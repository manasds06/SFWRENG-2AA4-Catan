package catan;

import java.util.Scanner;

/**
 * Step-forward state (R2.4): the simulator pauses here between turns and waits
 * for the human to type "go" on the console before proceeding.
 * In fully-automated (no-human) runs this state is bypassed because ActionPhase
 * resets directly to RollingPhase.
 */
public class WaitForGoState implements TurnState {

	private static final Scanner SCANNER = new Scanner(System.in);

	@Override
	public void handleTurn(CatanSimulator context, Agent a) {
		System.out.println("[Step] Type 'go' to advance to the next turn...");
		while (true) {
			if (SCANNER.hasNextLine()) {
				String line = SCANNER.nextLine().trim();
				if (line.equalsIgnoreCase("go")) {
					break;
				}
				System.out.println("[Step] Waiting for 'go'...");
			}
		}
		// Advance to rolling phase for the next agent
		context.setState(new RollingPhase());
	}
}

