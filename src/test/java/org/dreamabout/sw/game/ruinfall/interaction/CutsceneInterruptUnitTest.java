package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** T013: Cutscene active path triggers CUTSCENE_INTERRUPT_OPEN_MENU */
class CutsceneInterruptUnitTest {

    @Test
    @DisplayName("Cutscene active leads to CUTSCENE_INTERRUPT_OPEN_MENU action and opens menu")
    void cutsceneInterrupt() {
        NavigationStateService nav = new NavigationStateService();
        OverlayStackService overlays = new OverlayStackService();
        DialogState dialog = new DialogState();
        ESCDebounceState debounce = new ESCDebounceState(200);
        TelemetryLogger telemetry = new TelemetryLogger(TelemetryConfig::isEnabled);
        ESCInputHandler handler = new ESCInputHandler(nav, overlays, dialog, debounce, telemetry);
        handler.setCutsceneActive(true);
        EscActionType action = handler.handleEscAt(1000L);
        assertEquals(EscActionType.CUTSCENE_INTERRUPT_OPEN_MENU, action);
        assertTrue(nav.isPauseMenuOpen());
    }
}
