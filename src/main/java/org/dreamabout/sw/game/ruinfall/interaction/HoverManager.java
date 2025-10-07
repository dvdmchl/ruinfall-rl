package org.dreamabout.sw.game.ruinfall.interaction;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class HoverManager {

    private final AtomicReference<HoverState> ref = new AtomicReference<>(new HoverState(-1, -1, null, 0));

    public HoverState getState() { return ref.get(); }

    public void onHoverTile(int x, int y, InteractiveRegistry registry) {
        HoverState current = ref.get();
        if (current.tileX() == x && current.tileY() == y) {
            // same tile: just refresh hovered id based on potential stack change
            List<InteractiveObject> stack = registry.getStackAt(x, y);
            int idx = current.stackIndex();
            if (stack.isEmpty()) {
                ref.set(new HoverState(x, y, null, 0));
            } else {
                if (idx >= stack.size()) idx = 0; // clamp
                ref.set(new HoverState(x, y, stack.get(idx).getId(), idx));
            }
            return;
        }
        // new tile – reset index
        List<InteractiveObject> stack = registry.getStackAt(x, y);
        String hoveredId = stack.isEmpty() ? null : stack.get(0).getId();
        ref.set(new HoverState(x, y, hoveredId, 0));
    }

    public void cycle(int delta, InteractiveRegistry registry) {
        if (delta == 0) return;
        HoverState current = ref.get();
        List<InteractiveObject> stack = registry.getStackAt(current.tileX(), current.tileY());
        if (stack.size() <= 1) return; // nothing to cycle
        int newIndex = (current.stackIndex() + delta) % stack.size();
        if (newIndex < 0) newIndex += stack.size();
        ref.set(new HoverState(current.tileX(), current.tileY(), stack.get(newIndex).getId(), newIndex));
    }

    public InteractiveObject getCurrentObject(InteractiveRegistry registry) {
        HoverState s = ref.get();
        if (s.hoveredId() == null) return null;
        return registry.getById(s.hoveredId());
    }
}
