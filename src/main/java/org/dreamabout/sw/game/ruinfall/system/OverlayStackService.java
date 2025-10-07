package org.dreamabout.sw.game.ruinfall.system;

import org.dreamabout.sw.game.ruinfall.system.overlay.OverlayEntry;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Manages a LIFO stack of UI overlays. Each overlay may optionally pause gameplay.
 * Pause consolidation is derived from counting paused overlays (pauseCount > 0).
 */
public class OverlayStackService {
    private final Deque<OverlayEntry> stack = new ArrayDeque<>();
    private int pauseCount = 0;

    /** Push a new overlay with an identifier and pause flag. */
    public void pushOverlay(String id, boolean pausesGameplay) {
        stack.push(new OverlayEntry(id, pausesGameplay));
        if (pausesGameplay) pauseCount++;
    }

    /** Pop the top overlay if present returning its id, or null if empty. */
    public String popOverlay() {
        if (stack.isEmpty()) return null;
        OverlayEntry e = stack.pop();
        if (e.pausesGameplay()) pauseCount--;
        return e.id();
    }

    /** @return total overlays (paused + non-paused). */
    public int getOverlayCount() { return stack.size(); }

    /** @return active overlays that individually pause gameplay. */
    public int getPauseOverlayCount() { return pauseCount; }

    /** @return true if at least one overlay is present. */
    public boolean hasOverlays() { return !stack.isEmpty(); }
}
