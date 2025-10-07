# Feature Specification: ESC Key Menu Navigation Behavior

**Feature Branch**: `005-esc-key-will`  
**Created**: 2025-10-07  
**Status**: Draft  
**Input**: User description: "esc key will not close the game, it will only open menu as it is doing now, or it will traverse back in menu"

## Execution Flow (main)
```
1. Parse user description from Input
2. Extract key concepts (ESC key, no direct exit, open menu, navigate back)
3. Mark ambiguities (resolved except telemetry optionality & debounce definition now fixed)
4. Define user scenarios (gameplay → menu, submenu backtracking, dialog dismissal, overlays, cutscene interrupt)
5. Generate functional requirements (testable, numbered)
6. Identify key entities (Navigation State, Menu Layer, Overlay Stack)
7. Review checklist population
8. Return: SUCCESS (spec ready for planning; no pending clarifications)
```

---

## Clarifications

### Session 1 (2025-10-07)
1. Telemetry default & toggle mechanism (FR-015)
   - Question: What is the default state and mechanism for enabling/disabling ESC action telemetry logging?
   - Options Considered: 
     - A: Enabled by default; disable only via configuration flag (no runtime toggle).
     - B: Enabled by default; runtime settings toggle + config override.
     - C: Disabled by default; config enable only.
     - D: Disabled by default; runtime toggle.
   - Decision: A (user response)
   - Implications: A single configuration flag controls telemetry (enabled by default). No in-game runtime toggle/UI for this feature scope. Changes require app restart / config reload. Implementation will gate event recording behind this flag; storage is ephemeral (no persistence requirement stated). No additional categories beyond listed action types.
2. Precedence ordering when multiple state categories overlap (FR-005, FR-011, FR-012, FR-014)
   - Question: What is the canonical precedence order for ESC handling across Dialog, Overlay, Cutscene, Transition?
   - Options Considered:
     - A: Dialog > Overlay > Cutscene > Transition (Transition just ignores ESC; no action queued).
     - B: Cutscene highest priority.
     - C: Transition hard blocks all.
     - D: Dialog > Cutscene > Overlay.
   - Decision: A (user response)
   - Implications: A single ESC press acts on at most one category using precedence: Dialog first (dismiss), else top Overlay (close), else Cutscene (interrupt & show Pause Menu), else if Transition active ESC is ignored (no queuing). This ordering must be deterministic and testable; overlapping layered UI must not process lower-precedence actions in the same discrete press.
3. Overlay world progression effect (FR-008, FR-009, FR-011)
   - Question: Do Overlays pause world progression like the Pause Menu?
   - Options Considered:
     - A: Overlays do not pause.
     - B: All Overlays fully pause world (same as Pause Menu).
     - C: Mixed; some pause, some not.
     - D: Config flag toggles behavior globally.
   - Decision: B (user response)
   - Implications: Opening any Overlay pauses world progression. World resumes only after the last pausing layer (all Overlays and Pause Menu) is closed. Resume logic must check that no pausing Overlays remain. Acceptance criteria & requirements updated to reflect overlay-induced pause.

### Open Clarification Topics (pending)
- (None considered critical after overlay pause resolution.)

---

## ⚡ Quick Guidelines
- ✅ Focus on WHAT users need and WHY
- ❌ Avoid HOW to implement (no tech stack, APIs, code structure)
- 👥 Written for business stakeholders, not developers

### Section Requirements
- **Mandatory sections**: Completed
- **Optional sections**: Added only when relevant
- Removed all unused placeholder text

### For AI Generation
All ambiguities resolved; assumptions explicitly stated.

---

## User Scenarios & Testing *(mandatory)*

### Primary User Story
As a player engaged in active gameplay, when I press the ESC key I want to safely access the pause / game menu without the game unexpectedly closing, and if I am already navigating menus I want ESC to take me one level back rather than exit the entire game.

