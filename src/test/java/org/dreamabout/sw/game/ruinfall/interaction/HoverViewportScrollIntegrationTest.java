package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HoverViewportScrollIntegrationTest {

    @Test @DisplayName("Viewport horizontal scroll shifts hovered tile via stored cursor recompute")
    void viewportScrollChangesHover() {
        InteractiveRegistry reg = new InteractiveRegistry();
        HoverManager hm = new HoverManager();
        reg.addListener(hm);
        reg.register(new EnemyObjectAdapter("eA","A","HP",3,2));
        reg.register(new EnemyObjectAdapter("eB","B","HP",4,2));
        InteractionUIController ui = new InteractionUIController(reg, new SelectionManager(), hm);
        ui.setTileSize(32);
        // Simulate initial cursor at scene (x= (3*32)+5 ) with viewport 0 so hover tile=3
        double sceneX = 3*32 + 5; double sceneY = 2*32 + 8;
        ui.setLastMouseScenePosition(sceneX, sceneY);
        ui.refreshHoverFromStoredCursor(0,0);
        assertEquals("eA", hm.getState().hoveredId());
        // Scroll viewport left by 32 px (content moves right) so world offset increases -> tile should become 4
        ui.refreshHoverFromStoredCursor(32,0);
        assertEquals(4, hm.getState().tileX());
        assertEquals("eB", hm.getState().hoveredId());
    }
}
