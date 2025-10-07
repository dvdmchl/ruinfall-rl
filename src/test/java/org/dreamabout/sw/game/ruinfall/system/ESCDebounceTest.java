package org.dreamabout.sw.game.ruinfall.system;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** T008: Debounce behavior: <200ms ignored, >=200ms accepted. */
public class ESCDebounceTest {

    @Test
    @DisplayName("Debounce rejects rapid presses under 200ms and accepts at threshold")
    void debounceThreshold() {
        ESCDebounceState state = new ESCDebounceState(200); // expected constructor (interval ms)
        long t0 = 1_000_000L;
        assertTrue(state.tryAccept(t0), "First press should be accepted");
        assertFalse(state.tryAccept(t0 + 100), "Second press too soon should be rejected");
        assertTrue(state.tryAccept(t0 + 200), "Press at threshold should be accepted");
    }
}

