package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VisibilityInvalidationIntegrationTest {
    @Test
    void selectionClearedWhenVisibilityLost() {
        InteractiveRegistry reg = new InteractiveRegistry();
        var enemy = new EnemyObjectAdapter("e1","Goblin","HP",6,6);
        reg.register(enemy);
        var sm = new SelectionManager();
        var ui = new InteractionUIController(reg, sm, new HoverManager());
        ui.onLeftClickTile(6,6, id -> true);
        assertEquals("e1", ui.getSidePanelViewModel().selectedObjectId());
        // simulate visibility invalidation
        ui.validateVisibility(id -> false);
        assertNull(ui.getSidePanelViewModel().selectedObjectId());
    }
}
