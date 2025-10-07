# Implementation Plan: ESC Key Menu Navigation Behavior

**Branch**: `005-esc-key-will` | **Date**: 2025-10-07 | **Spec**: `specs/005-esc-key-will/spec.md`
**Input**: Feature specification from `/specs/005-esc-key-will/spec.md`

## Execution Flow (/plan command scope)
```
1. Load feature spec from Input path
2. Fill Technical Context (no NEEDS CLARIFICATION – Clarifications Session present)
3. Fill the Constitution Check section based on constitution document.
4. Evaluate Constitution Check section (PASS)
5. Execute Phase 0 → research.md (completed)
6. Execute Phase 1 → contracts, data-model.md, quickstart.md, agent file update candidate (completed)
7. Re-evaluate Constitution Check (PASS)
8. Plan Phase 2 → Describe task generation approach (tasks.md NOT created)
9. STOP - Ready for /tasks command
```
**IMPORTANT**: Per template, /plan stops before generating tasks.md.

## Summary
Primary requirement: Ensure ESC never closes the application directly; it opens the Pause Menu from gameplay, navigates back within menus, dismisses dialogs first, closes overlays one at a time, interrupts cutscenes, and resumes gameplay only after all pausing layers (Pause Menu + Overlays) are closed. 
High-level technical approach: Introduce a centralized ESCInputHandler that enforces a deterministic precedence chain (Dialog > Overlay > Cutscene > Normal) with a 200 ms debounce, consults NavigationState & OverlayStack services, records telemetry when enabled, and ensures pause/unpause semantics via a reference-count or layer-count approach. All operations remain in-memory (no persistence). Tests will validate timing, precedence, stack behavior, pause state transitions, and idempotency.

## Technical Context
**Language/Version**: Java 21 (Maven)  
**Primary Dependencies**: FXGL (bundled JavaFX runtime), JUnit 5  
**Storage**: N/A (in-memory only)  
**Testing**: JUnit 5 unit + integration tests under `src/test/java`  
**Target Platform**: Desktop (FXGL / JavaFX)  
**Project Type**: Single desktop game project  
**Performance Goals**: ≤100 ms latency open/close (SC-001, SC-004, SC-010); ≤150 ms cutscene interrupt (SC-008)  
**Constraints**: Deterministic discrete press handling (200 ms threshold); no frame hitch; avoid global scans (O(1) or O(log n) state lookups / O(stack size) overlay ops)  
**Scale/Scope**: Single-player session; small number of simultaneous overlays (expected <5), dialogs (<=1 top), no networked concurrency  

## Constitution Check
Core Principle Alignment:
- I (Maintainable): Adds cohesive input handler + small services (NavigationState, OverlayStack, TelemetryLogger); avoids invasive refactors.
- II (Test-first): Plan enumerates unit & integration tests (debounce, precedence, overlay pause, dialog dismissal, transition ignore, telemetry action categorization) prior to implementation.
- III (Cohesive Player Experience): Preserves consistent ESC behavior; clarifies layered priorities; prevents accidental game exit.
- IV (English Specification): All added artifacts authored in English (US) despite localized repo environment.
Delivery Constraints: No new external dependencies; pure in-memory state; FXGL compatible; deterministic tests. No violations.
Result: PASS (no Complexity Tracking entries required).

## Project Structure

### Documentation (this feature)
```
specs/005-esc-key-will/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
│   ├── esc-input-handler.md
│   ├── navigation-service.md
│   ├── overlay-manager.md
│   ├── pause-state-rules.md
│   └── telemetry-logging.md
└── spec.md              # Feature specification (with Clarifications Session)
```

### Source Code (repository excerpt relevant to feature)
```
src/main/java/org/dreamabout/sw/game/ruinfall/
├── RuinfallApp.java                         # Main loop / input registration point
├── interaction/
│   ├── InteractionUIController.java         # Existing UI control
│   ├── SelectionManager.java                # Existing selection logic
│   ├── HoverManager.java                    # Existing hover logic (unchanged here)
│   └── (NEW) ESCInputHandler.java           # Centralized ESC handling (planned)
├── ui/
│   ├── PauseMenuView.java (existing or to add behaviors)
│   └── OverlayViews...(inventory/map)
└── system/
    ├── NavigationStateService.java (NEW)
    ├── OverlayStackService.java (NEW)
    └── TelemetryLogger.java (NEW, toggle-aware)

src/test/java/org/dreamabout/sw/game/ruinfall/interaction/
├── ESCDebounceTest.java (NEW)
├── ESCPrecedenceTest.java (NEW)
├── ESCOverlayPauseResumeTest.java (NEW)
├── ESCDialogDismissTest.java (NEW)
├── ESCCutsceneInterruptTest.java (NEW)
├── ESCTransitionIgnoreTest.java (NEW)
├── ESCPauseMenuNavigationTest.java (NEW)
└── ESCTelemetryLoggingTest.java (NEW)
```
**Structure Decision**: Single-project Java FXGL game; new services colocated under `system/` for state logic; input handler in `interaction/` to remain near UI interaction stack.

## Phase 0: Outline & Research
### Research Focus Areas
1. Debounce Strategy: Chosen simple timestamp comparison (System time) with >=200 ms threshold; avoids scheduling overhead.
2. Precedence Enforcement: Single responsibility method returning first actionable layer; prevents double-processing.
3. Pause Semantics: Layer-count approach (Pause Menu + N overlays) ensure resume only when count == 0.
4. Telemetry Toggle: Single config flag (default enabled) gating record calls; lightweight enum action types.
5. Transition Guard: ESC ignored during transitions unless higher precedence (Dialog/Overlay) present.

