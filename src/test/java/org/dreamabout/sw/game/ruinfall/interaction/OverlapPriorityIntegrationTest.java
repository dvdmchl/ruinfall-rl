package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * T015: Integration test for overlapping objects priority resolution.
 */
class OverlapPriorityIntegrationTest {

    @Test
    void overlapResolvesToPriorityEnemyNpcChest() {
        InteractiveRegistry reg = new InteractiveRegistry();
        reg.register(new Chest("c1","Chest","Loot",7,7));
        reg.register(new NPC("n1","NPC","Friendly",7,7));
        reg.register(new EnemyObjectAdapter("e1","Goblin","HP",7,7));
        var ui = new InteractionUIController(reg, new SelectionManager(), new HoverManager());
        ui.onLeftClickTile(7,7, id -> true);
        assertEquals("e1", ui.getSidePanelViewModel().selectedObjectId());
    }
}
