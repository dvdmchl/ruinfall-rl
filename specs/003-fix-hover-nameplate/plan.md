# Implementation Plan: Fix stale hover / nameplate when objects move or viewport scrolls

**Branch**: `003-fix-hover-nameplate` | **Date**: 2025-10-07 | **Spec**: `specs/003-fix-hover-nameplate/spec.md`
**Input**: Feature specification from `/specs/003-fix-hover-nameplate/spec.md`

## Execution Flow (/plan command scope)
```
1. Load feature spec from Input path
2. Fill Technical Context (no NEEDS CLARIFICATION after user override to proceed)
3. Fill the Constitution Check section based on constitution document.
4. Evaluate Constitution Check section (PASS)
5. Execute Phase 0 → research.md (completed)
6. Execute Phase 1 → contracts, data-model.md, quickstart.md, agent file update (completed)
7. Re-evaluate Constitution Check (PASS)
8. Plan Phase 2 → Describe task generation approach (done below, tasks.md NOT created)
9. STOP - Ready for /tasks command
```
**IMPORTANT**: Per template, /plan stops before generating tasks.md.

## Summary
Primary requirement: Eliminate stale or offset-dependent incorrect hover/nameplate targeting so that the object visually under the cursor (after camera scrolling or object movement) is always the one whose nameplate and selection are shown.
Technical approach (high-level): Introduce deterministic coordinate translation that incorporates current viewport (camera) offset (including fractional components) when mapping cursor scene coordinates to world tile coordinates; add hover refresh triggers on movement and camera offset changes; ensure stale hover state is invalidated when the object leaves the tile; retain existing stack cycling semantics.

## Technical Context
**Language/Version**: Java 21 (Maven)  
**Primary Dependencies**: FXGL (bundled JavaFX runtime), JUnit 5  
**Storage**: N/A (in‑memory game state)  
**Testing**: JUnit 5 unit + integration tests under `src/test/java`  
**Target Platform**: Desktop (JavaFX via FXGL)  
**Project Type**: Single desktop game project  
**Performance Goals**: Maintain existing frame pacing; hover recomputation O(size of stack on tile) per trigger; no full-map scans  
**Constraints**: Deterministic tests (no RNG in hover logic); avoid floating rounding off-by-one when converting to tile indices  
**Scale/Scope**: Local single-player session; modest number of interactives per tile stack (expected < 10)  

## Constitution Check
Core Principle Alignment:
- I (Maintainable): Small, cohesive additions (coordinate helper + hover refresh trigger points) vs invasive refactor.
- II (Test-first): New unit tests for coordinate translation + integration tests for movement/viewport hover refresh will precede implementation.
- III (Player Experience): Improves reliability & feedback consistency; no control scheme changes.
- IV (English Specs): All artifacts authored in English (verified).
Delivery Constraints: No new external deps, FXGL compatible, deterministic tests. All pass.
Result: PASS (no violations; Complexity Tracking remains empty).

## Project Structure
### Documentation (this feature)
```
specs/003-fix-hover-nameplate/
├── plan.md              # This file (/plan command output)
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
│   ├── hover-refresh.md
│   └── coordinate-mapping.md
└── spec.md              # Feature specification
```
### Source Code (repository root excerpt relevant to feature)
```
src/
├── main/java/org/dreamabout/sw/game/ruinfall/
│   ├── RuinfallApp.java                      # Main app loop (viewport & UI update)
│   └── interaction/
│       ├── InteractionUIController.java      # Hover + selection orchestration
│       ├── HoverManager.java                 # Hover state (to be extended)
│       ├── SelectionManager.java             # (existing)
│       └── InteractiveObject.java            # Provides tile coordinates
└── test/java/org/dreamabout/sw/game/ruinfall/interaction/
    ├── HoverNameplateIntegrationTest.java    # Existing placeholder – extend
    ├── HoverCycleTest.java                   # Existing stack cycling test
    ├── ContextMenuPlacementTest.java         # (offset correctness cross-check)
    └── ... (other interaction tests)
```
**Structure Decision**: Single-project Java FXGL game; no multi-module decomposition needed for this feature scope.

## Phase 0: Outline & Research
### Research Focus Areas
1. Coordinate Translation Accuracy: Ensure fractional viewport offsets handled via floor((scene + offset)/tileSize).
2. Hover Refresh Triggers: Movement events + camera (viewport) scroll events need to prompt re-evaluation when cursor stationary.
3. Stale State Invalidation: When hovered object departs tile, clear or rebind to new top-of-stack object.
4. Determinism: Avoid frame timing dependence; drive tests via explicit method calls.

