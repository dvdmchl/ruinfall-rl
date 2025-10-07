# Implementation Plan: Interactive Object System (Hover / Selection / UI Layer)

**Branch**: `002-extend-ruinfall-with` | **Date**: 2025-10-06 | **Spec**: `specs/002-extend-ruinfall-with/spec.md`
**Input**: Feature specification from `/specs/002-extend-ruinfall-with/spec.md`

## Execution Flow (/plan command scope)
```
1. Load feature spec from Input path â†’ OK
2. Fill Technical Context (scan for NEEDS CLARIFICATION) â†’ No outstanding markers (spec gate READY)
3. Fill the Constitution Check section â†’ No violations
4. Evaluate Constitution Check â†’ PASS (Initial)
5. Execute Phase 0 â†’ research.md (produced below)
6. Execute Phase 1 â†’ data-model.md, quickstart.md, contracts/ (event contracts), agent file update
7. Re-evaluate Constitution Check â†’ PASS (Post-Design)
8. Plan Phase 2 â†’ Task generation approach described
9. STOP
```

**IMPORTANT**: All clarifications already resolved inside the feature spec. This plan proceeds directly; no /clarify gating needed.

## Summary
Introduce an extensible interactive object system enabling: single hover nameplate, selection persistence, side panel info, and context menus with placeholder actions (Attack, Loot, Talk disabled; Inspect enabled). Supports stacked objects with scroll-wheel cycling and stack menu trigger. Ensures visibility gating (VISIBLE only) and immediate selection/context menu invalidation on loss of visibility. UI elements auto-clamp to viewport and respect side panel reserved area. Architecture centered on a registry + deterministic resolution priority (Enemy > NPC > Chest; stable insertion order) decoupled from FXGL rendering layer.

## Technical Context
**Language/Version**: Java 21  
**Primary Dependencies**: FXGL 21.1 (bundles JavaFX), JUnit 5.10.2  
**Storage**: N/A (in-memory runtime state)  
**Testing**: JUnit 5 (logic-level tests; UI logic separated from JavaFX nodes for testability)  
**Target Platform**: Desktop (JavaFX)  
**Project Type**: Single project (game)  
**Performance Goals**: Maintain 60 FPS; hover/selection resolution < 0.1 ms per frame (tiny set of interactives)  
**Constraints**: No blocking operations on FXGL game loop; all UI updates on JavaFX Application Thread; memory overhead minimal (few dozen objects)  
**Scale/Scope**: Early prototypeâ€”< 50 interactive objects concurrently expected  

## Constitution Check
*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*
- Principle I (Maintainable code): PASS â€” Uses composition (registry + managers) and clear separation (models vs nodes)
- Principle II (Test-first reliability): PASS â€” Plan enumerates logic tests (selection, visibility invalidation, priority, cycling)
- Principle III (Cohesive player experience): PASS â€” Unified interaction patterns; ESC for deselect; consistent placeholder feedback (toast/log)
- Principle IV (English specification & communication): PASS â€” All artifacts generated in English (this answer excepted per locale output rule; source docs remain English)
- Delivery constraints: PASS â€” Assets/UI within FXGL; no secrets; docs in spec tree

No violations â†’ Complexity Tracking remains empty.

## Project Structure

### Documentation (this feature)
```
specs/002-extend-ruinfall-with/
â”śâ”€â”€ spec.md
â”śâ”€â”€ plan.md                # This file
â”śâ”€â”€ research.md            # Phase 0 output
â”śâ”€â”€ data-model.md          # Phase 1 output
â”śâ”€â”€ quickstart.md          # Phase 1 output
â”śâ”€â”€ contracts/             # Event contracts (not HTTP APIs)
â”‚   â””â”€â”€ interaction-events.md
â””â”€â”€ tasks.md               # Phase 2 (future /tasks command)
```

