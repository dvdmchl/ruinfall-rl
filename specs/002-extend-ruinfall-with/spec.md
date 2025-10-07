# Feature Specification: Interactive Object System (Selection & UI Layer)

**Feature Branch**: `002-extend-ruinfall-with`  
**Created**: 2025-10-06  
**Status**: Ready for Implementation  
**Input**: User description: "Extend Ruinfall with a basic interactive object system. The player can select any visible interactive object with the mouse (for example, enemies, chests, NPCs). When an object is hovered or selected, show its nameplate and minimal information near it. When selected, show more detailed information in a fixed side panel. Right-clicking an object opens a simple context menu with available actions relevant to its type (e.g., Attack for enemies, Loot for chests, Talk for NPCs). Focus this iteration only on interaction, selection logic, and UI presentation — not on implementing the underlying actions or inventory mechanics yet."

## Execution Flow (main)
```
1. Parse user description from Input → OK
2. Extract key concepts → actors: player, interactive objects (enemy, chest, NPC, extensible); actions: hover, left-click select, right-click open context menu; data: object identity, visibility, basic stats/summary; constraints: only visible objects, no action implementation yet
3. Mark unclear aspects → inserted as [NEEDS CLARIFICATION] (now resolved)
4. Define user scenarios → below
5. Generate testable functional requirements → FR list
6. Identify key entities → InteractiveObject, SelectionState, Nameplate UI, ContextMenu UI, SidePanel UI Model
7. Review checklist → updated to Ready (all clarifications resolved)
8. Return SUCCESS (spec ready for planning / task breakdown)
```

---

## ⚡ Quick Guidelines
- ✅ Focus on what the user sees and can do (mouse interaction with a world)
- ❌ Do NOT implement combat, loot, or dialogue logic in this iteration
- 👥 Written for stakeholders: describes UI & interaction behavior

---

## User Scenarios & Testing *(mandatory)*

### Primary User Story
As a player I want to move my mouse over objects in my field of view and quickly see their name and a short summary; I want to left-click to select an object and view details in a side panel; I want to right‑click to open a context menu of actions relevant to that object's type, so I can prepare future actions (not yet executed this iteration).

### Acceptance Scenarios
1. Given an enemy is visible (FOV state = VISIBLE), When I hover its tile, Then a floating nameplate appears near it showing its name and a short info line (e.g., type / HP summary) and disappears when the cursor leaves (unless it is also selected).
2. Given a chest is visible, When I left-click it, Then it becomes the active selection, its nameplate remains visible, and the side panel populates with a detail section (name, type, short placeholder description, placeholder actions/content section).
3. Given another object is currently selected, When I click an empty floor tile with no interactive object, Then the current selection is cleared and the side panel shows a neutral "No selection" state.
4. Given an object is visible and selected, When I right‑click the object, Then a context menu appears to list available (placeholder) actions such as "Attack (disabled placeholder)" for an enemy.
5. Given an object is not visible (state = MEMORY or UNSEEN), When I hover its coordinate, Then no nameplate or menu appears (an object cannot be selected).
6. Given the context menu is open, When I click outside the menu, Then the menu closes without changing the current selection.
7. Given I have an enemy selected, When I select a chest, Then the side panel immediately updates to show the chest's information.

### Edge Cases
- Multiple objects overlapping on a single tile: resolved via fixed priority list (Enemy > NPC > Chest). Hover/selection always targets the highest priority present; if multiple of the same priority exist, use earliest registration (stable ordering). No cycling via scroll in this iteration (S4 + S1 fallback decision).
- Rapid mouse movement across many objects: system should avoid flicker (no debounce needed if perf OK).
- The context menu opens, and the object leaves visibility (moves into fog): the menu closes, and selection is cleared. (Confirmed)
- Click on an object outside the current camera viewport (future scrolling): ignored in this iteration (camera assumed static / centered).
- Right-click on an object that is not yet selected: performs selection first, then opens a menu (selection → menu order). (Confirmed)

---

## Requirements *(mandatory)*