### Consolidated Findings (Decisions)
- Decision: Add a pure function (static utility or method on HoverManager) `mapCursorToTile(sceneX, sceneY, viewportOffsetX, viewportOffsetY, tileSize)` returning int tile indices using floor semantics.
  - Rationale: Centralizes logic; easy unit test; reduces duplication.
  - Alternatives: Inline math in multiple locations (rejected due to duplication risk).
- Decision: Introduce internal trigger method `refreshHoverIfAffected(movedObjOldX, movedObjOldY, movedObjNewX, movedObjNewY)` invoked after interactive moves.
  - Rationale: Keeps responsibility localized to HoverManager.
  - Alternatives: Global event bus expansion (overkill now).
- Decision: On viewport offset change (camera scroll), re-run hover tile mapping for current cursor coordinates captured last (store lastMouseSceneX/Y in controller) without requiring mouse event.
  - Rationale: Fixes stale plate when camera recenters.
- Decision: When object leaving hovered tile was the hovered target and tile now empty → clear; if other interactives remain → select top (consistent with existing ordering rules).
- Decision: Maintain O(k) where k = stack size for recomputation (no scanning unrelated tiles).

### Alternatives Considered
- Full reactive event system for all game object state changes (deferred; complexity not justified).
- Polling all objects each frame (rejected: performance & unnecessary work).

Phase 0 Output: `research.md` (created) – all unknowns resolved. No NEEDS CLARIFICATION remain.

## Phase 1: Design & Contracts
### Data Model (Hover-related Extract)
See `data-model.md` for structured fields:
- ViewportOffset(x: double, y: double)
- HoverState(tileX: int, tileY: int, hoveredObjectId: String|null, stackIndex: int)
Relationships: HoverState refers to InteractiveObject by id; updated atomically after triggers.
Validation Rules: tileX/tileY ≥ -1 (−1 indicates no hover); stackIndex valid only if hoveredObjectId != null.

### Contracts
Created:
- `contracts/coordinate-mapping.md`: Function contract + test matrix (offset sign, fractional, boundary near tile edges).
- `contracts/hover-refresh.md`: Trigger conditions and expected state transitions (movement in/out, camera scroll, object removal).
Existing `interaction-events.md` left unchanged (new triggers remain internal method calls, not semantic events yet).

### Quickstart
`quickstart.md` documents: running mvn tests; new test classes `HoverCoordinateMappingTest` (unit) and `HoverMovementRefreshIntegrationTest` (integration) to be added in implementation phase.

### Agent Context Update
Script `.specify/scripts/powershell/update-agent-context.ps1 -AgentType copilot` will append recent change entry for this feature (pending execution during full implementation or now if required).

### Constitution Re-check (Post-Design)
No new complexity added; still PASS.

## Phase 2: Task Planning Approach (Description Only)
Task Generation Strategy:
- Derive tasks for: coordinate utility function + unit tests (first), hover movement refresh logic + integration tests, viewport scroll re-evaluation, stale invalidation, nameplate placement verification, refactor tests for determinism.
- Parallelizable [P]: Coordinate mapping unit tests, movement trigger integration test scaffolding, viewport scroll test scaffolding.
Ordering:
1. Data & utility (function + unit test) [TDD]
2. Movement-trigger refresh logic + failing integration test
3. Viewport scroll re-evaluation logic + test
4. Stale invalidation logic + test adjustments
5. Nameplate placement correctness test (ensures FR-001..FR-006)
6. Performance guard test (ensure no broad scans) – optional if trivial
Estimated Output: ~18-22 tasks (leaner than template default due to narrow scope).

## Phase 3+: Future Implementation (Out of Scope for /plan)
Refer to template; unchanged.

## Complexity Tracking
| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|---------------------------------------|
| (none) | | |

## Progress Tracking
**Phase Status**:
- [x] Phase 0: Research complete (/plan command)
- [x] Phase 1: Design complete (/plan command)
- [x] Phase 2: Task planning complete (/plan command - description above, tasks not generated)
- [ ] Phase 3: Tasks generated (/tasks command)
- [ ] Phase 4: Implementation complete
- [ ] Phase 5: Validation passed

**Gate Status**:
- [x] Initial Constitution Check: PASS
- [x] Post-Design Constitution Check: PASS
- [x] All NEEDS CLARIFICATION resolved
- [ ] Complexity deviations documented (N/A)

---
*Based on Constitution v1.0.1 - See `/memory/constitution.md`*
