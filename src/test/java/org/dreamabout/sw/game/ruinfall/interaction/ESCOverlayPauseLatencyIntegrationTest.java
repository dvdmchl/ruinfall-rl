package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ESCOverlayPauseLatencyIntegrationTest {
    @Test
    void overlayPauseLatency() {
        var overlays = new OverlayStackService();
        overlays.pushOverlay("map", true);
        var handler = new ESCInputHandler(new NavigationStateService(), overlays, new DialogState(), new ESCDebounceState(200), new TelemetryLogger(TelemetryConfig::isEnabled));
        long start = System.nanoTime();
        var action = handler.handleEscAt(0);
        long durationMs = (System.nanoTime() - start) / 1_000_000;
        assertEquals(EscActionType.OVERLAY_CLOSE, action);
        assertTrue(durationMs < 100, "Expected overlay close <100ms but was " + durationMs);
    }
}
