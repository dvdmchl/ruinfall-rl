package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SelectionVisibilityClearTest {
    @Test
    void selectionClearsWhenObjectNotVisible() {
        InteractiveRegistry reg = new InteractiveRegistry();
        var enemy = new EnemyObjectAdapter("e1","Goblin","HP 5/5",2,3);
        reg.register(enemy);
        SelectionManager sm = new SelectionManager();
        sm.select(enemy);
        assertEquals("e1", sm.getState().selectedId());
        sm.validateSelection(id -> false); // not visible
        assertNull(sm.getState().selectedId());
    }
}
