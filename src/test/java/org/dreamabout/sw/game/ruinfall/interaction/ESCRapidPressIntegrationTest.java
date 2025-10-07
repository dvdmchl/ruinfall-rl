package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ESCRapidPressIntegrationTest {
    @Test
    void rapidPressIdempotentOpen() {
        var handler = new ESCInputHandler(new NavigationStateService(), new OverlayStackService(), new DialogState(), new ESCDebounceState(200), new TelemetryLogger(TelemetryConfig::isEnabled));
        var open = handler.handleEscAt(0);
        assertEquals(EscActionType.OPEN_MENU, open);
        var ignored = handler.handleEscAt(50);
        assertEquals(EscActionType.IGNORED, ignored);
        var resume = handler.handleEscAt(250);
        assertEquals(EscActionType.RESUME_GAMEPLAY, resume);
    }
}
