package org.dreamabout.sw.game.ruinfall.interaction;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class SelectionManager {

    private final AtomicReference<SelectionState> stateRef = new AtomicReference<>(new SelectionState(null, System.nanoTime()));

    public SelectionState getState() {
        return stateRef.get();
    }

    public void select(InteractiveObject obj) {
        Objects.requireNonNull(obj, "obj");
        stateRef.set(new SelectionState(obj.getId(), System.nanoTime()));
    }

    public void clear() {
        SelectionState current = stateRef.get();
        if (current.selectedId() != null) {
            stateRef.set(new SelectionState(null, System.nanoTime()));
        }
    }

    public void validateSelection(VisibilityChecker checker) {
        SelectionState current = stateRef.get();
        if (current.selectedId() != null && !checker.isVisible(current.selectedId())) {
            clear();
        }
    }
}
