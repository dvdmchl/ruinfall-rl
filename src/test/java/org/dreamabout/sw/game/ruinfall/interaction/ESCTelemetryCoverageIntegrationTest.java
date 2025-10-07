package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.EnumSet;

class ESCTelemetryCoverageIntegrationTest {
    @Test
    void telemetryCoverage() {
        var nav = new NavigationStateService();
        var overlays = new OverlayStackService();
        var dialog = new DialogState();
        var debounce = new ESCDebounceState(1); // very small to simplify
        var telemetry = new TelemetryLogger(TelemetryConfig::isEnabled);
        var handler = new ESCInputHandler(nav, overlays, dialog, debounce, telemetry);

        // 0: Dialog dismissal
        dialog.setOpen(true); handler.handleEscAt(0); // DIALOG_DISMISS
        // 10: Overlay close
        overlays.pushOverlay("map", true); handler.handleEscAt(10); // OVERLAY_CLOSE
        // 20: Cutscene interrupt opens menu
        handler.setCutsceneActive(true); handler.handleEscAt(20); // CUTSCENE_INTERRUPT_OPEN_MENU (menu depth=1)
        // 30: open submenu (depth=2) manually
        nav.openPauseMenu();
        // 40: submenu back
        handler.handleEscAt(40); // SUBMENU_BACK (depth=1)
        // 50: resume gameplay (close menu)
        handler.handleEscAt(50); // RESUME_GAMEPLAY (depth=0)
        // 60: open menu again
        handler.handleEscAt(60); // OPEN_MENU

        var actions = EnumSet.noneOf(EscActionType.class);
        telemetry.getRecords().forEach(r -> actions.add(r.action()));
        assertTrue(actions.contains(EscActionType.DIALOG_DISMISS));
        assertTrue(actions.contains(EscActionType.OVERLAY_CLOSE));
        assertTrue(actions.contains(EscActionType.CUTSCENE_INTERRUPT_OPEN_MENU));
        assertTrue(actions.contains(EscActionType.OPEN_MENU));
        assertTrue(actions.contains(EscActionType.SUBMENU_BACK));
        assertTrue(actions.contains(EscActionType.RESUME_GAMEPLAY));
        assertEquals(6, actions.size(), "Unexpected extra actions recorded: " + actions);
    }
}
