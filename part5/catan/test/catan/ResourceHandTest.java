package catan;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the ResourceHand class.
 * Covers boundary testing (empty hand) and partition testing (single / multiple
 * resource types, removal).
 */
public class ResourceHandTest {

    private ResourceHand hand;

    @BeforeEach
    void setUp() {
        hand = new ResourceHand();
    }

    /**
     * Boundary test: a brand-new hand should have exactly 0 total cards.
     */
    // BOUNDARY TESTING
    @Test
    void testNewHandIsEmpty() {
        assertEquals(0, hand.getTotalCards(), "A new ResourceHand should have 0 total cards");
    }

    /**
     * Partition test (single resource type): adding 3 WOOD should be reflected by
     * get().
     */
    // PARTITION TESTING
    @Test
    void testAddAndGetResources() {
        hand.add(ResourceType.WOOD, 3);
        assertEquals(3, hand.get(ResourceType.WOOD), "After adding 3 WOOD, get(WOOD) should return 3");
        assertEquals(0, hand.get(ResourceType.BRICK), "Other resource types should remain 0");
    }

    /**
     * Partition test: removing resources should decrease get() and getTotalCards().
     */
    // PARTITION TESTING
    @Test
    void testRemoveResources() {
        hand.add(ResourceType.WHEAT, 5);
        hand.remove(ResourceType.WHEAT, 3);
        assertEquals(2, hand.get(ResourceType.WHEAT), "After adding 5 and removing 3 WHEAT, should have 2");
        assertEquals(2, hand.getTotalCards(), "Total cards should be 2 after removal");
    }
}
