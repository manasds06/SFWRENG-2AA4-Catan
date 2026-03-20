package catan;

public abstract class Action {
	public abstract boolean execute(Board b, Agent a);
	public abstract boolean undo(Board b, Agent a);
	public abstract String describe();

	public boolean isUndoable() { return false; }

	/** Visitor pattern: accept a visitor for evaluation (void per canonical form). */
	public void accept(ActionVisitor v) { }
}
