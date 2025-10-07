package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ESCPauseMenuNavigationTest {
    @Test
    void submenuBack() {
        NavigationStateService nav = new NavigationStateService();
        OverlayStackService overlays = new OverlayStackService();
        DialogState dialog = new DialogState();
        ESCDebounceState debounce = new ESCDebounceState(200);
        TelemetryLogger telemetry = new TelemetryLogger(TelemetryConfig::isEnabled);
        ESCInputHandler handler = new ESCInputHandler(nav, overlays, dialog, debounce, telemetry);
        handler.handleEscAt(0); // open menu depth=1
        nav.openPauseMenu(); // simulate entering submenu depth=2
        assertEquals(2, nav.getMenuDepth());
        var backAction = handler.handleEscAt(250);
        assertEquals(EscActionType.SUBMENU_BACK, backAction);
        assertEquals(1, nav.getMenuDepth());
        var resumeAction = handler.handleEscAt(500);
        assertEquals(EscActionType.RESUME_GAMEPLAY, resumeAction);
        assertFalse(nav.isPauseMenuOpen());
    }
}
