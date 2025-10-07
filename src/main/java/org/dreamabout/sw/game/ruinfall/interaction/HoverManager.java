package org.dreamabout.sw.game.ruinfall.interaction;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Manages hover state over interactive objects.
 *
 * Invariants:
 * - State updates are atomic (single HoverState record) to avoid partial visibility.
 * - All recomputations inspect at most one tile stack (O(k) with k = stack size).
 * - tileX/tileY == -1 implies hoveredId == null and stackIndex == 0.
 * - stackIndex always within current stack size (clamped to 0 when stack shrinks).
 *
 * Trigger Sources:
 * - Mouse movement (onHoverTile)
 * - Scroll wheel cycling (cycle)
 * - Object movement / removal (listener callbacks)
 * - Explicit app refresh (refreshHoverIfAffected) – kept for backward compatibility.
 */
public class HoverManager implements InteractiveRegistry.Listener {

    private final AtomicReference<HoverState> ref = new AtomicReference<>(new HoverState(-1, -1, null, 0));

    /** Returns current immutable hover snapshot. */
    public HoverState getState() { return ref.get(); }

    /** Mouse moved or synthetic recompute to tile (x,y); negative coordinates clear hover. */
    public void onHoverTile(int x, int y, InteractiveRegistry registry) {
        if (x < 0 || y < 0) {
            ref.set(new HoverState(-1, -1, null, 0));
            return;
        }
        HoverState current = ref.get();
        if (current.tileX() != x || current.tileY() != y) {
            recompute(x, y, 0, registry); // new tile => reset index
        } else {
            recompute(x, y, current.stackIndex(), registry); // same tile => preserve index if valid
        }
    }

    /** Cycle selection within current tile stack by delta (-1 or +1 typical). */
    public void cycle(int delta, InteractiveRegistry registry) {
        if (delta == 0) return;
        HoverState current = ref.get();
        List<InteractiveObject> stack = registry.getStackAt(current.tileX(), current.tileY());
        if (stack.size() <= 1) return; // nothing to cycle
        int newIndex = (current.stackIndex() + delta) % stack.size();
        if (newIndex < 0) newIndex += stack.size();
        ref.set(new HoverState(current.tileX(), current.tileY(), stack.get(newIndex).getId(), newIndex));
    }

    /** Convenience to resolve concrete object. */
    public InteractiveObject getCurrentObject(InteractiveRegistry registry) {
        HoverState s = ref.get();
        if (s.hoveredId() == null) return null;
        return registry.getById(s.hoveredId());
    }

    private void recompute(int tileX, int tileY, int desiredIndex, InteractiveRegistry registry) {
        List<InteractiveObject> stack = registry.getStackAt(tileX, tileY);
        if (stack.isEmpty()) {
            ref.set(new HoverState(tileX, tileY, null, 0));
            return;
        }
        int idx = desiredIndex >= stack.size() ? 0 : desiredIndex;
        ref.set(new HoverState(tileX, tileY, stack.get(idx).getId(), idx));
    }

    // --- Listener callbacks (movement / removal) ---
    @Override
    public void onObjectMoved(String id, int oldX, int oldY, int newX, int newY, InteractiveRegistry registry) {
        HoverState s = ref.get();
        if (s.tileX() == -1) return; // inactive hover
        boolean affectsOld = (s.tileX() == oldX && s.tileY() == oldY);
        boolean affectsNew = (s.tileX() == newX && s.tileY() == newY);
        if (affectsOld && (oldX != newX || oldY != newY)) {
            recompute(oldX, oldY, s.stackIndex(), registry); // object left hovered tile
        } else if (affectsNew) {
            recompute(newX, newY, 0, registry); // object entered hovered tile
        }
    }

    @Override
    public void onObjectUnregistered(String id, int oldX, int oldY, InteractiveRegistry registry) {
        HoverState s = ref.get();
        if (s.tileX() == oldX && s.tileY() == oldY) {
            recompute(oldX, oldY, s.stackIndex(), registry); // fallback or clear
        }
    }

    /** Explicit refresh helper (legacy use). */
    public void refreshHoverIfAffected(int oldX,int oldY,int newX,int newY, InteractiveRegistry registry){
        onObjectMoved("", oldX, oldY, newX, newY, registry);
    }
}
