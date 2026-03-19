package catan;

import java.util.Stack;

public class CommandHistory {
	private Stack<Action> historyStack;
	private Stack<Action> redoStack;

	public CommandHistory() {
		historyStack = new Stack<>();
		redoStack = new Stack<>();
	}

	public void push(Action a) {
		historyStack.push(a);
		redoStack.clear();
	}

	public Action undo() {
		if (historyStack.isEmpty()) return null;
		Action a = historyStack.pop();
		redoStack.push(a);
		return a;
	}

	public Action redo() {
		if (redoStack.isEmpty()) return null;
		Action a = redoStack.pop();
		historyStack.push(a);
		return a;
	}
}
