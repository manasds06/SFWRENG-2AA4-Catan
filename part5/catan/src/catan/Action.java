package catan;

public abstract class Action {
	public abstract boolean execute(Board b, Agent a);
	public abstract String describe();

	/** Visitor pattern: accept a visitor for evaluation (void per canonical form). */
	public abstract void accept(ActionVisitor v);
}
