package catan;

public abstract class Action {
	public abstract boolean execute(Board b, Agent a);
	public abstract String describe();
}
