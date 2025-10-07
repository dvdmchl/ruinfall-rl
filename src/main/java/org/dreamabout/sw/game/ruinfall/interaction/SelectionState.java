package org.dreamabout.sw.game.ruinfall.interaction;

/**
 * Immutable snapshot of selection.
 */
public record SelectionState(String selectedId, long changedAtNano) {
    public boolean isEmpty() { return selectedId == null; }
}
