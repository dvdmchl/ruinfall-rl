package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * T004: Contract test placeholder.
 * Expected (from contracts/interaction-events.md): 8 semantic events - HoverTileChanged, HoverStackCycled,
 * ObjectSelected, SelectionCleared, ContextMenuOpened, ContextMenuClosed, StackMenuOpened, PlaceholderActionInvoked.
 *
 * This test intentionally fails until an event representation (enum / constants + dispatcher) is implemented.
 */
class InteractionEventsContractTest {

    @Test
    void eventsEnumContainsAllContractEntries() {
        EnumSet<InteractionEvent> all = EnumSet.allOf(InteractionEvent.class);
        assertEquals(8, all.size(), "Expected 8 interaction events");
        for (InteractionEvent e : InteractionEvent.values()) {
            assertNotNull(e.name());
        }
        // Spot check a few specific ones
        assertTrue(all.contains(InteractionEvent.HoverTileChanged));
        assertTrue(all.contains(InteractionEvent.PlaceholderActionInvoked));
    }
}
