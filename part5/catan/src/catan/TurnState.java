package catan;

public interface TurnState {
	void handleTurn(CatanSimulator context, Agent a);
}
