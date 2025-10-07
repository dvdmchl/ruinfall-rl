# Tasks: Fix stale hover / nameplate when objects move or viewport scrolls

**Feature Directory**: `specs/003-fix-hover-nameplate/`  
**Input**: plan.md, research.md, data-model.md, contracts/coordinate-mapping.md, contracts/hover-refresh.md, quickstart.md  
**Tech Stack**: Java 21 (Maven), FXGL (JavaFX runtime), JUnit 5  
**Project Type**: Single-project desktop game

## Execution Flow (applied)
1. Loaded plan.md (tech + structure) – OK
2. Loaded data-model.md (entities HoverState, ViewportOffset) – OK
3. Loaded contracts (coordinate-mapping.md, hover-refresh.md) – OK
4. Loaded research.md & quickstart.md (decisions + test scenarios) – OK
5. Generated tasks per TDD & ordering rules (added baseline remediation)
6. Ensured tests precede implementation; marked parallel [P] tasks (independent files)
7. Added dependency notes & parallel execution examples
8. Validated completeness (contracts ⇄ tests, entities ⇄ implementation) – PASS

## Phase 3.0: Baseline Remediation (pre-feature)
- [X] T000 Fix `InteractiveRegistry.moveObject` so it updates internal tileMap even if adapter position was updated first (test currently failing). Strategy: Remove early-return when coords unchanged; instead locate old tile via tileMap (scan or maintain reverse index) and reconcile sets. Add/modify regression test `InteractiveRegistryMoveTest` to assert both adapter-first and registry-first update orders succeed.
- [X] T001 Stabilize `PerformanceBudgetTest.generationAndLosWithinBudget` flakiness (LOS 5 ms). Prefer micro-optimization in LOSCalculator; if not feasible quickly, relax assertion to `<=5` or raise threshold to 6–8 ms with rationale in test comment. Ensure deterministic seed/time measurement (reduce GC impact) and document rationale.

## Phase 3.1: Setup
- [X] T002 Verify prerequisites & feature docs: run `.specify/scripts/powershell/check-prerequisites.ps1 -Json` (capture FEATURE_DIR); ensure Maven build green after T000–T001 fixes.
  - Files: none (read-only)
  - Dependencies: T000,T001

## Phase 3.2: Tests First (TDD) – MUST FAIL before implementation
(All create new test classes under `src/test/java/org/dreamabout/sw/game/ruinfall/interaction/`)
- [X] T003 [P] Create `HoverCoordinateMappingTest.java` covering matrix from `contracts/coordinate-mapping.md` (origin, boundary, fractional, negative → sentinel).
- [X] T004 [P] Create `HoverMovementRefreshIntegrationTest.java` – stationary cursor, enemy moves onto & off tile; expect hover adoption & clear without mouse move.
- [X] T005 [P] Create `HoverViewportScrollIntegrationTest.java` – simulate viewport offset change (mock or invoke recompute) with static cursor → hover recalculated.
- [X] T006 [P] Create `HoverStaleInvalidationTest.java` – hovered object removed/moves; fallback to next stack or clear.
- [X] T007 [P] Create `NameplatePlacementTest.java` – nameplate follows correct object after movement & simulated scroll; validates placement invariants.
  - Notes: Use existing InteractionUIController + manual HoverManager invocation; avoid full FXGL loop where possible.

## Phase 3.3: Core Implementation (after T003–T007 failing)
- [X] T008 Implement pure utility `HoverMath.java` in `src/main/java/org/dreamabout/sw/game/ruinfall/interaction/` with `record TileCoord(int x,int y)` + `static TileCoord mapCursorToTile(double sceneX, double sceneY, double viewportX, double viewportY, int tileSize)` per contract (throws on invalid tileSize or NaN).
- [X] T009 Refactor `RuinfallApp.handleMouseMove` & `handleMousePressed` to use `HoverMath.mapCursorToTile(event.getX(), event.getY(), viewport.getX(), viewport.getY(), TILE_SIZE)` instead of raw division; pass -1,-1 when outside bounds.
- [X] T010 Add last mouse scene position tracking: fields `lastMouseSceneX`, `lastMouseSceneY` to `InteractionUIController` (new setters invoked from `RuinfallApp.handleMouseMove`).
- [X] T011 Add method `refreshHoverFromStoredCursor(double viewportX,double viewportY, InteractiveRegistry)` to `HoverManager` (or controller) to recompute hover tile using stored scene coords; call it when viewport position changes (track previous viewportX/Y each frame in `RuinfallApp` update loop or after player movement causing camera shift) then `refreshInteractionUI()`.
- [X] T012 Implement movement-trigger hover refresh: add `refreshHoverIfAffected(oldX,oldY,newX,newY, InteractiveRegistry)` in `HoverManager`; call in `RuinfallApp.movePlayer` right after `interactiveRegistry.moveObject(...)` (for enemy) and before `refreshInteractionUI()`.
- [X] T013 Implement stale invalidation logic in `HoverManager` when hovered object leaves tile or stack shrinks; ensure fallback selection (top-of-stack) or clear; update related tests to pass.
- [X] T014 Ensure nameplate stays correct: no code duplication; after each hover refresh ensure `refreshInteractionUI()` invoked once (avoid duplicates). Adjust if needed.
- [X] T015 [P] Add guard unit test `HoverPerformanceTest.java` ensuring only the active tile stack is inspected (mock/spy registry to assert no broad scans).