### Functional Requirements
- **FR-001**: System MUST detect mouse hover over any interactive object whose tile is currently VISIBLE.
- **FR-002**: System MUST show a floating nameplate on hover containing at minimum: object display name and a short type descriptor (e.g., "Enemy," "Chest," "NPC").
- **FR-003**: Nameplate MUST disappear after hover ends if the object is not currently selected.
- **FR-004**: Left-click on a visible interactive object MUST set that object as the single active selection (single-selection model).
- **FR-005**: A selected object MUST retain its nameplate even when not hovered (persistent highlight state).
- **FR-006**: Side panel MUST on selection show: name, type, placeholder short description, and textual list of potential actions (non-functional placeholders).
- **FR-007**: Left-click on an empty visible floor tile MUST clear the current selection and reset the side panel to a neutral "No selection" state.
- **FR-008**: Right-click on a visible interactive object MUST open a context menu anchored to the object with its available actions.
- **FR-009**: Actions in the context menu MUST be represented as clickable items, but executing them in this iteration performs only placeholder feedback (e.g., log output or UI toast "Not implemented").
- **FR-010**: Clicking outside an open context menu MUST close the menu without altering the selection.
- **FR-011**: Opening a context menu via right-click MUST (if the object was not already selected) first select the object.
- **FR-012**: System MUST prevent selection or menu opening on objects outside the current visible (FOV) state (MEMORY / UNSEEN are non-interactive).
- **FR-013**: If the selected object becomes non-visible (movement or FOV change), selection MUST be cleared and all related UI (panel, nameplate, menu) hidden within 1 frame of update.
- **FR-014**: Changing selection MUST close any previously open context menu.
- **FR-015**: System MUST support minimum types: Enemy, Chest, NPC; additional types shall be introducible via metadata without changing existing selection logic.
- **FR-016**: Every interactive object MUST have an internal unique identifier and displayName.
- **FR-017**: UI floating elements (nameplate, context menu) MUST render above the map layer and MUST NOT overlap the fixed side panel; if their default horizontal placement would overlap or exceed viewport bounds, they shift horizontally inward to remain fully visible with ≥4 px margin. Vertical placement preference: above the object; if insufficient space (would clip top), place below; if below would clip bottom, clamp inside with 4 px margin.
- **FR-018**: System MUST guarantee at most one active context menu at any given time.
- **FR-019**: Switching selection MUST be achievable with a single click (no explicit deselect step required).
- **FR-020**: System MUST log each attempt to invoke a not-yet-implemented action including object id and action code/label.
- **FR-021**: When multiple interactive objects occupy the same tile, hover and selection MUST target the highest priority type (Enemy > NPC > Chest). If multiple objects of the same priority coexist, select the earliest registered (stable order). Nameplate and context menu refer only to the resolved target.

### Non-Functional (Scope-Limited) Requirements
- **NFR-001**: Hover → nameplate response SHOULD appear within 100 ms (≈1–2 frames at 60 FPS); no artificial delay.
- **NFR-002**: Context menu anchor MUST default to centered above the object's tile (offset 0, -12 px). If vertical space above is insufficient (would breach a 4 px top margin), anchor below (offset 0, +12 px). After vertical decision, apply horizontal clamping to keep full menu inside viewport and outside side panel with ≥4 px margin.
- **NFR-003**: Architecture MUST allow new action entries to be added without modifying existing functional requirements (open list model).
- **NFR-004**: Nameplate width MUST be between 120 px (min) and 240 px (max). Text wraps on word boundaries. A single word exceeding 240 px is soft-truncated with an ellipsis (…); underlying full text retained for later tooltip expansion (future enhancement).
- **NFR-005**: Localization scope for this iteration is English (en-US) only. All user-facing strings MUST be centralized in a simple registry/enumeration to enable future i18n without refactoring calling code.

### Assumptions
- Hover targeting is tile-based (object anchored at a tile center); pixel-perfect hitboxes not require this iteration.
- Typically max 1 interactive object per tile (overlap treated as exceptional; handled deterministically via priority).
- Basic Enemy / Chest / NPC object data will exist or be stubbed without complex logic.

### Out of Scope (Explicit)
- Executing real actions (Attack, Loot, Talk) – no HP, inventory, or dialogue changes.
- Persisting selection across the game restarts.
- Multi-selection, keyboard shortcuts for actions.
- Drag selection, path highlights, tooltip fade/animation timing.
- Controller / touch input.

### Key Entities (conceptual)
- **InteractiveObject (abstract concept)**: id, displayName, type, shortSummary (string), visibility (derived from Tile / FOV), tileX, tileY.
- **SelectionState**: currentSelectedId (nullable), selectedType, timestampOfChange.
- **NameplateModel**: objectId, textLines (min: name, summary), anchorPosition.
- **ContextMenuModel**: objectId, list<ActionItem>; ActionItem: code, label, enabled (false for placeholder), tooltip(optional).
- **SidePanelViewModel**: selectedObjectId, headerName, typeLabel, descriptionPlaceholder, actionsSummary (text list).

### Clarifications (Resolved)
- Multi-object tile handling: fixed priority Enemy > NPC > Chest with stable earliest-registration tie-break; no cycling this iteration.
- Context menu positioning: prefer above (0, -12 px), fallback below (0, +12 px); clamp inside viewport & away from side panel (≥4 px margins).
- Nameplate sizing: 120–240 px; word wrap; long single word soft-truncated with ellipsis.
- Localization: English-only; strings centralized for future expansion.

---

## Review & Acceptance Checklist
*GATE: Ready – all clarifications resolved*

### Content Quality
- [x] No implementation details (languages, frameworks, APIs) – only behavioral description
- [x] Focused on user value and interactions
- [x] Written for non-technical stakeholders
- [x] All mandatory sections are completed

### Requirement Completeness
- [x] No [NEEDS CLARIFICATION] markers remain
- [x] Requirements are testable and unambiguous
- [x] Success criteria are measurable
- [x] Scope is clearly bounded
- [x] Dependencies and assumptions identified

---

## Execution Status
- [x] User description parsed
- [x] Key concepts extracted
- [x] Ambiguities marked (now resolved)
- [x] User scenarios defined
- [x] Requirements generated
- [x] Entities identified
- [x] Review checklist passed

---
