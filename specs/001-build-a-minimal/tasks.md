# Tasks: Minimal Single-Level Roguelike Core (Ruinfall)

**Input**: Design documents from `specs/001-build-a-minimal/` (plan.md, data-model.md, research.md, quickstart.md)
**Prerequisites**: plan.md (complete), research.md, data-model.md, contracts/ (placeholder only, no external endpoints)

## Execution Flow (main)
```
1. Load plan.md → extract tech (Java 21, FXGL 21.1, JUnit 5) & structure
2. Load data-model.md → entities → model tasks
3. Load research.md → systems & decisions → system tasks
4. Load quickstart.md → gameplay loop & scenarios → integration tests
5. Generate tasks (setup → tests → models → systems → integration → polish)
6. Mark parallel-capable tasks [P] (distinct files, no ordering dependency)
7. Number tasks (T001 ...)
8. Build dependency notes & parallel examples
9. Validate coverage (entities, systems, scenarios) → SUCCESS
```

## Format: `[ID] [P?] Description`
[P] = Can be executed in parallel (different files, independent)

## Phase 3.1: Setup
- [x] T001 Verify Maven project configuration (Java 21, FXGL 21.1, JUnit 5) and add comment header noting feature in `pom.xml`
- [x] T002 Create package scaffolding for systems in `src/main/java/org/dreamabout/sw/game/ruinfall/system/` (no logic yet) and test packages under `src/test/java/org/dreamabout/sw/game/ruinfall/`

## Phase 3.2: Tests First (TDD) – MUST FAIL INITIALLY
- [x] T003 [P] Create generation connectivity test in `src/test/java/org/dreamabout/sw/game/ruinfall/generation/DungeonGeneratorConnectivityTest.java` (assert all FLOOR tiles reachable; player & enemy spawn on FLOOR)
- [x] T004 [P] Create room placement constraints test in `src/test/java/org/dreamabout/sw/game/ruinfall/generation/DungeonGeneratorRoomsTest.java` (no overlaps; room count within bounds)
- [x] T005 [P] Create LOS radius & blocking test in `src/test/java/org/dreamabout/sw/game/ruinfall/visibility/LOSCalculatorTest.java` (walls block; tiles beyond radius 8 not visible)
- [x] T006 [P] Create fog state transition test in `src/test/java/org/dreamabout/sw/game/ruinfall/visibility/FogOfWarStateTransitionTest.java` (VISIBLE→MEMORY, UNSEEN never reverts)
- [x] T007 [P] Create damage cadence test in `src/test/java/org/dreamabout/sw/game/ruinfall/combat/DamageCadenceTest.java` (contact damage only once per turn)
- [x] T008 [P] Create enemy random movement test in `src/test/java/org/dreamabout/sw/game/ruinfall/ai/EnemyMovementTest.java` (moves ≤1 tile, stays in FLOOR, no walls)
- [x] T009 [P] Create restart flow integration test in `src/test/java/org/dreamabout/sw/game/ruinfall/integration/RestartFlowTest.java` (HP reset, dungeon regenerates or resets with same seed if provided)

## Phase 3.3: Core Models (Entities & Data Structures)
- [x] T010 [P] Implement enums & tile model: `TileType` (WALL/FLOOR), `VisibilityState` (UNSEEN/MEMORY/VISIBLE) and class `Tile` in `src/main/java/org/dreamabout/sw/game/ruinfall/model/` (fields: type, visibility)
- [x] T011 [P] Implement `Room` and `Corridor` classes in `src/main/java/org/dreamabout/sw/game/ruinfall/model/` (with center pre-compute & points list)
- [x] T012 [P] Implement `Dungeon` class in `src/main/java/org/dreamabout/sw/game/ruinfall/model/Dungeon.java` (width, height, tiles[][], rooms list, seed)
- [x] T013 [P] Implement `Player` class in `src/main/java/org/dreamabout/sw/game/ruinfall/model/Player.java` (x,y,hp,maxHp,lastDamageTurn)
- [x] T014 [P] Implement `Enemy` class in `src/main/java/org/dreamabout/sw/game/ruinfall/model/Enemy.java` (x,y,lastDamageTurn)
- [x] T015 [P] Implement `TurnSystem` class in `src/main/java/org/dreamabout/sw/game/ruinfall/model/TurnSystem.java` (currentTurn, runEnded)
- [x] T016 [P] Implement `HUDController` skeleton in `src/main/java/org/dreamabout/sw/game/ruinfall/ui/HUDController.java` (methods: initHUD(player), updateHP(player), showGameOver(), hideGameOver())

