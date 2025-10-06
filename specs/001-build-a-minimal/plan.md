# Implementation Plan: Minimal Single-Level Roguelike Core (Ruinfall)

**Branch**: `001-build-a-minimal` | **Date**: 2025-10-06 | **Spec**: `specs/001-build-a-minimal/spec.md`
**Input**: Feature specification from `/specs/001-build-a-minimal/spec.md`

## Execution Flow (/plan command scope)
```
1. Load feature spec from Input path
2. Fill Technical Context (scan for NEEDS CLARIFICATION) – none remain
3. Fill the Constitution Check section based on constitution document
4. Evaluate Constitution Check (no violations) → mark Initial Constitution Check PASS
5. Execute Phase 0 → research.md (decisions & rationale)
6. Execute Phase 1 → data-model.md, quickstart.md, contracts/ (N/A endpoints note), update agent file
7. Re-evaluate Constitution Check (still PASS) → mark Post-Design Constitution Check PASS
8. Plan Phase 2 approach (describe only, DO NOT create tasks.md)
9. STOP - Ready for /tasks command
```

**IMPORTANT**: /plan stops before generating tasks.md (Phase 2 execution) or writing implementation code beyond minimal scaffolding.

## Summary
Implement the minimal core of Ruinfall: a single procedurally generated dungeon level (rooms + corridors) with one player and one roaming enemy in a turn-based, tile-by-tile loop. Provide LOS (radius 8) with fog-of-war (unseen / memory / visible), contact damage reducing player HP from 5 to 0 ending the run, restart via R, exit via ESC. Technical approach: lightweight rectangle room placement with corridor carving ensuring full connectivity; Bresenham-style ray LOS with wall blocking; simple random-walk enemy movement constrained to traversable tiles; per-turn damage gating via a "last damage turn" marker; seeded RNG for reproducibility.

## Technical Context
**Language/Version**: Java 21 (Maven)  
**Primary Dependencies**: FXGL 21.1 (includes JavaFX runtime), JUnit 5  
**Storage**: N/A (in‑memory runtime structures only)  
**Testing**: JUnit 5 (unit + future integration harness)  
**Target Platform**: Desktop (Windows, macOS, Linux)  
**Project Type**: single  
**Performance Goals**: Steady 60 FPS; generation < 50 ms typical map; LOS recompute per move < 2 ms  
**Constraints**: Deterministic reproducibility when seed supplied; no blocking operations on FXGL game loop thread beyond trivial computations  
**Scale/Scope**: Single level, exactly 1 enemy, <= ~120x120 tiles (initial target 64x48)  

## Constitution Check
*Initial Gate Assessment*
- Maintainable code quality: Plan favors composition (separate generator, visibility, turn system) – PASS
- Test-first reliability: Plan schedules tasks.md (Phase 2) before core implementation; minimal scaffolding only now – PASS
- Cohesive player experience: Controls, HP HUD, damage feedback, restart flow defined in spec – PASS
- Delivery constraints: FXGL chosen (compliant), no secrets, simple assets later – PASS
- Governance: No deviations; no extra complexity layers (no ECS abstraction beyond FXGL built-ins yet) – PASS

No violations → Complexity Tracking remains empty.

## Project Structure

### Documentation (this feature)
```
specs/001-build-a-minimal/
├── plan.md
├── research.md          # Phase 0
├── data-model.md        # Phase 1
├── quickstart.md        # Phase 1
├── contracts/           # Phase 1 (note: no external API endpoints)
└── spec.md
```

### Source Code (repository root)
```
# Single Maven project (standard layout)
.
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/dreamabout/sw/game/ruinfall/
│   │   │       └── RuinfallApp.java          # FXGL Application stub (initial scaffold only)
│   │   └── resources/                        # (future assets: textures, fonts)
│   └── test/
│       └── java/
│           └── org/dreamabout/sw/game/ruinfall/
│               └── PlaceholderTest.java      # Ensures JUnit configured
└── target/                                   # Maven build output (ignored in VCS)
```
**Structure Decision**: Adopt standard Maven single-project layout aligning with Java + FXGL conventions for clarity and tooling support.

## Phase 0: Outline & Research (Completed)
Produced `research.md` capturing decisions:
- Dungeon generation algorithm (room placement + corridor linking, connectivity guarantee)
- LOS & fog-of-war approach (Bresenham rays, tile state machine)
- Turn sequencing & damage gating
- RNG seeding strategy
- Performance considerations & data representations
All unknowns resolved; no NEEDS CLARIFICATION remain.

## Phase 1: Design & Contracts (Completed)
Artifacts produced:
- `data-model.md`: Entities (Player, Enemy, Dungeon, Room, Corridor, Tile, VisibilityState, TurnSystem, HUD) with fields & relationships
- `quickstart.md`: Setup, build, run, seed usage, restart/exit instructions
- `contracts/` NOTE: No external network/API endpoints in this feature; internal service contracts implicit via data-model. Directory retained for consistency; contains README.
- Agent context updated (copilot) via script.

## Phase 2: Task Planning Approach (Ready /tasks)
**Task Generation Strategy**:
- Derive tasks from entities (model classes), systems (generation, visibility, turn/damage, HUD), and acceptance scenarios → integration tests
- Pre-implementation test tasks: generation connectivity, LOS blocking & radius boundary, fog state transitions, damage cadence, restart behavior
- Implementation tasks follow TDD order; each test failing before implementation
- Parallelization: Independent model classes & pure utility tests flagged [P]
**Ordering Strategy**:
1. Setup (ensure Maven, base packages) – already partially done
2. Tests for generation, LOS, fog, turn/damage, restart
3. Core implementation: generation, visibility, player movement, enemy AI, damage system, HUD, overlay
4. Polish: performance guard test (timing), minor refactors, documentation updates
Estimated tasks: ~26–30 items.

## Phase 3+: Future Implementation
(Out of scope for /plan; will execute after /tasks produces tasks.md.)

## Complexity Tracking
| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|--------------------------------------|
| (none)    |            |                                      |

## Progress Tracking
**Phase Status**:
- [x] Phase 0: Research complete (/plan command)
- [x] Phase 1: Design complete (/plan command)
- [x] Phase 2: Task planning complete (/plan command - describe approach only)
- [x] Phase 3: Tasks generated (/tasks command)
- [ ] Phase 4: Implementation complete
- [ ] Phase 5: Validation passed

**Gate Status**:
- [x] Initial Constitution Check: PASS
- [x] Post-Design Constitution Check: PASS
- [x] All NEEDS CLARIFICATION resolved
- [x] Complexity deviations documented (N/A)

---
*Based on Constitution v1.0.0 - See `/specify/memory/constitution.md`*
