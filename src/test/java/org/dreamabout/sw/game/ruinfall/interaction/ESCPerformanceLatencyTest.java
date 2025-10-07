package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** T051: Performance guard - typical RESUME_GAMEPLAY path < 100ms */
class ESCPerformanceLatencyTest {
    @Test
    void resumeLatencyUnderBudget() {
        var nav = new NavigationStateService();
        var handler = new ESCInputHandler(nav, new OverlayStackService(), new DialogState(), new ESCDebounceState(1), new TelemetryLogger(TelemetryConfig::isEnabled));
        handler.handleEscAt(0); // open menu
        long start = System.nanoTime();
        var action = handler.handleEscAt(5); // resume
        long ms = (System.nanoTime() - start) / 1_000_000;
        assertEquals(EscActionType.RESUME_GAMEPLAY, action);
        assertTrue(ms < 100, "Resume path exceeded 100ms: " + ms + "ms");
    }
}