## Phase 3.4: Systems Implementation
- [x] T017 Implement `DungeonGenerator` in `src/main/java/org/dreamabout/sw/game/ruinfall/system/DungeonGenerator.java` (room placement, corridor carving, connectivity guarantee, seeded RNG API: generate(seed,width,height))
- [x] T018 Implement `LOSCalculator` in `src/main/java/org/dreamabout/sw/game/ruinfall/system/LOSCalculator.java` (Bresenham rays, radius check, blocked by WALL)
- [x] T019 Implement `VisibilitySystem` in `src/main/java/org/dreamabout/sw/game/ruinfall/system/VisibilitySystem.java` (recomputeVisibility(player,dungeon) applying fog transitions)
- [x] T020 Implement `EnemyAI` in `src/main/java/org/dreamabout/sw/game/ruinfall/system/EnemyAI.java` (random cardinal move using shared RNG; fallback stay)
- [x] T021 Implement `DamageSystem` in `src/main/java/org/dreamabout/sw/game/ruinfall/system/DamageSystem.java` (applyContactDamage(player,enemy,turnSystem) once per turn)
- [x] T022 Implement `RestartService` in `src/main/java/org/dreamabout/sw/game/ruinfall/system/RestartService.java` (reset or regenerate dungeon, player/enemy state, preserve seed if provided)
- [x] T023 Implement `PerformanceMonitor` in `src/main/java/org/dreamabout/sw/game/ruinfall/system/PerformanceMonitor.java` (timed section utility; enabled via system property `ruinfall.debugPerf`)

## Phase 3.5: Integration (GameApplication Wiring)
- [x] T024 Integrate generation + entity spawn + RNG seed parse/log into `RuinfallApp` (store seed; log start seed)
- [x] T025 Add input handling (arrow keys move player 1 tile if FLOOR; ESC exit; R triggers RestartService) in `RuinfallApp`
- [x] T026 Wire visibility recomputation after each player move, update render (tile node opacity or layering) in `RuinfallApp`
- [x] T027 Add HUD integration & damage feedback (HP label updates; floating text on damage; game over overlay) in `RuinfallApp`

## Phase 3.6: Polish
- [x] T028 [P] Add performance budget test `src/test/java/org/dreamabout/sw/game/ruinfall/perf/PerformanceBudgetTest.java` (assert generation <50ms, LOS <2ms on default map)
- [x] T029 [P] Update `specs/001-build-a-minimal/quickstart.md` with `ruinfall.debugPerf` flag & restart behavior notes
- [x] T030 Remove placeholder test file `src/test/java/org/dreamabout/sw/game/ruinfall/PlaceholderTest.java`
- [x] T031 [P] Create root `README.md` summarizing feature scope, build, run, seed & perf flag usage

## Dependencies
- Tests (T003–T009) precede implementations (T010+)
- Model classes (T010–T016) required before systems (T017–T023)
- DungeonGenerator (T017) before LOS/Visibility (T018–T019) and before integration (T024)
- Visibility (T019) before integration of fog (T026)
- EnemyAI (T020) + DamageSystem (T021) before HUD damage feedback (T027)
- RestartService (T022) before restart input wiring (T025) and restart tests (T009 depends conceptually on its final behavior)
- PerformanceMonitor (T023) before performance test (T028)
- Remove Placeholder test (T030) after new test suite stable

## Parallel Execution Examples
```
# Example 1: Run all initial test creation tasks in parallel (files distinct)
Task: T003 DungeonGeneratorConnectivityTest
Task: T004 DungeonGeneratorRoomsTest
Task: T005 LOSCalculatorTest
Task: T006 FogOfWarStateTransitionTest
Task: T007 DamageCadenceTest
Task: T008 EnemyMovementTest
Task: T009 RestartFlowTest

# Example 2: Implement independent model classes in parallel
Task: T010 Tile & enums
Task: T011 Room & Corridor
Task: T012 Dungeon
Task: T013 Player
Task: T014 Enemy
Task: T015 TurnSystem
Task: T016 HUDController

# Example 3: Later polish parallel
Task: T028 PerformanceBudgetTest
Task: T029 Update quickstart.md
Task: T031 README.md
```

## Validation Checklist
- [ ] All entities from data-model have creation tasks (Player, Enemy, Dungeon, Room, Corridor, Tile(+enums), TurnSystem, HUDController)
- [ ] All core systems from research decisions have tasks (Generator, LOS, Visibility, EnemyAI, Damage, Restart, Performance)
- [ ] All gameplay acceptance scenarios have test tasks (connectivity, LOS, fog transitions, damage cadence, enemy movement, restart)
- [ ] Tests precede implementation tasks
- [ ] Parallel [P] tasks touch distinct files
- [ ] No external API contracts → no contract test tasks needed (intentional)

## Notes
- Purposefully no networking / persistence tasks in MVP.
- Each test should use deterministic seed for reproducibility.
- Performance test may be skipped in CI if environment constrained (guard with assumption or system property if needed).
