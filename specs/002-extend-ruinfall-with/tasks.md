# Tasks: Interactive Object System (Hover / Selection / UI Layer)

**Feature Directory**: `specs/002-extend-ruinfall-with/`  
**Input Docs**: plan.md, research.md, data-model.md, quickstart.md, contracts/interaction-events.md, spec.md  
**Tech Stack**: Java 21, FXGL (JavaFX), JUnit 5

## Execution Flow (tasks generation rationale)
1. Parsed plan & data-model â†’ enumerated entities & target source files.
2. Parsed contracts/interaction-events.md â†’ single contract file with 9 semantic events.
3. Parsed quickstart & spec user stories â†’ integration test scenarios.
4. Derived tasks: Setup â†’ Tests (contract/unit/integration) â†’ Core models/managers â†’ UI nodes â†’ Integration wiring â†’ Polish.
5. Marked [P] tasks that touch disjoint files and have no ordering dependency.
6. Ensured all tests precede implementation (TDD gate).

## Format
`[ID] [P?] Description`
- [P] means can run in parallel (different files, no dependency conflict).
- File paths are explicit. Java package root assumed: `org/dreamabout/sw/game/ruinfall/interaction` unless stated otherwise.

---
## Phase 3.1: Setup
- [X] T001 Verify / adjust `pom.xml` (FXGL + JUnit already present) and add resource bundle path if needed (ensure `src/main/resources/i18n/` on classpath).
- [X] T002 Create localization accessor class at `src/main/java/org/dreamabout/sw/game/ruinfall/localization/Messages.java` (singleton wrapper over ResourceBundle `i18n.messages`).
- [X] T003 [P] Create base `src/main/resources/i18n/messages.properties` with placeholder keys (types, actions, ui.labels.*). (Independent)

## Phase 3.2: Tests First (TDD) â€“ MUST complete (and initially fail) before Phase 3.3
### Contract Test (events)
- [X] T004 Create contract test for documented events at `src/test/java/org/dreamabout/sw/game/ruinfall/interaction/InteractionEventsContractTest.java` (assert event enum / constants or placeholders & logging invocation pattern).

### Core Logic Unit Tests
- [X] T005 [P] Create `InteractiveResolutionTest` verifying priority ordering (Enemy > NPC > Chest) & stable insertion at `src/test/java/org/dreamabout/sw/game/ruinfall/interaction/InteractiveResolutionTest.java`.
- [X] T006 [P] Create `SelectionVisibilityClearTest` verifying selection cleared when visibility checker invalidates object at `src/test/java/org/dreamabout/sw/game/ruinfall/interaction/SelectionVisibilityClearTest.java`.
- [X] T007 [P] Create `HoverCycleTest` verifying scroll cycling & stack index wrap at `src/test/java/org/dreamabout/sw/game/ruinfall/interaction/HoverCycleTest.java`.

### Integration / Acceptance Scenario Tests (from spec & quickstart)
- [X] T008 [P] Integration test hover shows nameplate (`HoverNameplateIntegrationTest`) at `src/test/java/org/dreamabout/sw/game/ruinfall/interaction/HoverNameplateIntegrationTest.java`.
- [X] T009 [P] Integration test selecting chest populates side panel (`ChestSelectionPanelIntegrationTest`).
- [X] T010 [P] Integration test empty tile click clears selection (`EmptyTileDeselectIntegrationTest`).
- [X] T011 [P] Integration test right-click selects then opens context menu (`RightClickContextMenuIntegrationTest`).
- [X] T012 [P] Integration test non-visible object ignored (`NonVisibleInteractionGuardIntegrationTest`).
- [X] T013 [P] Integration test outside click closes context menu (`ContextMenuOutsideClickIntegrationTest`).
- [X] T014 [P] Integration test switching selection updates side panel (`SelectionSwitchPanelUpdateIntegrationTest`).
- [X] T015 [P] Integration test overlapping objects priority resolution (`OverlapPriorityIntegrationTest`).
- [X] T016 [P] Integration test selection cleared on visibility loss (`VisibilityInvalidationIntegrationTest`).
- [X] T017 [P] Integration test placeholder action logs attempt (`PlaceholderActionLoggingIntegrationTest`).

## Phase 3.3: Core Implementation (Models & Managers) â€“ AFTER tests exist
- [X] T018 [P] Implement `InteractiveObject` interface at `src/main/java/org/dreamabout/sw/game/ruinfall/interaction/InteractiveObject.java`.
- [X] T019 [P] Implement enums `InteractiveObjectType.java` & `InteractiveAction.java` (two files) with documented values.
- [X] T020 [P] Implement value class `InteractiveActionItem.java`.
- [X] T021 [P] Implement `InteractiveRegistry.java` (maps, priority resolution, move/register/unregister, getStackAt, getPrimaryAt).
- [X] T022 [P] Implement `SelectionState.java` & `SelectionManager.java` (select, clear, validateSelection(VisibilityChecker)).
- [X] T023 [P] Implement `HoverState.java` & `HoverManager.java` (onHoverTile, cycle, getCurrentObject).
- [X] T024 [P] Implement model POJOs `NameplateModel.java`, `ContextMenuModel.java`, `SidePanelViewModel.java` (immutable or simple mutable containers).
- [X] T025 [P] Implement concrete interactive classes/adapters: `EnemyObjectAdapter.java`, `Chest.java`, `NPC.java` (implement InteractiveObject + summary strings).

