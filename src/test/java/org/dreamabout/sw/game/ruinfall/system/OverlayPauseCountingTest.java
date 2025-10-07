package org.dreamabout.sw.game.ruinfall.system;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** T010: Overlay pause counting correctness */
public class OverlayPauseCountingTest {

    @Test
    @DisplayName("Pause overlay count reflects paused overlays only")
    void pauseOverlayCounting() {
        OverlayStackService svc = new OverlayStackService();
        assertEquals(0, svc.getPauseOverlayCount());
        svc.pushOverlay("map", true);
        svc.pushOverlay("quest", false);
        svc.pushOverlay("settings", true);
        assertEquals(2, svc.getPauseOverlayCount());
        assertEquals(3, svc.getOverlayCount());
        assertEquals("settings", svc.popOverlay());
        assertEquals(1, svc.getPauseOverlayCount());
    }
}

