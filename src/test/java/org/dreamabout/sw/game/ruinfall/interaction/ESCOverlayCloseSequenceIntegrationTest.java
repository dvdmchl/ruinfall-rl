package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ESCOverlayCloseSequenceIntegrationTest {
    @Test
    void overlayCloseSequence() {
        var nav = new NavigationStateService();
        var overlays = new OverlayStackService();
        var dialog = new DialogState();
        var debounce = new ESCDebounceState(200);
        var telemetry = new TelemetryLogger(TelemetryConfig::isEnabled);
        var handler = new ESCInputHandler(nav, overlays, dialog, debounce, telemetry);
        overlays.pushOverlay("map", true);
        overlays.pushOverlay("quest", false);
        var close1 = handler.handleEscAt(0);
        assertEquals(EscActionType.OVERLAY_CLOSE, close1);
        assertEquals(1, overlays.getOverlayCount());
        var close2 = handler.handleEscAt(250);
        assertEquals(EscActionType.OVERLAY_CLOSE, close2);
        assertEquals(0, overlays.getOverlayCount());
        var openMenu = handler.handleEscAt(500);
        assertEquals(EscActionType.OPEN_MENU, openMenu);
        assertTrue(nav.isPauseMenuOpen());
    }
}
