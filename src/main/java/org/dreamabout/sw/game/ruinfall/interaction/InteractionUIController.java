package org.dreamabout.sw.game.ruinfall.interaction;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Logic-only UI controller (no JavaFX) providing models for tests.
 */
public class InteractionUIController {

    private final InteractiveRegistry registry;
    private final SelectionManager selectionManager;
    private final HoverManager hoverManager;
    private ContextMenuModel contextMenu; // null when closed

    private final PrintStream logStream;

    public InteractionUIController(InteractiveRegistry registry, SelectionManager selectionManager, HoverManager hoverManager) {
        this(registry, selectionManager, hoverManager, System.out);
    }

    public InteractionUIController(InteractiveRegistry registry, SelectionManager selectionManager, HoverManager hoverManager, PrintStream logStream) {
        this.registry = registry;
        this.selectionManager = selectionManager;
        this.hoverManager = hoverManager;
        this.logStream = logStream;
    }

    public void onMouseMoveTile(int x, int y) {
        hoverManager.onHoverTile(x, y, registry);
    }

    public void onScroll(int delta) {
        hoverManager.cycle(delta, registry);
    }

    public void onLeftClickTile(int x, int y, VisibilityChecker visibility) {
        Optional<InteractiveObject> primary = registry.getPrimaryAt(x, y);
        if (primary.isPresent() && visibility.isVisible(primary.get().getId())) {
            selectionManager.select(primary.get());
            closeContextMenu();
        } else {
            selectionManager.clear();
            closeContextMenu();
        }
    }

    public void onRightClickTile(int x, int y, VisibilityChecker visibility) {
        Optional<InteractiveObject> primary = registry.getPrimaryAt(x, y);
        if (primary.isEmpty()) return;
        InteractiveObject obj = primary.get();
        if (!visibility.isVisible(obj.getId())) return;
        // select first if not selected
        if (!obj.getId().equals(selectionManager.getState().selectedId())) {
            selectionManager.select(obj);
        }
        openContextMenuFor(obj);
    }

    public void openContextMenuFor(InteractiveObject obj) {
        contextMenu = new ContextMenuModel(obj.getId(), buildActionsForType(obj.getType()));
    }

    private List<InteractiveActionItem> buildActionsForType(InteractiveObjectType type) {
        List<InteractiveActionItem> list = new ArrayList<>();
        switch (type) {
            case ENEMY -> {
                list.add(new InteractiveActionItem("INSPECT", "actions.inspect", true));
                list.add(new InteractiveActionItem("ATTACK", "actions.attack", false));
            }
            case CHEST -> {
                list.add(new InteractiveActionItem("INSPECT", "actions.inspect", true));
                list.add(new InteractiveActionItem("LOOT", "actions.loot", false));
            }
            case NPC -> {
                list.add(new InteractiveActionItem("INSPECT", "actions.inspect", true));
                list.add(new InteractiveActionItem("TALK", "actions.talk", false));
            }
        }
        return List.copyOf(list);
    }

    public void onEmptyTileClick() {
        selectionManager.clear();
        closeContextMenu();
    }

    public void closeContextMenu() {
        contextMenu = null;
    }

    public void validateVisibility(VisibilityChecker visibility) {
        selectionManager.validateSelection(visibility);
        if (contextMenu != null && !visibility.isVisible(contextMenu.objectId())) {
            closeContextMenu();
        }
    }

    public NameplateModel getNameplateModel() {
        // Show for hovered or selected (prefer hovered stack context)
        HoverState hs = hoverManager.getState();
        List<InteractiveObject> stack = registry.getStackAt(hs.tileX(), hs.tileY());
        InteractiveObject target = null;
        if (hs.hoveredId() != null) {
            target = registry.getById(hs.hoveredId());
        } else if (selectionManager.getState().selectedId() != null) {
            target = registry.getById(selectionManager.getState().selectedId());
            if (target != null) {
                stack = registry.getStackAt(target.getTileX(), target.getTileY());
            }
        }
        if (target == null) return null;
        List<String> lines = List.of(target.getDisplayName(), target.getShortSummary());
        int idx = 0;
        for (int i = 0; i < stack.size(); i++) {
            if (stack.get(i).getId().equals(target.getId())) { idx = i; break; }
        }
        return new NameplateModel(target.getId(), lines, stack.size(), idx);
    }

    public SidePanelViewModel getSidePanelViewModel() {
        String selId = selectionManager.getState().selectedId();
        if (selId == null) return new SidePanelViewModel(null, "", "", "Nothing selected", List.of());
        InteractiveObject obj = registry.getById(selId);
        if (obj == null) return new SidePanelViewModel(null, "", "", "Nothing selected", List.of());
        String header = obj.getDisplayName();
        String typeLabel = obj.getType().name().toLowerCase();
        String desc = obj.getShortSummary();
        List<InteractiveActionItem> actions = buildActionsForType(obj.getType());
        return new SidePanelViewModel(selId, header, typeLabel, desc, actions);
    }

    public ContextMenuModel getContextMenuModel() {
        return contextMenu;
    }

    public void invokeAction(String actionCode) {
        String selId = selectionManager.getState().selectedId();
        if (selId == null) return;
        // Only INSPECT is enabled currently; others placeholder log
        if (!"INSPECT".equals(actionCode)) {
            logStream.println("ACTION_PLACEHOLDER objectId=" + selId + " action=" + actionCode);
        } else {
            logStream.println("ACTION_INSPECT objectId=" + selId);
        }
    }

    public void onOutsideClick() {
        // Close only the context menu; do not alter selection
        if (contextMenu != null) {
            closeContextMenu();
        }
    }
}

