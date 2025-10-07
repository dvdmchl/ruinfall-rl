# Research: Interactive Object System

## Decision Log

### Interaction Resolution Approach
- Decision: Central `InteractiveRegistry` keyed by tile coordinate -> ordered set of objects.
- Rationale: O(1) tile lookup, deterministic ordering, easy stack operations.
- Alternatives Considered: Linear scan of all objects each hover (wasteful), spatial quad tree (overkill for small counts).

### Overlap Cycling Mechanism
- Decision: Maintain per-hover tile index; mouse wheel delta cycles through stack; middle-click opens stack menu.
- Rationale: Simple, matches spec; low state overhead.
- Alternatives: Hold ALT+scroll (adds complexity), on-screen arrows (UI clutter).

### Visibility Invalidation
- Decision: `SelectionManager.onVisibilityRefresh(VisibilityChecker)` clears selection and open context menu if object not visible.
- Rationale: Central enforcement within one frame of update.
- Alternatives: Event broadcasting from visibility system (extra coupling) or polling in UI loop (unnecessary repetition).

### Localization Strategy
- Decision: Java `ResourceBundle` wrapper `Messages.get(key)` with base `i18n/messages.properties`.
- Rationale: Standard Java approach, easily extensible for future locales.
- Alternatives: Custom map loader (redundant), external JSON (adds parsing cost & tooling).

### UI Layer Structure
- Decision: Pure model POJOs (NameplateModel, ContextMenuModel, SidePanelViewModel) plus JavaFX node classes which render models.
- Rationale: Enables headless tests validating logic independent from JavaFX thread.
- Alternatives: Direct binding of managers to JavaFX properties (harder to test off thread).

### Placeholder Actions
- Decision: Provide enum `InteractiveAction` and build `InteractiveActionItem` with enabled flag; disabled items for Attack/Loot/Talk except Inspect (enabled, logs only).
- Rationale: Clean extension point; future real implementation flips enabled.
- Alternatives: Hard-coded string list per type (less maintainable).

### Highlight Rendering
- Decision: Single reusable `AuraHighlight` circle positioned under currently selected entity.
- Rationale: Avoid per-entity permanent highlight nodes.
- Alternatives: Per-selection dynamic effect nodes (higher churn).

### Context Menu Placement
- Decision: Preferred anchor to the right of object center ( +8px x offset ), fallback left if clipping side panel or screen; vertical adjust above, then below, then clamp.
- Rationale: Aligns with revised spec (right side preference) while reusing bounding clamp util.

## Open Issues (None)
All clarifications resolved in original feature spec.