### Consolidated Findings (Decisions)
- Decision: Use monotonic time (e.g., FXGL provided or System.nanoTime converted) for debounce.
  - Rationale: Avoid wall-clock adjustments.
  - Alternatives: Event queue buffering (rejected: unnecessary complexity).
- Decision: Represent NavigationState as composite (primary mode + menuDepth) instead of deep subclassing.
  - Rationale: Simpler serialization/testing; fewer branches.
  - Alternatives: Multiple boolean flags (risk of inconsistent combinations).
- Decision: OverlayStack maintains LIFO list; each overlay marked `pausesWorld=true` (current scope all true) for future extensibility.
  - Rationale: Future non-pausing overlays can disable flag.
- Decision: Telemetry only logs after action executed, never for ignored presses (transition).
  - Rationale: Cleaner analytics.
- Decision: Cutscene interruption triggers immediate pause menu open; no intermediate gameplay state.
  - Rationale: Aligns FR-014 ensuring user context shift.

### Alternatives Considered
- Central global GameState enum only (rejected: less flexible for stacks & overlays).
- Event bus for ESC actions (deferred until more keys/actions require decoupling).
- Separate PauseManager & OverlayManager pause counters (merged into single aggregated evaluation in NavigationStateService for simplicity).

Phase 0 Output: `research.md` (created) – no unresolved items.

## Phase 1: Design & Contracts
### Data Model Extraction
See `data-model.md` for detailed entities:
- NavigationState (mode, menuDepth, transitionActive, cutsceneActive, postRun)
- OverlayStack (list<OverlayEntry>)
- OverlayEntry (id, type, openedAt, pausesWorld)
- DialogState (present:boolean, dialogType?)
- ESCDebounceState (lastAcceptedEpochMillis)
- ESCActionRecord (timestamp, actionType)
- TelemetryConfig (enabled)

### Contracts Overview
Created under `contracts/`:
- esc-input-handler.md: Contract for `handleEscPress(nowMillis)` returning EscActionType or Ignored.
- navigation-service.md: Operations for pause menu open/close, back navigation, resume logic.
- overlay-manager.md: Open/close overlay semantics & world pause interplay.
- pause-state-rules.md: Precedence order & state transition table.
- telemetry-logging.md: Action types enum, logging rules, toggle flag behavior.

### Quickstart
`quickstart.md` outlines: (1) Build & run tests with Maven; (2) Run targeted ESC tests; (3) Manual smoke steps verifying overlays + pause interplay, debounce, precedence.

### Agent Context Update
Script `.specify/scripts/powershell/update-agent-context.ps1 -AgentType copilot` can now include this feature (same tech stack; will append branch). Execution deferred until after plan file saved (eligible now).

### Constitution Re-check (Post-Design)
No new complexity or external dependencies introduced; principles still satisfied. PASS.

## Phase 2: Task Planning Approach (Description Only)
Task Generation Strategy:
- Map each Functional Requirement (FR-001..FR-017) to at least one test task.
- Derive unit tests first (debounce, precedence resolution algorithm, state transitions) then integration tests (menu open/close flows, overlays, dialogs, cutscene interrupt, transition ignore, telemetry coverage).
- Implementation tasks follow TDD: create failing test → implement minimal code → refactor.

Ordering Strategy:
1. ESCDebounceState + unit test [P]
2. Precedence resolver unit test [P]
3. NavigationStateService skeleton + tests
4. OverlayStackService + pause counting tests
5. ESCInputHandler core path (gameplay → open menu)
6. Back navigation (submenu depth) tests
7. Dialog dismissal handling tests
8. Overlay close-first handling tests
9. Cutscene interrupt tests
10. Transition ignore tests
11. Resume gameplay condition tests
12. Telemetry logging tests (enabled/disabled edge & action categories)
13. Integration scenario: rapid ESC press sequence
14. Integration scenario: overlay stack then menu open
15. Integration scenario: dialog atop overlay
16. Integration scenario: pause menu top-level resume
17. Integration scenario: cutscene interruption path
18. Performance guard (optional) ensure constant-time decision logic
Estimated Output: ~26 tasks (some parallelizable [P] for early unit tests).

Estimated Task Tags:
- [P] Debounce, Precedence Resolver, OverlayStack basic tests (independent)
- Integration tests sequenced after core handler available.

## Phase 3+: Future Implementation (Out of Scope for /plan)
Refer to template: /tasks will create tasks.md; subsequent phases implement & validate.

## Complexity Tracking
| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|---------------------------------------|
| (none) | | |

## Progress Tracking
**Phase Status**:
- [x] Phase 0: Research complete (/plan command)
- [x] Phase 1: Design complete (/plan command)
- [x] Phase 2: Task planning complete (/plan command - description only)
- [ ] Phase 3: Tasks generated (/tasks command)
- [ ] Phase 4: Implementation complete
- [ ] Phase 5: Validation passed

**Gate Status**:
- [x] Initial Constitution Check: PASS
- [x] Post-Design Constitution Check: PASS
- [x] All NEEDS CLARIFICATION resolved
- [x] Complexity deviations documented (N/A)

---
*Based on Constitution v1.0.1 - See `/memory/constitution.md`*
