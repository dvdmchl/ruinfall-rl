package org.dreamabout.sw.game.ruinfall.system;

/** Tracks ESC key press timing to enforce minimum interval. */
public class ESCDebounceState {
    private final long intervalMs;
    private long lastAccepted = -1;

    public ESCDebounceState(long intervalMs) {
        this.intervalMs = intervalMs;
    }

    /**
     * Attempts to accept a press at the given time (millis). Returns true if accepted.
     */
    public boolean tryAccept(long nowMs) {
        if (lastAccepted < 0) {
            lastAccepted = nowMs; return true;
        }
        if (nowMs - lastAccepted >= intervalMs) {
            lastAccepted = nowMs; return true;
        }
        return false;
    }
}

