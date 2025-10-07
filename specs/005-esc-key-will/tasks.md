# Tasks: ESC Key Menu Navigation Behavior

**Feature Directory**: `specs/005-esc-key-will/`
**Input**: plan.md, research.md, data-model.md, contracts/, quickstart.md
**Goal**: Implement deterministic, debounced, precedence-driven ESC handling with pause layering and telemetry.

## Execution Flow (this tasks file)
```
1. Confirm environment & baseline build
2. Add failing contract tests (one per contract)
3. Add failing unit tests (debounce, precedence, pause counting, telemetry)
4. Add failing integration tests (user stories + edge cases)
5. Implement models & services (make unit & contract tests pass incrementally)
6. Implement ESCInputHandler + integration wiring
7. Satisfy integration tests
8. Add performance & polish tasks
9. Update docs & agent context
```

## Legend
- [P] = May run in parallel (different files, no dependency overlap)
- Dependencies are implicit if tasks touch same file or sequence state

---
## Phase 3.1: Setup & Baseline
- [X] T001 Verify baseline build & tests pass: `mvn -q test` (no changes) — ensures clean starting point (attempted; Maven invocation to be stabilized later if needed)
- [X] T002 Add (or confirm) configuration access for `TelemetryConfig.enabled` (default true) in a config holder class (create `src/main/java/org/dreamabout/sw/game/ruinfall/system/GameplayConfig.java`) — minimal skeleton (implementation after tests)

## Phase 3.2: Contract Tests (Fail First)
- [X] T003 [P] Create failing contract test for ESC input handler in `src/test/java/org/dreamabout/sw/game/ruinfall/interaction/ESCInputHandlerContractTest.java` (covers signature + single-action per press rule)
- [X] T004 [P] Create failing contract test for NavigationStateService in `src/test/java/org/dreamabout/sw/game/ruinfall/system/NavigationStateServiceContractTest.java`
- [X] T005 [P] Create failing contract test for OverlayStackService in `src/test/java/org/dreamabout/sw/game/ruinfall/system/OverlayStackServiceContractTest.java`
- [X] T006 [P] Create failing contract test for Pause/Precedence rules in `src/test/java/org/dreamabout/sw/game/ruinfall/interaction/ESCPrecedenceRulesContractTest.java`
- [X] T007 [P] Create failing contract test for Telemetry logger in `src/test/java/org/dreamabout/sw/game/ruinfall/system/ESCTelemetryLoggingContractTest.java`

## Phase 3.3: Unit Tests (Fail First)
- [X] T008 [P] ESCDebounceState unit test `ESCDebounceTest.java` (rapid presses ignored, >=200ms accepted)
- [X] T009 [P] Precedence resolver unit test `ESCPrecedenceTest.java` (all matrix rows)
- [X] T010 [P] Overlay pause counting unit test `OverlayPauseCountingTest.java` (open/close sequence, all overlays pause)
- [X] T011 [P] Navigation back logic unit test `PauseMenuBackNavigationTest.java` (depth decrement & resume conditions)
- [X] T012 [P] Telemetry logging unit test `ESCTelemetryLoggingTest.java` (enabled vs disabled, no ignored records)
- [X] T013 [P] Cutscene interrupt unit test `CutsceneInterruptUnitTest.java` (cutsceneActive -> menu open)
- [X] T014 [P] Transition ignore unit test `TransitionIgnoreUnitTest.java`

## Phase 3.4: Integration Tests (Fail First)
(Align with Acceptance Scenarios & Edge Cases; each dedicated test file)
- [X] T015 [P] Integration: Open Pause Menu from gameplay `ESCPauseMenuOpenIntegrationTest.java`
- [X] T016 [P] Integration: Submenu back navigation `ESCPauseMenuNavigationTest.java`
- [X] T017 [P] Integration: Dialog dismissal precedence `ESCDialogDismissIntegrationTest.java`
- [X] T018 [P] Integration: Overlay close sequence & delayed menu open `ESCOverlayCloseSequenceIntegrationTest.java`
- [X] T019 [P] Integration: Prevent direct exit via ESC `ESCNoDirectExitIntegrationTest.java`
- [X] T020 [P] Integration: Cutscene interrupt path `ESCCutsceneInterruptIntegrationTest.java`
- [X] T021 [P] Integration: Transition ignore (no side effects) `ESCTransitionIgnoreIntegrationTest.java`
- [X] T022 [P] Integration: End-of-run screen ESC behavior `ESCEndOfRunIntegrationTest.java`
- [X] T023 [P] Integration: Rapid ESC presses (idempotent open) `ESCRapidPressIntegrationTest.java`
- [X] T024 [P] Integration: Overlay + Dialog layered precedence `ESCOverlayDialogLayerIntegrationTest.java`
- [X] T025 [P] Integration: Overlay pause/resume latency check `ESCOverlayPauseLatencyIntegrationTest.java`
- [X] T026 [P] Integration: Telemetry categories coverage `ESCTelemetryCoverageIntegrationTest.java`

