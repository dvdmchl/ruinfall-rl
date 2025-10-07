package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NameplatePlacementTest {

    @Test @DisplayName("Nameplate updates after enemy moves onto hovered tile without mouse move")
    void nameplateUpdatesAfterMovement() {
        InteractiveRegistry reg = new InteractiveRegistry();
        HoverManager hm = new HoverManager();
        reg.addListener(hm);
        InteractionUIController ui = new InteractionUIController(reg, new SelectionManager(), hm);
        ui.setTileSize(16);
        // Hover empty tile (2,2)
        hm.onHoverTile(2,2, reg);
        assertNull(ui.getNameplateModel(), "Precondition: no nameplate on empty tile");
        var enemy = new EnemyObjectAdapter("e1","Enemy","HP",1,1);
        reg.register(enemy);
        enemy.setTilePosition(2,2);
        reg.moveObject("e1",2,2);
        var model = ui.getNameplateModel();
        assertNotNull(model, "Nameplate should appear after movement");
        assertEquals("e1", model.objectId());
    }
}
