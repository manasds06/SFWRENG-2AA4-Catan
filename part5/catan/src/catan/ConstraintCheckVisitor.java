package catan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Concrete Visitor that scores candidate actions against R3.3 constraints.
 *
 * Constraints (resolved before value-based actions):
 *   1. More than 7 cards → agent must spend cards (force a build).
 *   2. Two road segments ≤ 2 edges apart → buy road to connect.
 *   3. Opponent's longest road within 1 of agent's → extend to defend.
 *
 * Usage:
 *   visitor = new ConstraintCheckVisitor(board, agent, allAgents);
 *   if (visitor.hasActiveConstraint()) {
 *       action.accept(visitor);
 *       double score = visitor.getScore();
 *   }
 */
public class ConstraintCheckVisitor implements ActionVisitor {

	private final Board board;
	private final Agent agent;
	private final List<Agent> allAgents;
	private double score;

	// Cached constraint checks (computed once at construction)
	private final boolean handLimitExceeded;
	private final boolean roadGapExists;
	private final boolean longestRoadThreatened;
	private final List<Edge> gapEdges;
	private final Set<Edge> longestChainExtendingEdges;

	public ConstraintCheckVisitor(Board board, Agent agent, List<Agent> allAgents) {
		this.board = board;
		this.agent = agent;
		this.allAgents = allAgents;
		this.score = 0.0;

		this.handLimitExceeded = agent.getHandTotal() > 7;
		this.gapEdges = findRoadGapEdges(board, agent, 2);
		this.roadGapExists = !gapEdges.isEmpty();
		this.longestRoadThreatened = isLongestRoadThreatened();
		this.longestChainExtendingEdges = longestRoadThreatened ? findLongestChainExtendingEdges(board, agent) : Collections.emptySet();
	}

	public double getScore() {
		return score;
	}

	/** Returns true if any R3.3 constraint is active. */
	public boolean hasActiveConstraint() {
		return handLimitExceeded || roadGapExists || longestRoadThreatened;
	}

	/**
	 * Settlement spends 4 cards — very effective for hand limit.
	 * Does not help with road gap or longest road threats.
	 */
	@Override
	public void visit(BuildSettlementAction a) {
		score = 0.0;
		if (handLimitExceeded) {
			// Settlement costs 4 cards and earns VP — great for spending down
			score = 1.0;
		}
	}

	/**
	 * Road directly addresses road-gap and longest-road constraints.
	 * Also helps with hand limit (spends 2 cards).
	 */
	@Override
	public void visit(BuildRoadAction a) {
		score = 0.0;
		if (roadGapExists && gapEdges.contains(a.getTarget())) {
			score = 1.0;
		} else if (longestRoadThreatened && longestChainExtendingEdges.contains(a.getTarget())) {
			// Only roads at the open tips of the longest chain defend it
			score = 0.9;
		} else if (handLimitExceeded) {
			// Road spends 2 cards — helps reduce hand, but less than settlement
			score = 0.5;
		}
	}

	/**
	 * City spends 5 cards — excellent for hand limit.
	 * Does not help with road constraints.
	 */
	@Override
	public void visit(UpgradeToCityAction a) {
		score = 0.0;
		if (handLimitExceeded) {
			// City costs 5 cards (3 ORE + 2 WHEAT) and earns VP
			score = 1.0;
		}
	}

	// ── Constraint detection helpers ─────────────────────────────────

	/**
	 * Returns the set of unowned edges that are incident to the open
	 * endpoint nodes of the agent's longest road chain.
	 */
	private Set<Edge> findLongestChainExtendingEdges(Board board, Agent agent) {
		// Gather all agent-owned edges
		Set<Edge> agentEdges = new HashSet<>();
		for (Edge e : board.getEdges().values()) {
			if (e.getOwner() == agent) agentEdges.add(e);
		}
		if (agentEdges.isEmpty()){
			return Collections.emptySet();
		}

		// Find the starting edge that produces the longest DFS path
		Edge bestStart = null;
		int bestLength = 0;

		for (Edge start : agentEdges) {
			int length = dfsRoad(start, agent, new HashSet<>(), agentEdges);
			if (length > bestLength) {
				bestLength = length;
				bestStart = start;
			}
		}

		if (bestStart == null){
			return Collections.emptySet();
		}

		// Re-trace to collect the actual chain
		Set<Edge> chainEdges = new HashSet<>();
		collectLongestChain(bestStart, agent, agentEdges, chainEdges, bestLength);

		// Collect all nodes that are endpoints of edges in the chain
		Set<Node> chainNodes = new HashSet<>();
		for (Edge e : chainEdges) {
			chainNodes.add(e.getA());
			chainNodes.add(e.getB());
		}

		// The "tip" nodes are chain nodes that touch only ONE chain edge
		Set<Edge> extendingEdges = new HashSet<>();
		for (Node n : chainNodes) {
			int chainDegree = 0;
			for (Edge e : n.edges) {
				if (chainEdges.contains(e)) chainDegree++;
			}
			if (chainDegree == 1) {
				// This node is a tip, any unowned adjacent edge extends the chain
				for (Edge e : n.edges) {
					if (e.getOwner() == null) extendingEdges.add(e);
				}
			}
		}
		return extendingEdges;
	}

	/**
	 * DFS helper that also accumulates the edges on the winning path into chain. 
	 * Returns the length of the longest sub-path from current.
	 */
	private static int collectLongestChain(Edge current, Agent agent,
			Set<Edge> agentEdges, Set<Edge> chain, int targetLength) {
		chain.add(current);
		if (chain.size() == targetLength) return targetLength;

		List<Edge> neighbors = new ArrayList<>();
		for (Edge e : current.getA().edges) {
			if (e != current && agentEdges.contains(e) && !chain.contains(e)) neighbors.add(e);
		}
		for (Edge e : current.getB().edges) {
			if (e != current && agentEdges.contains(e) && !chain.contains(e)) neighbors.add(e);
		}

		for (Edge next : neighbors) {
			int result = collectLongestChain(next, agent, agentEdges, chain, targetLength);
			if (result == targetLength) return result;
		}
		chain.remove(current);
		return chain.size();
	}

