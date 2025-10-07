package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * T013: Integration placeholder for outside click closing context menu.
 */
class ContextMenuOutsideClickIntegrationTest {

    @Test
    void outsideClickClosesMenu() {
        InteractiveRegistry reg = new InteractiveRegistry();
        var npc = new NPC("n1","NPC","Friendly",4,4);
        reg.register(npc);
        var ui = new InteractionUIController(reg, new SelectionManager(), new HoverManager());
        ui.onRightClickTile(4,4, id -> true);
        assertNotNull(ui.getContextMenuModel());
        ui.onOutsideClick(); // only closes menu
        assertNull(ui.getContextMenuModel());
        assertEquals("n1", ui.getSidePanelViewModel().selectedObjectId()); // selection unchanged
    }
}
