# Quickstart: Interactive Object System

1. Launch game normally via `mvn exec:java`.
2. Move player with arrow or WASD keys to reveal enemy / objects.
3. Hover a visible object → nameplate shows name + summary.
4. Scroll mouse wheel while hovering stacked tile → cycles target (indicator updates to `current/total`).
5. Left-click object → selection persists nameplate; side panel updates with details & actions list.
6. Right-click object (even if unselected) → opens context menu with placeholder actions; click outside to close.
7. Press ESC → if context menu open, closes; else if selection exists, clears; else exits (legacy behavior).
8. Move so selected object leaves visibility → selection & UI clear immediately.
9. Middle-click hovered stack badge (or middle-click tile with stack) → opens stack selection menu (listing all objects) (placeholder simplified list for now).

## Placeholder Actions
- Inspect: logs to console + transient toast.
- Attack / Loot / Talk: disabled (greyed) and log attempt if clicked.

## Extension Points
- Add new object type: implement `InteractiveObject`, register in `InteractiveRegistry`, extend `InteractiveObjectType` and priority list in `InteractiveRegistry`.
- Add real action: mark `InteractiveActionItem.enabled = true` and implement dispatch in UI controller.


