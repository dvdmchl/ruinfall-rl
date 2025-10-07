package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * T012: Integration placeholder for ignoring non-visible objects during interaction.
 */
class NonVisibleInteractionGuardIntegrationTest {

    @Test
    void nonVisibleObjectIgnored() {
        InteractiveRegistry reg = new InteractiveRegistry();
        var chest = new Chest("c1","Chest","Loot",5,5);
        reg.register(chest);
        var ui = new InteractionUIController(reg, new SelectionManager(), new HoverManager());
        ui.onLeftClickTile(5,5, id -> false); // not visible -> selection should not persist
        assertNull(ui.getSidePanelViewModel().selectedObjectId());
        ui.onRightClickTile(5,5, id -> false);
        assertNull(ui.getContextMenuModel());
    }
}
