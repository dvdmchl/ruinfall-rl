package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.EscActionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** T009: Precedence matrix coverage */
public class ESCPrecedenceTest {

    @Test
    @DisplayName("Dialog dismissal wins over overlay close")
    void dialogBeatsOverlay() {
        EscActionType action = EscPrecedenceResolver.resolve(true, true, false, false, true, 1);
        assertEquals(EscActionType.DIALOG_DISMISS, action);
    }

    @Test
    @DisplayName("Overlay close beats cutscene interrupt and menu open")
    void overlayBeatsCutsceneAndMenu() {
        EscActionType action = EscPrecedenceResolver.resolve(false, true, true, false, false, 0);
        assertEquals(EscActionType.OVERLAY_CLOSE, action);
    }
}

