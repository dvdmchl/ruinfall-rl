# Research & Decisions: Minimal Single-Level Roguelike Core (Ruinfall)

Date: 2025-10-06  
Spec: `specs/001-build-a-minimal/spec.md`

## Overview
All unknowns in the feature spec have been resolved. This document records design decisions, rationale, and considered alternatives to support maintainability and reproducibility.

## Decisions
### 1. Dungeon Generation Algorithm
- Decision: Rectangular room placement with random size within bounds (e.g., 5x4 to 12x10), non-overlapping; iterative attempt placement until target room count or attempt cap. Corridors: connect rooms in order of sorted X (or via MST on room centers) with straight orthogonal L-shaped tunnels. Final pass converts carved floor tiles, ensures start and enemy spawn are reachable.
- Rationale: Simple, deterministic with seed, guarantees connectivity easily; performance is trivial for small maps.
- Alternatives Considered:
  - BSP subdivision: Overkill for single-level MVP.
  - Cellular automata caves: Harder to guarantee explicit rooms feel.
  - Drunkard walk: Less structured; harder to ensure distinct rooms.

### 2. Map Dimensions
- Decision: Default 64x48 tiles (configurable constant); maximum considered 120x120.
- Rationale: Fits typical 16:9 window (e.g., 1280x720) with 16px tiles while leaving UI space.
- Alternatives: Larger maps increase generation & LOS cost marginally; not needed early.

### 3. Tile Representation
- Decision: 2D array of Tile objects (enum type + visibility state byte). Visibility state stored as tri-state enum (UNSEEN, MEMORY, VISIBLE).
- Rationale: Direct indexing is fastest for LOS and movement; small memory footprint.
- Alternatives: Hash map (slower, unnecessary); flat 1D array (micro-optimization not required yet).

### 4. Line of Sight (LOS)
- Decision: Bresenham ray casting from player to each perimeter cell within radius 8; stop when a wall is encountered; mark traversed tiles visible; previously visible but now not hit become MEMORY.
- Rationale: Simple to implement, fast for small radius (O(r^2) rays ~ 201 rays). Clear blocking logic.
- Alternatives: Shadow casting (more efficient for large radius, not needed). Field-of-view recursive algorithms (added complexity).

### 5. Fog-of-War State Machine
- Decision: On each player move: set all VISIBLE → MEMORY; perform LOS rays: for each hit tile set to VISIBLE; UNSEEN only transitions forward (UNSEEN→VISIBLE or MEMORY; never revert).
- Rationale: Straightforward, guarantees invariants.
- Alternatives: Maintain per-turn visibility list (requires extra structure) – not needed now.

### 6. Enemy Movement AI
- Decision: Random-walk attempt among cardinal directions each turn (after player move) selecting a random valid traversable neighboring tile (no pathfinding). If none valid (surrounded), enemy stays.
- Rationale: Minimal complexity satisfying wandering behavior.
- Alternatives: A* or chase heuristic adds complexity not required in MVP.

### 7. Turn & Damage Sequencing
- Decision: Turn cycle defined as (Player action/input) → (Enemy action) → (Damage resolution at any contact event applied immediately but only once per cycle). Use a turn counter; enemy & player damage store lastAppliedTurn to prevent double hits when overlap persists.
- Rationale: Enforces FR-007 cadence requirement simply.
- Alternatives: Event queue or ECS system; unnecessary overhead now.

### 8. RNG & Seeding
- Decision: Single java.util.Random instance created at startup. If `--seed=<long>` cli arg present parse and use; else generate system random seed (Long) and log to console. Pass this RNG to generator and any random decisions.
- Rationale: Deterministic reproduction and traceability.
- Alternatives: Multiple RNG streams (splittable); not necessary for minimal feature.

### 9. Player & Enemy Spawn
- Decision: Player spawn at center of first room placed; enemy spawn at random floor tile that is not the player tile (retry until success). Ensure path exists (always true with generation design).
- Rationale: Predictable starting location while preserving variation in enemy location.
- Alternatives: Both random; risk of immediate adjacency more often (still allowed, but we keep player more stable early for debugging).

### 10. Performance Safeguards
- Decision: Soft budget: generation < 50 ms, LOS recompute < 2 ms. Add optional debug logging with elapsed times when a system property `ruinfall.debugPerf=true` is set.
- Rationale: Keep frame pacing stable and detect regressions.
- Alternatives: No measurement; would obscure early performance issues.

### 11. Rendering & Tile Size
- Decision: Tile size 16px (constant). Player & enemy simple colored rectangles or placeholder textures later.
- Rationale: Standard retro scale; matches typical FOV radius.
- Alternatives: 32px increases required window size or reduces visible area.

### 12. Damage Feedback
- Decision: On damage: apply red tint via entity view color adjust + spawn floating text node "-1 HP" that fades & rises over 150ms.
- Rationale: Meets FR-019 minimalistic feedback.
- Alternatives: Particle effects or audio; deferred beyond MVP.

### 13. Restart Flow
- Decision: After HP hits 0, set game state flag RUN_ENDED, stop accepting movement input; show overlay text UI node; key R triggers full re-init (new seed unless original provided) by re-running init routines.
- Rationale: Clear separation of active vs ended state.
- Alternatives: Scene reload; more heavy-weight.

### 14. Testing Strategy (Pre-Implementation)
- Decision: Unit tests target deterministic generation connectivity, LOS blocking & radius boundary, fog transition invariants, damage once-per-turn, restart resets state.
- Rationale: Guard core invariants before layering complexity.
- Alternatives: Delay tests until after implementation risks regressions.

## Risk Register (Early)
| Risk | Impact | Mitigation |
|------|--------|------------|
| JavaFX platform init failures on headless CI | Build/test flaky | Keep tests not launching full FXGL stage; isolate pure logic in non-JavaFX classes |
| Performance regression with larger maps | Frame drops | Keep map size modest; add timing logs |
| Overlap damage double application bug | Incorrect difficulty | Turn counter gating + unit test |
| Visibility artifacts at edges | Player confusion | Bounds checks in LOS rays + tests |

## Open Items (Deferred)
- Audio feedback (post-MVP)
- Additional enemy types / pathfinding
- Inventory / items system
- Multi-level progression

## Conclusion
Current decisions satisfy all functional requirements while minimizing complexity and enabling TDD. No outstanding clarifications remain.

