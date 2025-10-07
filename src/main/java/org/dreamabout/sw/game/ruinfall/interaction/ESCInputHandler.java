package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.*;

/**
 * Central ESC key coordinator.
 * Responsibilities:
 *  - Debounce rapid presses (min interval enforced by ESCDebounceState)
 *  - Determine highest-precedence action via EscPrecedenceResolver
 *  - Apply side effects (dialog dismiss, overlay pop, menu open/back/resume, cutscene interrupt)
 *  - Record telemetry only for meaningful (non-IGNORED / non-NO_OP) actions
 *  - Provide consolidated pause query (menu OR any pause overlay)
 */
@SuppressWarnings({"FieldCanBeLocal","unused"}) // Fields retained for clarity & DI; accessed inside switch / helper methods.
public class ESCInputHandler {
    private final NavigationStateService navigation;
    private final OverlayStackService overlays;
    private final DialogState dialogState;
    private final ESCDebounceState debounce;
    private final TelemetryLogger telemetry;

    private boolean cutsceneActive;
    private boolean transitionActive;

    public ESCInputHandler(NavigationStateService navigation,
                           OverlayStackService overlays,
                           DialogState dialogState,
                           ESCDebounceState debounce,
                           TelemetryLogger telemetry) {
        this.navigation = navigation;
        this.overlays = overlays;
        this.dialogState = dialogState;
        this.debounce = debounce;
        this.telemetry = telemetry;
    }

    /** Convenience constructor for quick tests; consider removing when DI container introduced. */
    public ESCInputHandler() {
        this(new NavigationStateService(), new OverlayStackService(), new DialogState(), new ESCDebounceState(200), new TelemetryLogger(TelemetryConfig::isEnabled));
    }

    /** External state setters (will later be wired to game events). */
    public void setCutsceneActive(boolean value) { this.cutsceneActive = value; }
    public void setTransitionActive(boolean value) { this.transitionActive = value; }
    public void setDialogOpen(boolean open) { this.dialogState.setOpen(open); }

    /** Handle ESC using current system time. */
    public EscActionType handleEsc() {
        return handleEscAt(System.currentTimeMillis());
    }

    /**
     * Handle ESC at a supplied (test-controlled) timestamp.
     * @param nowMs current time in milliseconds
     * @return resolved action (may be IGNORED when debounced or during transition)
     */
    public EscActionType handleEscAt(long nowMs) {
        if (!debounce.tryAccept(nowMs)) {
            return EscActionType.IGNORED; // debounced
        }
        boolean dialogOpen = dialogState.isOpen();
        boolean overlayPresent = overlays.hasOverlays();
        boolean menuOpen = navigation.isPauseMenuOpen();
        int depth = navigation.getMenuDepth();

        EscActionType action = EscPrecedenceResolver.resolve(dialogOpen, overlayPresent, cutsceneActive, transitionActive, menuOpen, depth);

        // Side effects
        switch (action) {
            case DIALOG_DISMISS -> dialogState.setOpen(false);
            case OVERLAY_CLOSE -> overlays.popOverlay();
            case CUTSCENE_INTERRUPT_OPEN_MENU -> {
                cutsceneActive = false;
                navigation.openPauseMenu();
            }
            case OPEN_MENU -> navigation.openPauseMenu();
            case SUBMENU_BACK -> navigation.navigateBack();
            case RESUME_GAMEPLAY -> navigation.resumeGameplay();
            case IGNORED, NO_OP -> { /* no side effect */ }
        }

        // Telemetry only for meaningful actions
        if (action != EscActionType.IGNORED && action != EscActionType.NO_OP) {
            telemetry.record(action);
        }
        return action;
    }

    /** @return true if gameplay should be considered paused (menu open OR a pausing overlay). */
    public boolean isGameplayPaused() { return navigation.isPauseMenuOpen() || overlays.getPauseOverlayCount() > 0; }
}
