package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.EscActionType;
import org.dreamabout.sw.game.ruinfall.system.TelemetryConfig;
import org.dreamabout.sw.game.ruinfall.system.TelemetryLogger;
import org.dreamabout.sw.game.ruinfall.system.NavigationStateService;
import org.dreamabout.sw.game.ruinfall.system.OverlayStackService;
import org.dreamabout.sw.game.ruinfall.system.DialogState;
import org.dreamabout.sw.game.ruinfall.system.ESCDebounceState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Contract test (T003 updated): Validates ESCInputHandler public behavior surface. */
class ESCInputHandlerContractTest {

    @Test
    @DisplayName("ESCInputHandler returns exactly one non-IGNORED action per initial press and IGNORED for rapid repeat")
    void singleActionPerPress() {
        TelemetryConfig.setEnabled(true);
        ESCInputHandler handler = new ESCInputHandler(
                new NavigationStateService(),
                new OverlayStackService(),
                new DialogState(),
                new ESCDebounceState(200),
                new TelemetryLogger(TelemetryConfig::isEnabled)
        );
        EscActionType first = handler.handleEsc();
        assertNotNull(first);
        assertNotEquals(EscActionType.IGNORED, first);
        EscActionType secondImmediate = handler.handleEsc();
        assertEquals(EscActionType.IGNORED, secondImmediate, "Second rapid press should be debounced");
    }
}
