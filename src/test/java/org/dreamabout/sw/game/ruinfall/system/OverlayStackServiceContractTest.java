package org.dreamabout.sw.game.ruinfall.system;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Contract test (T005 updated): Overlay stack behavior. */
class OverlayStackServiceContractTest {

    @Test
    @DisplayName("Push/pop overlays updates counts and pause counting")
    void overlayPushPop() {
        OverlayStackService svc = new OverlayStackService();
        svc.pushOverlay("map", true);
        svc.pushOverlay("quest", false);
        assertEquals(2, svc.getOverlayCount());
        assertEquals(1, svc.getPauseOverlayCount());
        assertTrue(svc.hasOverlays());
        String popped = svc.popOverlay();
        assertEquals("quest", popped);
        assertEquals(1, svc.getOverlayCount());
        assertEquals(1, svc.getPauseOverlayCount());
    }
}
