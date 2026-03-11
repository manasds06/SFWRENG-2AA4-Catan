package catan;

import java.util.List;

public class ConsoleLogger implements Observer {

	@Override
	public void update(Board b, List<Agent> agents) {
		// TODO: print round summary to stdout
		for (Agent a : agents) {
			System.out.print("[P" + a.getId() + "=" + a.getVictoryPoints() + "] ");
		}
		System.out.println();
	}
}