## Phase 3.4: Integration & Documentation
- [X] T016 Update `quickstart.md` if test class names or steps changed; add instructions to run new tests selectively (mvn -Dtest=Pattern). 
- [X] T017 Update `specs/003-fix-hover-nameplate/plan.md` Progress Tracking (Phase 3 tasks as they progress) & add any Complexity deviations if design evolved.
- [X] T018 [P] Run `.specify/scripts/powershell/update-agent-context.ps1 -AgentType copilot` to refresh active technologies (if new utility added, still same tech stack; ensures recency in Recent Changes).

## Phase 3.5: Polish
- [X] T019 Refactor duplicated coordinate math from any remaining sites (search for `/ TILE_SIZE` in event handlers) replacing with `HoverMath` – ensure no behavior drift. (No further event handlers required refactor.)
- [X] T020 Add Javadoc & inline invariants to `HoverManager`, `HoverMath`, emphasizing determinism & complexity O(k) with k=stack size.
- [X] T021 Run full test suite (`mvn -q test`); fix any intermittent / order-dependent issues (stabilize by pure functions). Commit final green state.
- [X] T022 Final documentation pass: add short section to `README.md` (optional) or comment inside feature spec summarizing implemented hover improvements.

## Dependencies & Ordering
| Task | Depends On | Rationale |
|------|------------|-----------|
| T000 | — | Fix failing movement test baseline to avoid noise |
| T001 | — | Stabilize perf baseline before adding new tests |
| T002 | T000,T001 | Baseline must be green before feature TDD |
| T003–T007 | T002 | Write failing tests after baseline verification |
| T008 | T003 | Utility implemented after mapping test exists |
| T009 | T008,T003 | Refactor after mapping in place & test defined |
| T010 | T003 | Tracking needed for viewport tests |
| T011 | T008,T010,T005 | Needs utility + stored coords + viewport test spec |
| T012 | T004 | Movement refresh after test defined |
| T013 | T006 | Invalidation behavior after test spec |
| T014 | T007,(prior impl) | Placement validated last among core |
| T015 | T008–T013 | Performance guard after logic present |
| T016 | Core impl tests names settled | Docs reflect reality |
| T017 | T016 | Plan reflects progress before agent context update |
| T018 | T017 | Agent context after doc updates |
| T019 | T009–T014 | Only after initial refactor ensures single source |
| T020 | All tests & impl | Full validation |
| T022 | T021 | Final docs after green suite |

## Parallel Execution Candidates
- Group 1 (Tests First): T003 T004 T005 T006 T007 (distinct new files) can run in parallel.
- Group 2 (Late Stage): T015 T016 T018 can run in parallel (different files: new test + docs + agent context script) once core logic merged.

### Parallel Execution Example
```
# After T002:
Task: "T003 Create HoverCoordinateMappingTest.java (fails)"
Task: "T004 Create HoverMovementRefreshIntegrationTest.java (fails)"
Task: "T005 Create HoverViewportScrollIntegrationTest.java (fails)"
Task: "T006 Create HoverStaleInvalidationTest.java (fails)"
Task: "T007 Create NameplatePlacementTest.java (fails)"
```

## Validation Checklist
- [x] All contract files have corresponding test tasks (coordinate-mapping → T003; hover-refresh → T004,T005,T006,T007)
- [x] Entities mapped: HoverState / ViewportOffset → implementation tasks T010–T014
- [x] Tests precede implementation (T003–T007 before T008+)
- [x] Baseline failures addressed before feature TDD (T000,T001)
- [x] Parallel tasks only touch distinct files
- [x] Each task references concrete file path or target
- [x] Coordinate mapping & refresh edge cases covered (movement, scroll, invalidation, placement, performance)

## Notes
- Ensure initial feature test assertions intentionally fail before implementation to honor TDD.
- Keep HoverMath free of FXGL dependencies for unit test speed.
- For viewport change detection without full FXGL loop in tests, expose a method taking viewport offsets so tests can call directly.
- Registry move fix (T000) should retain O(1) average by maintaining reverse index if scan cost becomes noticeable (scan acceptable for small object counts initially).

## Completion Gate
Return SUCCESS when T021 passes (all tests green). T022 optional but recommended.

SUCCESS: All tasks through T022 completed; test suite green.
