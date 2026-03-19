package catan;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * An intelligent agent that uses the Visitor pattern to evaluate and choose
 * the best action on its turn.
 *
 * Decision flow:
 *   1. Generate all valid candidate actions (settlements, roads, cities).
 *   2. R3.3 constraints first: if any constraint is active, score candidates
 *      with ConstraintCheckVisitor and pick the highest.
 *   3. R3.2 value scoring: score remaining candidates with ValueEvaluationVisitor
 *      and pick the highest (ties broken randomly).
 *   4. Fallback: return null if nothing is possible.
 */
public class StrategicAgent extends Agent {

	private MoveValidator validator;
	private SecureRandom rng;

	public StrategicAgent(int id, MoveValidator validator) {
		initAgent(id);
		this.validator = validator;
		this.rng = new SecureRandom();
	}

	@Override
	public Action chooseAction(Board board) {
		// ── Step 1: Generate all valid candidate actions ─────────────────

		List<Action> candidates = new ArrayList<>();

		// Candidate settlements
		List<Node> settlementNodes = board.getAvailableNodesForSettlement(this);
		for (Node n : settlementNodes) {
			if (validator.canPlaceSettlement(board, this, n)) {
				candidates.add(new BuildSettlementAction(n));
			}
		}

		// Candidate cities
		for (Node n : board.getNodes().values()) {
			if (validator.canUpgradeToCity(board, this, n)) {
				candidates.add(new UpgradeToCityAction(n));
			}
		}

		// Candidate roads
		List<Edge> roadEdges = board.getAvailableEdgesForRoad(this);
		for (Edge e : roadEdges) {
			if (validator.canPlaceRoad(board, this, e)) {
				candidates.add(new BuildRoadAction(e));
			}
		}

		if (candidates.isEmpty()) {
			return null;
		}

		// ── Step 2: R3.3 — Check constraints first ──────────────────────

		// We need a reference to all agents; get it from the board's nodes
		List<Agent> allAgents = collectAgents(board);
		ConstraintCheckVisitor constraintVisitor =
				new ConstraintCheckVisitor(board, this, allAgents);

		if (constraintVisitor.hasActiveConstraint()) {
			Action best = pickBestAction(candidates, constraintVisitor);
			if (best != null) return best;
			// If no candidate addresses the constraint, fall through to value scoring
		}

		// ── Step 3: R3.2 — Value-based scoring ──────────────────────────

		ValueEvaluationVisitor valueVisitor = new ValueEvaluationVisitor(board, this);
		Action best = pickBestAction(candidates, valueVisitor);
		if (best != null) return best;

		// ── Step 4: Fallback — random pick ──────────────────────────────
		return candidates.get(rng.nextInt(candidates.size()));
	}

	/**
	 * Scores every candidate using the given visitor (via double dispatch)
	 * and returns the best. Ties are broken randomly.
	 */
	private Action pickBestAction(List<Action> candidates, ActionVisitor visitor) {
		double bestScore = -1;
		List<Action> bestActions = new ArrayList<>();

		for (Action action : candidates) {
			action.accept(visitor);

			double actionScore;
			if (visitor instanceof ValueEvaluationVisitor) {
				actionScore = ((ValueEvaluationVisitor) visitor).getScore();
			} else {
				actionScore = ((ConstraintCheckVisitor) visitor).getScore();
			}

			if (actionScore > bestScore) {
				bestScore = actionScore;
				bestActions.clear();
				bestActions.add(action);
			} else if (actionScore == bestScore) {
				bestActions.add(action);
			}
		}

		if (bestScore <= 0 || bestActions.isEmpty()) {
			return null;
		}

		// Tie-breaking: random pick among equals
		return bestActions.get(rng.nextInt(bestActions.size()));
	}

	/**
	 * Collects all distinct agents that have placed buildings on the board.
	 */
	private List<Agent> collectAgents(Board board) {
		List<Agent> agents = new ArrayList<>();
		for (Node n : board.getNodes().values()) {
			Agent owner = n.getOwner();
			if (owner != null && !agents.contains(owner)) {
				agents.add(owner);
			}
		}
		// Make sure this agent is included even if it has no buildings yet
		if (!agents.contains(this)) {
			agents.add(this);
		}
		return agents;
	}
}
