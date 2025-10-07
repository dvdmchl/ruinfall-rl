package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * T014: Integration placeholder for side panel updating when selection switches.
 */
class SelectionSwitchPanelUpdateIntegrationTest {

    @Test
    void switchingSelectionUpdatesPanel() {
        InteractiveRegistry reg = new InteractiveRegistry();
        var chest = new Chest("c1","Chest","Loot",1,1);
        var npc = new NPC("n1","NPC","Friendly",2,2);
        reg.register(chest);
        reg.register(npc);
        var ui = new InteractionUIController(reg, new SelectionManager(), new HoverManager());
        ui.onLeftClickTile(1,1, id -> true);
        assertEquals("c1", ui.getSidePanelViewModel().selectedObjectId());
        ui.onLeftClickTile(2,2, id -> true);
        assertEquals("n1", ui.getSidePanelViewModel().selectedObjectId());
    }
}
