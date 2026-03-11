package catan;

import java.util.Scanner;

public class HumanAgent extends Agent {

	private CommandParser parser;
	private Scanner scanner;
	private CatanSimulator context;

	public HumanAgent(int id, CatanSimulator context) {
		initAgent(id);
		this.parser  = new CommandParser();
		this.scanner = new Scanner(System.in);
		this.context = context;
	}

	@Override
	public Action chooseAction(Board b) {
		parser.setBoard(b);
		parser.setContext(context);
		return parser.parse(readCommandLineInput());
	}

	private String readCommandLineInput() {
		System.out.print("Player " + getId() + " > ");
		if (scanner.hasNextLine()) return scanner.nextLine().trim();
		return "";
	}
}