## Phase 3.4: UI Layer (JavaFX / FXGL Nodes)
- [X] T026 Implement `AuraHighlight.java` reusable highlight node (positioning under selected object).
- [X] T027 Implement `NameplateNode.java` (renders lines, width clamp 120â€“240 px, ellipsis rule).
- [X] T028 Implement `ContextMenuNode.java` (list of InteractiveActionItem with disabled styling & placement logic: prefer right side, fallback left, vertical above then below, clamp margins 4px).
- [X] T029 Implement `SidePanelController.java` (binds to current selection & models; neutral state message when none selected).
- [X] T030 Implement `InteractionUIController.java` (mediates registry, managers, builds models on events, ensures single context menu, closes on selection change or visibility loss).

## Phase 3.5: Integration & Wiring
- [X] T031 Integrate with main game (e.g., `RuinfallApp`): mouse move â†’ hover tile mapping, scroll â†’ HoverManager.cycle, left-click â†’ select/clear logic, right-click â†’ selection + context menu, middle-click â†’ stack menu (placeholder), ESC handling order (close menu > clear selection > fallback existing behavior).
- [X] T032 Implement placeholder action dispatch (Inspect enabled; others disabled produce log + optional toast) & logging of attempts (FR-020).

## Phase 3.6: Polish & Verification
- [X] T033 [P] Add micro performance test or timing assertion for registry lookups (<0.1 ms average) at `src/test/java/org/dreamabout/sw/game/ruinfall/interaction/RegistryPerformanceTest.java`.
- [X] T034 [P] Add unit tests for context menu placement edge cases (`ContextMenuPlacementTest`).
- [X] T035 [P] Complete & review `messages.properties` (ensure all type/action/ui keys present, no TODOs).
- [X] T036 [P] Update root `README.md` feature section describing interaction system & usage.
- [X] T037 Run full test suite, refactor duplication, finalize documentation (mark feature Phase 5: Validation passed in plan.md if all green).

---
## Dependencies Summary
- T001 â†’ prerequisite for all (classpath correctness).
- T002 before any code referencing `Messages`.
- T003 independent (parallel with T002 only if accessor does not yet load keys; but keep after T002 logically for clarity).
- Tests (T004â€“T017) MUST precede corresponding implementations (T018â€“T032).
- Registry (T021) required by managers (T022, T023) & tests referencing resolution.
- Managers (T022, T023) required before UI controller (T030) & integration wiring (T031).
- Models (T024) required before UI nodes (T027â€“T030).
- Object classes (T025) required for integration tests expecting concrete types.
- UI nodes (T026â€“T029) before controller wiring (T030â€“T031 depends on them).
- Placeholder action logic (T032) after action enum & UI menu.
- Polish tasks (T033â€“T037) after all implementation tasks.

## Parallelization Guidance
Phase 3.2 parallel group example (after T004 created):
```
# Run these in parallel:
T005 InteractiveResolutionTest
T006 SelectionVisibilityClearTest
T007 HoverCycleTest
T008 HoverNameplateIntegrationTest
T009 ChestSelectionPanelIntegrationTest
T010 EmptyTileDeselectIntegrationTest
T011 RightClickContextMenuIntegrationTest
T012 NonVisibleInteractionGuardIntegrationTest
T013 ContextMenuOutsideClickIntegrationTest
T014 SelectionSwitchPanelUpdateIntegrationTest
T015 OverlapPriorityIntegrationTest
T016 VisibilityInvalidationIntegrationTest
T017 PlaceholderActionLoggingIntegrationTest
```
Phase 3.3 parallel group example (after all tests exist):
```
# Suggested batch:
T018 T019 T020 T021 T024 T025
# Then when T021 done you can start T022 & T023
```
Phase 3.4 UI (sequential core):
```
T026 -> (T027, T028, T029 in parallel) -> T030 -> T031 -> T032
```
Polish parallel batch:
```
T033 T034 T035 T036 (then T037)
```

## Validation Checklist (to verify upon completion)
- [X] All contracts (interaction-events.md) have at least one contract test (T004).
- [X] All entities in data-model.md implemented (T018â€“T025 coverage) & tested.
- [X] All user stories mapped to integration tests (T008â€“T017).
- [X] Tests written before implementations they assert.
- [ ] Each [P] task touches distinct files or is read-only. (Needs final audit)
- [X] Context menu placement & visibility invalidation edge cases tested (T016, T034).
- [X] Performance consideration captured (T033).
- [X] Localization centralization implemented (T002, T035).

## Notes
- Keep classes in small focused files; avoid premature optimization.
- Ensure no JavaFX thread violations in logic tests: models/managers stay pure.
- Log format for placeholder actions: `ACTION_PLACEHOLDER objectId=<id> action=<code>`.
- ESC handling priority: (1) Close menu if open, (2) Clear selection if present, (3) Fallback to existing game ESC behavior.
- Nameplate width clamping & ellipsis: validate with a long single-word test string.

---
*Generated on 2025-10-06 based on current design artifacts.*