### Source Code (repository root)
(Additions for this feature only)
```
src/main/java/org/dreamabout/sw/game/ruinfall/
â”śâ”€â”€ interaction/
â”‚   â”śâ”€â”€ InteractiveObject.java
â”‚   â”śâ”€â”€ InteractiveObjectType.java
â”‚   â”śâ”€â”€ InteractiveAction.java
â”‚   â”śâ”€â”€ InteractiveActionItem.java
â”‚   â”śâ”€â”€ InteractiveRegistry.java
â”‚   â”śâ”€â”€ SelectionState.java
â”‚   â”śâ”€â”€ SelectionManager.java
â”‚   â”śâ”€â”€ HoverState.java
â”‚   â”śâ”€â”€ HoverManager.java
â”‚   â”śâ”€â”€ ContextMenuModel.java
â”‚   â”śâ”€â”€ NameplateModel.java
â”‚   â”śâ”€â”€ SidePanelViewModel.java
â”‚   â”śâ”€â”€ EnemyObjectAdapter.java       # Adapts existing Enemy model
â”‚   â”śâ”€â”€ Chest.java
â”‚   â”śâ”€â”€ NPC.java
â”‚   â””â”€â”€ InteractionUIController.java  # JavaFX/FXGL UI wiring
â”śâ”€â”€ ui/
â”‚   â”śâ”€â”€ NameplateNode.java
â”‚   â”śâ”€â”€ ContextMenuNode.java
â”‚   â”śâ”€â”€ SidePanelController.java
â”‚   â””â”€â”€ AuraHighlight.java
â””â”€â”€ localization/
    â””â”€â”€ Messages.java                 # ResourceBundle accessor

src/main/resources/i18n/
â””â”€â”€ messages.properties

src/test/java/org/dreamabout/sw/game/ruinfall/interaction/
â”śâ”€â”€ InteractiveResolutionTest.java
â”śâ”€â”€ SelectionVisibilityClearTest.java
â””â”€â”€ HoverCycleTest.java
```

**Structure Decision**: Single-project game; introduced segregated interaction + ui packages to isolate logic models from FXGL node classes; localization accessor separated for future i18n expansion.

## Phase 0: Outline & Research
*Completed â€” see `research.md`.*
Key researched decisions: registry-based resolution vs per-frame queries; cycling strategy via ordered sublist; JavaFX node anchoring & clamping util; ResourceBundle centralization; event contract mapping to internal listeners (no network APIs).

## Phase 1: Design & Contracts
*Completed â€” see `data-model.md`, `contracts/interaction-events.md`, and `quickstart.md`.*
Highlights:
- Deterministic resolution algorithm O(k) where k objects at tile (k small); global map for (x,y) â†’ LinkedHashSet preserving insertion order.
- Separation: Pure POJO models (testable headless) + JavaFX nodes (visual layer). Selection & hover managers expose immutable snapshots for UI binding.
- Localization via Messages.get(key).
- Placeholder actions produce log + toast; disabled ones visually greyed.

## Phase 2: Task Planning Approach (Preview Only)
**Task Generation Strategy**:
- Each model / manager class â†’ creation task
- Each event contract â†’ logic test (pre-implementation) task
- UI nodes â†’ after logic passing
- Integration tests for acceptance scenarios (hover nameplate, selection persist, context menu open/close, visibility invalidation) using simulated manager calls (minimizes JavaFX reliance)

**Ordering Strategy**:
1. Data models & registry
2. Selection + hover + visibility invalidation logic tests
3. Action + context menu model
4. UI nodes & controller wiring
5. RuinfallApp integration (input & ESC behavior)
6. Final acceptance tests

**Estimated Output**: ~22â€“26 tasks

## Phase 3+: Future Implementation
(Out of scope for /plan; implemented immediately in this session per user request.)

## Complexity Tracking
_None â€” No constitutional violations._

## Progress Tracking
**Phase Status**:
- [x] Phase 0: Research complete (/plan command)
- [x] Phase 1: Design complete (/plan command)
- [ ] Phase 2: Task planning complete (/plan command - describe approach only)
- [ ] Phase 3: Tasks generated (/tasks command) *Skipped â€” direct implementation this iteration*
- [x] Phase 4: Implementation complete (will mark after code & tests added)
- [x] Phase 5: Validation passed (mvn test 2025-10-07)

**Gate Status**:
- [x] Initial Constitution Check: PASS
- [x] Post-Design Constitution Check: PASS
- [x] All NEEDS CLARIFICATION resolved
- [x] Complexity deviations documented (N/A)

---
*Based on Constitution v1.0.1 - See `/memory/constitution.md`*
