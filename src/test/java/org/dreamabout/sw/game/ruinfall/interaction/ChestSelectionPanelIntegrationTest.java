package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChestSelectionPanelIntegrationTest {

    @Test
    void selectingChestPopulatesSidePanel() {
        InteractiveRegistry reg = new InteractiveRegistry();
        var chest = new Chest("c1","Wooden Chest","Old and dusty",2,2);
        reg.register(chest);
        var ui = new InteractionUIController(reg, new SelectionManager(), new HoverManager());
        ui.onLeftClickTile(2,2, id -> true);
        var panel = ui.getSidePanelViewModel();
        assertEquals("c1", panel.selectedObjectId());
        assertTrue(panel.headerName().contains("Chest"));
        assertFalse(panel.actions().isEmpty());
    }
}

