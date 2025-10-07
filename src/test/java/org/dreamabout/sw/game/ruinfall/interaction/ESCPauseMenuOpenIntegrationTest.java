package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ESCPauseMenuOpenIntegrationTest {
    @Test
    void openPauseMenu() {
        NavigationStateService nav = new NavigationStateService();
        OverlayStackService overlays = new OverlayStackService();
        DialogState dialog = new DialogState();
        ESCDebounceState debounce = new ESCDebounceState(200);
        TelemetryLogger telemetry = new TelemetryLogger(TelemetryConfig::isEnabled);
        ESCInputHandler handler = new ESCInputHandler(nav, overlays, dialog, debounce, telemetry);
        assertFalse(nav.isPauseMenuOpen());
        var action = handler.handleEscAt(0);
        assertEquals(EscActionType.OPEN_MENU, action);
        assertTrue(nav.isPauseMenuOpen());
        assertEquals(1, telemetry.getRecords().size());
        assertEquals(EscActionType.OPEN_MENU, telemetry.getRecords().getFirst().action());
    }
}
