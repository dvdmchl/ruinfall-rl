package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * T010: Integration placeholder for clearing selection on empty tile click.
 */
class EmptyTileDeselectIntegrationTest {

    @Test
    void emptyTileClickClearsSelection() {
        InteractiveRegistry reg = new InteractiveRegistry();
        var npc = new NPC("n1","Guide","Helpful",1,1);
        reg.register(npc);
        var ui = new InteractionUIController(reg, new SelectionManager(), new HoverManager());
        ui.onLeftClickTile(1,1, id -> true);
        assertEquals("n1", ui.getSidePanelViewModel().selectedObjectId());
        ui.onLeftClickTile(9,9, id -> true); // empty tile
        assertNull(ui.getSidePanelViewModel().selectedObjectId());
    }
}
