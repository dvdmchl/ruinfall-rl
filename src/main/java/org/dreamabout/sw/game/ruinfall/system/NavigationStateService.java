package org.dreamabout.sw.game.ruinfall.system;

/**
 * Tracks pause menu navigation depth and open/close state.
 * Depth semantics:
 *   0 = closed (gameplay active)
 *   1 = pause menu root open (gameplay paused)
 *  >1 = nested submenu levels
 */
public class NavigationStateService {
    private int depth = 0; // 0 = closed

    /** Open pause menu or descend into a submenu; first open sets depth=1. */
    public void openPauseMenu() {
        if (depth == 0) depth = 1; else depth++;
    }

    /** @return true if any pause menu layer is open (depth > 0). */
    public boolean isPauseMenuOpen() { return depth > 0; }

    /** @return current menu depth (0 closed, 1 root, >1 submenu levels). */
    public int getMenuDepth() { return depth; }

    /** Navigate back: decrement depth or resume gameplay if at root. */
    public void navigateBack() {
        if (depth > 1) {
            depth--;
        } else if (depth == 1) {
            resumeGameplay();
        }
    }

    /** Close all menus and resume gameplay (depth -> 0). */
    public void resumeGameplay() { depth = 0; }
}
