package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.system.EscActionType;

/** Precedence resolver implementing ordered ESC action decision logic. */
public final class EscPrecedenceResolver {
    private EscPrecedenceResolver() {}

    public static EscActionType resolve(boolean dialogOpen, boolean overlayPresent, boolean cutsceneActive,
                                        boolean transitionActive, boolean menuOpen, int menuDepth) {
        if (transitionActive) {
            // Transitions short-circuit to ignored before any UI mutation except dialog dismiss? Spec says ignore transitions.
            return EscActionType.IGNORED;
        }
        if (dialogOpen) return EscActionType.DIALOG_DISMISS;
        if (overlayPresent) return EscActionType.OVERLAY_CLOSE;
        if (cutsceneActive) return EscActionType.CUTSCENE_INTERRUPT_OPEN_MENU;
        if (!menuOpen) return EscActionType.OPEN_MENU;
        if (menuOpen && menuDepth > 1) return EscActionType.SUBMENU_BACK;
        if (menuOpen && menuDepth == 1) return EscActionType.RESUME_GAMEPLAY;
        return EscActionType.NO_OP;
    }
}