### Acceptance Scenarios
1. Given active gameplay with no menus visible, When the player presses ESC once, Then the Pause Menu becomes visible and the game world is paused (game progression halted / inputs to world suppressed) while the application continues running.
2. Given the Pause Menu is open at submenu depth > 0 (e.g., Settings → Audio), When the player presses ESC, Then the system returns to the immediate parent menu level (e.g., Settings root) without closing the entire menu system.
3. Given a confirmation dialog (e.g., "Save changes?") is open on top of a submenu within the Pause Menu, When the player presses ESC, Then only the dialog closes and focus returns to the underlying submenu.
4. Given the Pause Menu is open at its top-level AND the game was previously running (paused state), When the player presses ESC, Then the Pause Menu closes and gameplay resumes. If the game has not yet started (pre-run / main menu equivalent state), ESC at top-level does nothing.
5. Given the player is in gameplay and rapidly presses ESC multiple times within a very short interval, When inputs are processed, Then only a single opening of the Pause Menu occurs (no duplicate stacking) and no unintended deeper navigation happens.
6. Given an overlay (e.g., inventory or map) is currently open and Pause Menu is not, When ESC is pressed, Then the most recently opened (top) overlay closes; only after all overlays are closed does a subsequent ESC open the Pause Menu.
7. Given the player attempts to exit the game, When ESC is pressed repeatedly, Then the application never closes directly via ESC; exit must occur through an explicit menu option (e.g., "Quit to Desktop").
8. Given a non-protected cutscene is playing, When ESC is pressed, Then the cutscene is interrupted and the Pause Menu appears. (Assumption: initial version has no protected / uninterruptible cutscenes.)
9. Given the player is viewing a death / end-of-run screen, When ESC is pressed, Then the post-run menu (summary / meta options) is shown if not already visible.
10. Given an Overlay (and not the Pause Menu) is open during gameplay, When it opens, Then world progression pauses; When the last remaining Overlay is closed (and Pause Menu not open), Then gameplay resumes.

### Edge Cases
- Transition / fade state: Pressing ESC during a state transition is ignored (no partial menu state) and does not queue an action.
- Active cutscene: ESC interrupts (no protected sequences in MVP scope).
- Key held (repeat events): Holding ESC is treated as a single press (no repeated navigation).
- Immediately after resuming gameplay: ESC does not reopen the menu due to any residual key repeat handling.
- Death / end screen: ESC opens or focuses the post-run menu.
- Overlapping interactive layers: If a Dialog and Overlay are both present, a discrete ESC press dismisses only the Dialog. If only Overlays remain, next ESC closes the top Overlay; once no Dialogs/Overlays remain, next ESC interrupts a Cutscene (if any) else proceeds with normal menu logic. ESC during Transition still ignored (no buffering).
- Multiple pause sources: If both an Overlay and the Pause Menu are open, closing one does not resume gameplay until all pausing layers (all Overlays and Pause Menu) are closed.

## Requirements *(mandatory)*

