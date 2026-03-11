package catan;

import java.util.Scanner;

public class HumanAgent extends Agent {

	private CommandParser parser;
	private Scanner scanner;

	public HumanAgent(int id) {
		initAgent(id);
		this.parser = new CommandParser();
		this.scanner = new Scanner(System.in);
	}

	@Override
	public Action chooseAction(Board b) {
		String input = readCommandLineInput();
		return parser.parse(input, b);
	}

	private String readCommandLineInput() {
		System.out.print("Player " + getId() + " > ");
		if (scanner.hasNextLine()) {
			return scanner.nextLine().trim();
		}
		return "";
	}
}
