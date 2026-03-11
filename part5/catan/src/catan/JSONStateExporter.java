package catan;

import java.util.List;

public class JSONStateExporter implements Observer {

	private String outputFilePath;

	public JSONStateExporter(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	@Override
	public void update(Board b, List<Agent> agents) {
		writeToJson(b, agents);
	}

	private void writeToJson(Board b, List<Agent> agents) {
		// TODO: serialize board state and agent VP totals to JSON at outputFilePath
	}
}