### Functional Requirements
- **FR-001**: System MUST prevent the ESC key from directly terminating or closing the game application.
- **FR-002**: System MUST open the Pause Menu when ESC is pressed during active gameplay and the Pause Menu is not already visible.
- **FR-003**: System MUST treat the Pause Menu opening as idempotent (no duplicate instances) even if ESC is pressed multiple times rapidly.
- **FR-004**: System MUST navigate one level up the current menu hierarchy when ESC is pressed inside a submenu (depth > 0).
- **FR-005**: System MUST dismiss the topmost confirmation / modal dialog (if present above a menu) when ESC is pressed, without closing underlying menus.
- **FR-006**: System MUST NOT perform more than one navigation step per discrete ESC press. A "discrete press" is defined as: a key-down event for ESC when (a) the key was previously released, and (b) at least 200 ms have elapsed since the last accepted ESC action.
- **FR-007**: System MUST provide at least one explicit, menu-driven option for exiting the game (e.g., via a Quit / Exit item) instead of ESC acting as an exit.
- **FR-008**: System MUST maintain game paused state (no world progression) while the Pause Menu OR any Overlay is open.
- **FR-009**: System MUST restore gameplay focus and unpause when: (a) the Pause Menu is fully closed after being opened from gameplay and no pausing Overlays remain, OR (b) the last remaining Overlay is closed and the Pause Menu is not open.
- **FR-010**: System MUST, when ESC is pressed at the top-level Pause Menu while the game is paused from gameplay, close the menu and resume gameplay; if the game has not started (pre-run state), ESC at top-level does nothing.
- **FR-011**: System MUST, when overlays are open (inventory/map/others), close the most recently opened overlay on ESC before opening or navigating the Pause Menu.
- **FR-012**: System MUST ignore ESC presses occurring during non-interruptible screen transitions (no queuing of the action).
- **FR-013**: System MUST ensure ESC does not bypass required confirmation dialogs (e.g., unsaved changes) unless explicitly dismissed by the user.
- **FR-014**: System MUST interrupt cutscenes when ESC is pressed (no protected sequences in current scope) and surface the Pause Menu.
- **FR-015**: System SHOULD record (internally) the type of each ESC action (OpenMenu | NavigateBack | DismissDialog | ResumeGameplay | CloseOverlay | InterruptCutscene) with a timestamp for UX analysis; logging MUST be disable-able through a configuration flag without code changes.
- **FR-016**: System MUST prevent visual flicker or oscillation caused by rapid ESC key use by enforcing the 200 ms discrete press threshold and ignoring intermediate repeats.
- **FR-017**: System MUST apply a deterministic precedence order per discrete ESC press: Dialog > Overlay > Cutscene > (otherwise normal handling). If a Transition state is active, the ESC press is ignored (no buffering) unless a higher-precedence Dialog or Overlay exists. Only one category action executes per discrete press.

### Key Entities *(include if feature involves data)*
- **Navigation State**: Conceptual state representing current interaction mode (Gameplay, PauseMenu(level), ModalDialog, Overlay, Cutscene, Transition, PostRun).
- **Menu Hierarchy**: Logical tree defining parent-child relationships of menu screens; used for back navigation.
- **Overlay Stack**: Ordered collection of transient UI layers (dialogs, overlays) where ESC targets the top element first.

---

## Measurable Success Criteria
- **SC-001**: ESC to Pause Menu open latency ≤ 100 ms on reference hardware (baseline test run).
- **SC-002**: 5 rapid ESC presses at 50 ms intervals trigger exactly 1 accepted action (menu open) (FR-002, FR-006, FR-016).
- **SC-003**: Holding ESC for 1 second triggers exactly 1 action (no repeats) (FR-006).
- **SC-004**: ESC at top-level Pause Menu (paused state) resumes gameplay in ≤ 100 ms (FR-010).
- **SC-005**: ESC at top-level pre-run state produces no state change (FR-010).
- **SC-006**: With two overlays stacked, two sequential ESC presses close them top → down without opening the Pause Menu until overlays cleared (FR-011).
- **SC-007**: ESC during a transition produces zero state changes and next ESC after transition functions normally (FR-012).
- **SC-008**: ESC during cutscene reliably interrupts and shows Pause Menu within ≤ 150 ms (FR-014).
- **SC-009**: Telemetry (if enabled) categorizes ≥ 99% of ESC actions into one of the defined action types (FR-015).
- **SC-010**: Opening any Overlay pauses world within ≤ 100 ms; closing the last pausing layer (all Overlays & Pause Menu cleared) resumes world within ≤ 100 ms.

---

## Review & Acceptance Checklist
*GATE: Automated checks run during main() execution*

### Content Quality
- [x] No implementation details (languages, frameworks, APIs)
- [x] Focused on user value and business needs
- [x] Written for non-technical stakeholders
- [x] All mandatory sections completed

### Requirement Completeness
- [x] No [NEEDS CLARIFICATION] markers remain
- [x] Requirements are testable and unambiguous  
- [x] Success criteria are measurable
- [x] Scope is clearly bounded
- [x] Dependencies and assumptions identified (debounce=200 ms, no protected cutscenes MVP, optional telemetry)

---

## Execution Status
*Updated by main() during processing*

- [x] User description parsed
- [x] Key concepts extracted
- [x] Ambiguities marked (then resolved)
- [x] User scenarios defined
- [x] Requirements generated
- [x] Entities identified
- [x] Review checklist passed

---
