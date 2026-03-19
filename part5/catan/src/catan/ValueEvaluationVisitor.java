package catan;

/**
 * Concrete Visitor that scores candidate actions according to R3.2.
 *
 * Value rules:
 *   - Earning a VP (settlement, city): 1.0
 *   - Building without VP (road):     0.8
 *   - Spending cards so < 5 remain:   0.5 (applied when applicable)
 *
 * Usage:
 *   visitor = new ValueEvaluationVisitor(board, agent);
 *   action.accept(visitor);
 *   double score = visitor.getScore();
 */
public class ValueEvaluationVisitor implements ActionVisitor {

	private final Board board;
	private final Agent agent;
	private double score;

	public ValueEvaluationVisitor(Board board, Agent agent) {
		this.board = board;
		this.agent = agent;
		this.score = 0.0;
	}

	public double getScore() {
		return score;
	}

	/**
	 * Settlement earns 1 VP → value 1.0.
	 * If paying would also bring hand below 5, the base value already exceeds 0.5
	 * so we keep 1.0.
	 */
	@Override
	public void visit(BuildSettlementAction a) {
		score = 1.0;
	}

	/**
	 * City earns 1 VP → value 1.0.
	 */
	@Override
	public void visit(UpgradeToCityAction a) {
		score = 1.0;
	}

	/**
	 * Road earns no VP → base value 0.8.
	 * If paying the road cost would drop hand total below 5, value is adjusted
	 * to max(0.8, 0.5) = 0.8. Since 0.8 > 0.5 the road's normal value always
	 * dominates; the 0.5 "spend-down" value only matters if future action types
	 * have no other benefit.
	 */
	@Override
	public void visit(BuildRoadAction a) {
		int handAfter = agent.getHandTotal() - costTotal(Cost.ROAD);
		if (handAfter < 5) {
			// Spending cards to get below 5 is worth at least 0.5,
			// but road building is already 0.8 which is higher.
			score = Math.max(0.8, 0.5);
		} else {
			score = 0.8;
		}
	}

	/** Sum the total cards required by a Cost object. */
	private int costTotal(Cost c) {
		int total = 0;
		for (int v : c.getRequired().values()) {
			total += v;
		}
		return total;
	}
}
