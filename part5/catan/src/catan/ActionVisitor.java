package catan;

/**
 * Visitor interface for the Action hierarchy (Visitor pattern).
 * 
 * Each visit method evaluates a specific action type. Results are stored
 * in the visitor's internal state and retrieved via getters — visit/accept
 * are strictly void per the canonical Visitor pattern.
 */
public interface ActionVisitor {
	void visit(BuildSettlementAction a);
	void visit(BuildRoadAction a);
	void visit(UpgradeToCityAction a);
}