	/**
	 * R3.3: Check if any opponent's longest road is within 1 of this agent's.
	 * If the agent has length 7 and an opponent has 6 → threatened.
	 */
	private boolean isLongestRoadThreatened() {
		int myLength = getLongestRoadLength(board, agent);
		if (myLength == 0) return false;

		for (Agent other : allAgents) {
			if (other == agent) continue;
			int otherLength = getLongestRoadLength(board, other);
			if (otherLength >= myLength - 1 && otherLength > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Computes the longest connected road chain for a given agent using DFS.
	 */
	public static int getLongestRoadLength(Board board, Agent agent) {
		int max = 0;
		Set<Edge> agentEdges = new HashSet<>();
		for (Edge e : board.getEdges().values()) {
			if (e.getOwner() == agent) agentEdges.add(e);
		}
		for (Edge start : agentEdges) {
			Set<Edge> visited = new HashSet<>();
			int length = dfsRoad(start, agent, visited, agentEdges);
			if (length > max) max = length;
		}
		return max;
	}

	private static int dfsRoad(Edge current, Agent agent, Set<Edge> visited, Set<Edge> agentEdges) {
		visited.add(current);
		int max = 1;

		// Get neighboring edges via both endpoints
		List<Edge> neighbors = new ArrayList<>();
		for (Edge e : current.getA().edges) {
			if (e != current && agentEdges.contains(e) && !visited.contains(e)) neighbors.add(e);
		}
		for (Edge e : current.getB().edges) {
			if (e != current && agentEdges.contains(e) && !visited.contains(e)) neighbors.add(e);
		}

		for (Edge next : neighbors) {
			int length = 1 + dfsRoad(next, agent, visited, agentEdges);
			if (length > max) max = length;
		}
		visited.remove(current);
		return max;
	}

	/**
	 * R3.3: Finds unowned edges that would connect two road segments of the
	 * given agent that are within maxGap hops of each other.
	 *
	 * A "gap edge" is an unowned edge where both endpoints are within maxGap
	 * distance of an agent-owned edge.
	 */
	public static List<Edge> findRoadGapEdges(Board board, Agent agent, int maxGap) {
		List<Edge> result = new ArrayList<>();

		// Collect nodes that are endpoints of agent-owned edges
		Set<Node> agentRoadNodes = new HashSet<>();
		for (Edge e : board.getEdges().values()) {
			if (e.getOwner() == agent) {
				agentRoadNodes.add(e.getA());
				agentRoadNodes.add(e.getB());
			}
		}
		if (agentRoadNodes.isEmpty()) return result;

		// Find road segments (connected components of agent's roads)
		List<Set<Node>> segments = findRoadSegments(board, agent);
		if (segments.size() < 2) return result;

		// For each unowned edge, check if it connects nodes belonging to (or near) different segments
		for (Edge e : board.getEdges().values()) {
			if (e.getOwner() != null) continue;

			int segA = findSegmentBFS(e.getA(), segments, board, agent, maxGap);
			int segB = findSegmentBFS(e.getB(), segments, board, agent, maxGap);

			// If endpoints reach two different segments, this edge helps bridge them
			if (segA != -1 && segB != -1 && segA != segB) {
				result.add(e);
			}
		}
		return result;
	}

	/** Identifies connected components (segments) of an agent's road network. */
	private static List<Set<Node>> findRoadSegments(Board board, Agent agent) {
		Set<Node> visited = new HashSet<>();
		List<Set<Node>> segments = new ArrayList<>();

		for (Edge e : board.getEdges().values()) {
			if (e.getOwner() != agent) continue;
			Node startNode = e.getA();
			if (visited.contains(startNode)) continue;

			// BFS from startNode along agent-owned edges
			Set<Node> component = new HashSet<>();
			Queue<Node> queue = new LinkedList<>();
			queue.add(startNode);
			while (!queue.isEmpty()) {
				Node n = queue.poll();
				if (!component.add(n)) continue;
				for (Edge adj : n.edges) {
					if (adj.getOwner() == agent) {
						Node neighbor = (adj.getA() == n) ? adj.getB() : adj.getA();
						if (!component.contains(neighbor)) queue.add(neighbor);
					}
				}
			}
			visited.addAll(component);
			segments.add(component);
		}
		return segments;
	}

	/**
	 * BFS from a node, following unowned + agent-owned edges up to maxDist hops.
	 * Returns the index of the first road segment reached, or -1 if none.
	 */
	private static int findSegmentBFS(Node start, List<Set<Node>> segments, Board board, Agent agent, int maxDist) {
		Queue<Node> queue = new LinkedList<>();
		Set<Node> visited = new HashSet<>();
		Queue<Integer> depths = new LinkedList<>();
		queue.add(start);
		depths.add(0);
		while (!queue.isEmpty()) {
			Node n = queue.poll();
			int depth = depths.poll();
			if (!visited.add(n)) continue;

			for (int i = 0; i < segments.size(); i++) {
				if (segments.get(i).contains(n)) return i;
			}
			if (depth >= maxDist) continue;

			for (Edge e : n.edges) {
				Node neighbor = (e.getA() == n) ? e.getB() : e.getA();
				if (!visited.contains(neighbor)) {
					queue.add(neighbor);
					depths.add(depth + 1);
				}
			}
		}
		return -1;
	}
}