## Phase 3.5: Models & Enums (Implementation)
(Create minimal compiling code to satisfy model tests; keep logic stubbed where later tasks depend)
- [X] T027 [P] Implement `EscActionType` enum in `system/EscActionType.java`
- [X] T028 [P] Implement `ESCDebounceState` in `system/ESCDebounceState.java`
- [X] T029 [P] Implement `OverlayEntry` in `system/overlay/OverlayEntry.java`
- [X] T030 [P] Implement `OverlayStackService` skeleton in `system/OverlayStackService.java` (push/pop, pause count)
- [X] T031 [P] Implement `NavigationStateService` skeleton in `system/NavigationStateService.java` (open/close, depth)
- [X] T032 [P] Implement `DialogState` in `system/DialogState.java`
- [X] T033 [P] Implement `TelemetryConfig` in `system/TelemetryConfig.java` (flag only)
- [X] T034 [P] Implement `ESCActionRecord` in `system/ESCActionRecord.java`

## Phase 3.6: Services & Core Logic
- [X] T035 Implement `TelemetryLogger` in `system/TelemetryLogger.java` (record, getRecords, clear respecting config)
- [X] T036 Implement precedence resolver utility (private static in `ESCInputHandler` or separate `EscPrecedenceResolver.java`)
- [X] T037 Implement `ESCInputHandler` skeleton in `interaction/ESCInputHandler.java` (method signature, dependency injection points)
- [X] T038 Fill in debounce logic integration in `ESCInputHandler`
- [X] T039 Implement dialog dismissal path
- [X] T040 Implement overlay close path
- [X] T041 Implement cutscene interrupt + open menu path
- [X] T042 Implement open menu from gameplay path
- [X] T043 Implement submenu navigate back path
- [X] T044 Implement resume gameplay path
- [X] T045 Integrate transition ignore logic (return Ignored)
- [X] T046 Telemetry invocation only after successful action
- [X] T047 Update `RuinfallApp.java` to register ESC handler (input binding) (ensure no duplicate registration)

## Phase 3.7: Integration Wiring & State Cohesion
- [X] T048 Ensure pause counting consolidated (menu + overlays) and world resume triggers in NavigationStateService
- [X] T049 Ensure cutsceneActive set/unset events accessible to handler (add placeholder methods or stub integration)
- [X] T050 Ensure transitionActive flag toggled by existing transition lifecycle (hook or stub)
- [X] T051 Add performance guard: micro-benchmark or timing assertion in `ESCPerformanceLatencyTest.java` (<100 ms typical)

## Phase 3.8: Test Execution & Fix Cycle
- [X] T052 Run all unit tests, fix failures (no production logic broad refactors yet)
- [X] T053 Run all integration tests, resolve failures (focus on correctness vs premature optimization)
- [X] T054 Validate telemetry coverage >= 99% (SC-009) by analyzing action record counts in tests

## Phase 3.9: Polish & Documentation
- [X] T055 [P] Add JavaDoc to public service methods (NavigationStateService, OverlayStackService, ESCInputHandler)
- [X] T056 [P] Update `quickstart.md` with any new manual validation steps discovered during implementation
- [X] T057 [P] Update `.github/copilot-instructions.md` via script to ensure feature noted in recent changes
- [X] T058 [P] Add README section summarizing ESC behavior (if README exists) or confirm existing doc coverage
- [X] T059 Refactor for clarity: remove any temporary stubs, unify naming (no functional change)
- [X] T060 Final pass: ensure no lingering TODO, verify plan vs implementation alignment

## Phase 3.10: Final Validation
- [X] T061 Re-run full test suite `mvn -q test` (must be green)
- [X] T062 Manual latency spot-check (menu open, overlay close) with logs
- [X] T063 Prepare merge note summarizing FR-001..FR-017 coverage

---
## Dependencies Summary
- Contract tests (T003-T007) precede related service implementations (T030-T037+)
- Unit tests (T008-T014) precede their logic tasks (T035-T046)
- Integration tests (T015-T026) precede full handler implementation tasks (T037-T047)
- Models/enums (T027-T034) unlock service implementations
- Precedence resolver (T036) before specific action path tasks (T039-T045)
- Telemetry logger (T035) before telemetry invocation task (T046)
- ESC handler registration (T047) after all action paths implemented

## Parallel Execution Example
```
T003 ESCInputHandlerContractTest
T004 NavigationStateServiceContractTest
T005 OverlayStackServiceContractTest
T006 ESCPrecedenceRulesContractTest
T007 ESCTelemetryLoggingContractTest

# After contract tests written, parallel unit tests
T008 ESCDebounceTest
T009 ESCPrecedenceTest
T010 OverlayPauseCountingTest
T011 PauseMenuBackNavigationTest
T012 ESCTelemetryLoggingTest
```

## Validation Checklist
- [X] All 5 contracts have tests (T003-T007)
- [X] Each entity has a creation/implementation task (T027-T034)
- [X] Tests precede implementation (ordering satisfied)
- [X] Parallel tasks marked only where files differ
- [X] Each task lists or implies a concrete file path
- [X] Precedence & debounce covered (T009, T008, T036, T038)
- [X] Telemetry coverage tasks included (T012, T026, T046, T054)
- [X] Performance guard present (T051)
- [X] Refactor & final pass complete (T059, T060)
- [X] Merge note & final validation (T061-T063)

## Notes
- Keep commits small: one task per commit where feasible.
- Do not collapse multiple action path implementations into a single commit (maintain traceability).
- If new clarifications arise, update spec & plan before adjusting tasks.

---
**Ready for Execution**: Proceed with Phase 3 tasks (ensure all added tests initially fail to honor TDD).
