package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ESCEndOfRunIntegrationTest {
    @Test
    void endOfRunBehavior() {
        var nav = new NavigationStateService();
        var handler = new ESCInputHandler(nav, new OverlayStackService(), new DialogState(), new ESCDebounceState(200), new TelemetryLogger(TelemetryConfig::isEnabled));
        var open = handler.handleEscAt(0);
        assertEquals(EscActionType.OPEN_MENU, open);
        var resume = handler.handleEscAt(250);
        assertEquals(EscActionType.RESUME_GAMEPLAY, resume);
        assertFalse(nav.isPauseMenuOpen());
    }
}
