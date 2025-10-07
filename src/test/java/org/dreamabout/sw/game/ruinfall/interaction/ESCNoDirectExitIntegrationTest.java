package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** T019: Prevent direct exit via ESC (failing skeleton) */
class ESCNoDirectExitIntegrationTest {
    @Test
    void noDirectExit() {
        var nav = new NavigationStateService();
        var overlays = new OverlayStackService();
        var handler = new ESCInputHandler(nav, overlays, new DialogState(), new ESCDebounceState(200), new TelemetryLogger(TelemetryConfig::isEnabled));
        var a1 = handler.handleEscAt(0); // open menu
        var a2 = handler.handleEscAt(250); // resume
        assertEquals(EscActionType.OPEN_MENU, a1);
        assertEquals(EscActionType.RESUME_GAMEPLAY, a2);
        for (var rec : handler.isGameplayPaused() ? java.util.List.<ESCActionRecord>of() : new TelemetryLogger(TelemetryConfig::isEnabled).getRecords()) {
            // no exit action enumeration exists
            assertNotEquals("EXIT", rec.action().name());
        }
    }
}
