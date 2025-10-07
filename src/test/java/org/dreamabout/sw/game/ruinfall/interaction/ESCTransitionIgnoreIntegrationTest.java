package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ESCTransitionIgnoreIntegrationTest {
    @Test
    void transitionIgnored() {
        var handler = new ESCInputHandler(new NavigationStateService(), new OverlayStackService(), new DialogState(), new ESCDebounceState(200), new TelemetryLogger(TelemetryConfig::isEnabled));
        handler.setTransitionActive(true);
        var action = handler.handleEscAt(0);
        assertEquals(EscActionType.IGNORED, action);
    }
}
