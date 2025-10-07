package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * T011: Integration placeholder for right-click selecting and opening context menu.
 */
class RightClickContextMenuIntegrationTest {

    @Test
    void rightClickSelectsAndOpensContextMenu() {
        InteractiveRegistry reg = new InteractiveRegistry();
        var enemy = new EnemyObjectAdapter("e1","Goblin","HP",3,3);
        reg.register(enemy);
        var ui = new InteractionUIController(reg, new SelectionManager(), new HoverManager());
        ui.onRightClickTile(3,3, id -> true);
        assertEquals("e1", ui.getSidePanelViewModel().selectedObjectId());
        assertNotNull(ui.getContextMenuModel());
        assertEquals("e1", ui.getContextMenuModel().objectId());
    }
}
