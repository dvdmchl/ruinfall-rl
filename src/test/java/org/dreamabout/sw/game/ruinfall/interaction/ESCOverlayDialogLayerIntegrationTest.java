package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ESCOverlayDialogLayerIntegrationTest {
    @Test
    void overlayDialogLayering() {
        var overlays = new OverlayStackService();
        overlays.pushOverlay("map", true);
        var dialog = new DialogState();
        dialog.setOpen(true);
        var handler = new ESCInputHandler(new NavigationStateService(), overlays, dialog, new ESCDebounceState(200), new TelemetryLogger(TelemetryConfig::isEnabled));
        var action = handler.handleEscAt(0);
        assertEquals(EscActionType.DIALOG_DISMISS, action);
        assertFalse(dialog.isOpen());
        assertEquals(1, overlays.getOverlayCount());
    }
}
