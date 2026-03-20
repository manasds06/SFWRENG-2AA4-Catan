package catan;

import java.util.Scanner;

public class HumanAgent extends Agent {

	private CommandParser parser;
	private Scanner scanner;

	public HumanAgent(int id) {
		initAgent(id);
		this.parser  = new CommandParser();
		this.scanner = new Scanner(System.in);
	}

	@Override
	public Action chooseAction(Board b) {
		return parseAction(readInput(), b);
	}

	public String readInput() {
		System.out.print("Player " + getId() + " > ");
		if (scanner.hasNextLine()) return scanner.nextLine().trim();
		return "";
	}

	public Action parseAction(String input, Board b) {
		parser.setBoard(b);
		return parser.parse(input);
	}

	public CommandParser getParser() {
		return parser;
	}
}
