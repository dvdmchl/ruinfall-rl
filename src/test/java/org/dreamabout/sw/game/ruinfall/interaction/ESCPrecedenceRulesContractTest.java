package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.EscActionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Contract test (T006 updated): Precedence resolution ordering. */
class ESCPrecedenceRulesContractTest {

    @Test
    @DisplayName("Dialog dismissal has highest precedence over overlays and others")
    void dialogHighest() {
        EscActionType action = EscPrecedenceResolver.resolve(true, true, true, false, true, 2);
        assertEquals(EscActionType.DIALOG_DISMISS, action);
    }

    @Test
    @DisplayName("Overlay close precedes cutscene interrupt and menu open")
    void overlayBeforeCutsceneOrMenu() {
        EscActionType action = EscPrecedenceResolver.resolve(false, true, true, false, false, 0);
        assertEquals(EscActionType.OVERLAY_CLOSE, action);
    }

    @Test
    @DisplayName("Transition active returns IGNORED immediately")
    void transitionIgnored() {
        EscActionType action = EscPrecedenceResolver.resolve(false, false, false, true, true, 1);
        assertEquals(EscActionType.IGNORED, action);
    }
}
