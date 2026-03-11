package catan;

public class CommandParser {

	/**
	 * Parses a command string typed by a human player into an Action.
	 * Expected formats (case-insensitive):
	 *   "build settlement <nodeId>"
	 *   "build road <edgeId>"
	 *   "build city <nodeId>"
	 *   "roll"
	 *   "list"
	 *   "end"
	 *
	 * @param input  raw console input string
	 * @param board  current board (needed to resolve node/edge IDs)
	 * @return the corresponding Action, or null if unparseable
	 */
	public Action parse(String input, Board board) {
		// TODO: tokenize input and return matching Action subclass
		return null;
	}
}
