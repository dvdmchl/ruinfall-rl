package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** T014: Transition active path returns IGNORED */
class TransitionIgnoreUnitTest {

    @Test
    @DisplayName("Active transition causes IGNORED action and no telemetry")
    void transitionIgnored() {
        NavigationStateService nav = new NavigationStateService();
        OverlayStackService overlays = new OverlayStackService();
        DialogState dialog = new DialogState();
        ESCDebounceState debounce = new ESCDebounceState(200);
        TelemetryLogger telemetry = new TelemetryLogger(TelemetryConfig::isEnabled);
        ESCInputHandler handler = new ESCInputHandler(nav, overlays, dialog, debounce, telemetry);
        handler.setTransitionActive(true);
        EscActionType action = handler.handleEscAt(2000L);
        assertEquals(EscActionType.IGNORED, action);
        assertTrue(telemetry.getRecords().isEmpty());
        assertFalse(nav.isPauseMenuOpen());
    }
}
