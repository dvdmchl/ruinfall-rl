package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** T020: Cutscene interrupt path (failing skeleton) */
class ESCCutsceneInterruptIntegrationTest {
    @Test
    void cutsceneInterrupt() {
        var handler = new ESCInputHandler(new NavigationStateService(), new OverlayStackService(), new DialogState(), new ESCDebounceState(200), new TelemetryLogger(TelemetryConfig::isEnabled));
        handler.setCutsceneActive(true);
        var action = handler.handleEscAt(0);
        assertEquals(EscActionType.CUTSCENE_INTERRUPT_OPEN_MENU, action);
    }
}
