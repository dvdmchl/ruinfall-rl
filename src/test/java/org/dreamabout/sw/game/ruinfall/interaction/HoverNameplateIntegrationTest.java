package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * T008: Integration placeholder for showing nameplate on hover.
 */
class HoverNameplateIntegrationTest {

    @Test
    void hoveringVisibleObjectShowsNameplate() {
        InteractiveRegistry reg = new InteractiveRegistry();
        var enemy = new EnemyObjectAdapter("e1","Goblin","HP 5/5",4,4);
        reg.register(enemy);
        InteractionUIController ui = new InteractionUIController(reg, new SelectionManager(), new HoverManager());
        ui.onMouseMoveTile(4,4);
        NameplateModel model = ui.getNameplateModel();
        assertNotNull(model);
        assertEquals("e1", model.objectId());
        assertEquals(2, model.lines().size());
    }
}
