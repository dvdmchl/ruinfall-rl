package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ESCDialogDismissIntegrationTest {
    @Test
    void dialogDismiss() {
        var nav = new NavigationStateService();
        var overlays = new OverlayStackService();
        var dialog = new DialogState();
        var debounce = new ESCDebounceState(200);
        var telemetry = new TelemetryLogger(TelemetryConfig::isEnabled);
        var handler = new ESCInputHandler(nav, overlays, dialog, debounce, telemetry);
        dialog.setOpen(true);
        overlays.pushOverlay("map", true); // should lose precedence to dialog
        var action = handler.handleEscAt(0);
        assertEquals(EscActionType.DIALOG_DISMISS, action);
        assertFalse(dialog.isOpen());
    }
}
