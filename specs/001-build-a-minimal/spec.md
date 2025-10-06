# Feature Specification: Minimal Single-Level Roguelike Core (Ruinfall)

**Feature Branch**: `001-build-a-minimal`  
**Created**: 2025-10-06  
**Status**: Draft  
**Input**: User description: "Build a minimal 2D top-down roguelike called Ruinfall. The player explores a single procedurally generated dungeon level with rooms and corridors, moves tile by tile, sees only what’s in line-of-sight (with fog-of-war), encounters one wandering enemy that hurts on contact, and has a small HUD with HP. Keep it simple and focused on exploration feeling."

---

## User Scenarios & Testing *(mandatory)*

### Primary User Story
A player launches Ruinfall and is placed inside a freshly generated dungeon level consisting of rooms connected by corridors. They navigate one tile at a time using directional input, revealing only tiles within line‑of‑sight while previously seen areas remain dimmed (fog-of-war). They encounter a single roaming enemy; on contact the player loses health until their HP reaches zero, ending the run. The player’s current HP (and max) is always visible on a minimal HUD so they can gauge risk while exploring.

### Acceptance Scenarios
1. **Given** the game has started and the dungeon is generated, **When** the player moves one step in any valid cardinal direction, **Then** their position updates by exactly one traversable tile and newly visible tiles within line-of-sight are revealed while out-of-sight explored tiles remain fogged (memory dim state).
2. **Given** the player and the enemy occupy adjacent tiles, **When** the enemy moves into the player or the player moves into the enemy, **Then** the player’s HP decreases by 1 (contact damage) and the HUD reflects the new HP immediately (e.g., 4/5).
3. **Given** the player has >1 HP, **When** repeated contact damage reduces HP to exactly 0, **Then** exploration ends, all entities freeze, and an end-of-run overlay appears centered with text: "You have fallen. Press R to restart." No further movement occurs until restart or exit.
4. **Given** a newly generated dungeon, **When** the player explores all reachable rooms, **Then** no isolated (unreachable) rooms exist (connectivity guaranteed).
5. **Given** the player is standing still, **When** the enemy takes its autonomous turn, **Then** the enemy never moves through walls and remains within traversable floor tiles.
6. **Given** the player overlaps the enemy across multiple consecutive turns, **When** no separating movement occurs, **Then** damage is still applied only once per turn (not multiple times within the same turn cycle).
7. **Given** the player starts the run, **When** they press R after death, **Then** the game restarts with a newly generated dungeon (new random seed unless a seed was explicitly provided initially).

### Edge Cases
- Dungeon generation produces minimal geometry (e.g., only 1 room) — must still allow valid spawn of player and enemy.
- Player spawns adjacent to enemy — first move or enemy move still applies standard contact damage rules (no grace turn).
- Enemy attempts to move into wall corner or dead-end — may remain stationary that turn; will attempt another random valid direction on subsequent turns.
- Line-of-sight at map boundary — tiles outside bounds are never revealed; no errors.
- Player at 1 HP collides with enemy twice in rapid succession (input buffering) — HP must not drop below 0; end state triggered exactly once.
- All rooms discovered — fog-of-war persists for currently unseen (out-of-LOS) tiles (memory dim) rather than hiding them completely.
- Continuous overlap — damage limited to once per turn cycle (player action + enemy action), preventing double-application inside a single turn.
- Simultaneous movement intent to same tile — resolution order (player then enemy) ensures at most one damage application that turn.

---

## Clarifications *(resolved decisions)*
| Topic | Decision / Value | Rationale |
|-------|------------------|-----------|
| End-of-run wording & display | Overlay text: "You have fallen. Press R to restart." + scene dim (~60%) | Clear feedback & immediate restart hint |
| Enemy idle in dead-ends | Allowed to stay still for that turn | Simplest; avoids pathfinding complexity |
| Fog-of-war memory | Previously seen tiles shown dim (memory) | Common roguelike convention; aids navigation |
| Damage cadence | Max once per turn (player move + enemy move sequence) | Prevents rapid multi-hit ambiguity |
| Contact damage value | 1 HP | Keeps HP meaningful with small starting pool |
| Restart option | Press R after death; ESC quits | Immediate replay loop |
| Enemy movement model | Turn-based: one attempt after each player move | Simple deterministic pacing |
| LOS radius & algorithm | Radius 8 (circular) Bresenham / raycast blocked by walls | Supports exploration feeling, manageable visibility |
| Fog-of-war states | Unseen (black), Memory (dim gray), Visible (full) | Clear 3-state model |
| Starting / Max HP | 5 / 5 | Small number supports tension |
| Controls | Arrow keys only (WASD excluded in MVP) | Consistency & simplicity |
| Damage feedback | Player tint red 150ms + floating "-1 HP" text; no audio MVP | Minimal but noticeable feedback |
| Seeding | Optional `--seed=<int>` to reproduce; else random logged | Determinism when desired |
| HUD format | "HP: current/max" (e.g., HP: 4/5) | Clarity |
| Entity freeze on death | All movement halts | Prevents background actions post-run |
| OOB LOS handling | Ignore; never reveal out-of-bounds tiles | Safety & avoids artifacts |
| Simultaneous tile contest | Player resolves first; enemy then; single damage max per turn | Deterministic & matches cadence |

---

## Requirements *(mandatory)*

### Functional Requirements
- **FR-001**: System MUST generate exactly one dungeon level at game start consisting of at least one room and optional connecting corridors.
- **FR-002**: Generated dungeon MUST ensure that the player’s spawn tile and the enemy’s spawn tile are mutually reachable via traversable tiles (no isolation).
- **FR-003**: System MUST place exactly one enemy entity in the dungeon after generation.
- **FR-004**: Player MUST move strictly one tile per input in one of the four cardinal directions if the target tile is traversable.
- **FR-005**: System MUST prevent movement into walls (movement input toward a wall results in no positional change).
- **FR-006**: System MUST maintain and display the player’s current and maximum HP on a HUD at all times during exploration.
- **FR-007**: System MUST decrease player HP by exactly 1 when the player and enemy occupy the same tile or attempt to move into each other, applying damage at most once per turn cycle.
- **FR-008**: System MUST end the run when player HP reaches 0, locking further movement, freezing entities, and presenting end-of-run overlay text: "You have fallen. Press R to restart.".
- **FR-009**: Enemy MUST autonomously attempt one movement action after each player movement (turn-based), choosing only traversable tiles; if no valid move, it may remain stationary.
- **FR-010**: Enemy MUST NOT pass through walls or outside dungeon bounds.
- **FR-011**: System MUST compute line-of-sight with radius 8 (blocked by walls) each time the player changes position and update visible tiles accordingly.
- **FR-012**: System MUST implement fog-of-war with three states: Unseen (never seen), Memory (seen but currently out of LOS, dim), Visible (currently in LOS); Memory tiles must never revert to Unseen.
- **FR-013**: System MUST ensure Unseen tiles visually convey lack of knowledge (fully obscured / black).
- **FR-014**: System MUST initialize player HP to 5 (current and maximum) at run start.
- **FR-015**: System MUST prevent player HP from dropping below 0 (lower bound clamp) and MUST NOT display negative values.
- **FR-016**: System MUST ensure at least one viable movement option exists from the spawn tile (not fully enclosed by walls).
- **FR-017**: System MUST reveal only tiles within LOS immediately after each movement (no delayed reveal frames).
- **FR-018**: System MUST use Arrow Keys as the sole movement control scheme in the MVP.
- **FR-019**: System MUST provide visual feedback upon taking damage (red tint 150ms + floating "-1 HP" text) with no audio requirement in MVP.
- **FR-020**: System MUST guarantee deterministic reproduction of a dungeon when launched with `--seed=<int>` and MUST log the seed used (random or provided) at start.
- **FR-021**: System MUST allow restart by pressing R after the run ends; this restarts the level (new random seed if none specified originally, same seed if provided) and resets state.
- **FR-022**: System MUST allow exit via ESC at any time (during run or on end screen).

### Key Entities
- **Player**: position (tile), current HP, max HP, visibility origin.
- **Dungeon**: dimensions, collection of tiles/rooms/corridors, seed.
- **Room**: bounds (rect), connected corridor references.
- **Corridor**: ordered list of tile coordinates.
- **Tile**: coordinates, type (wall/floor), visibility state (unseen/memory/visible).
- **Enemy**: position, movement state, contact damage (1), last damage turn marker.
- **FogOfWar / Visibility Model**: tracks per-tile visibility state based on LOS radius and wall blocking.
- **HUD**: displays HP current/max and end-of-run overlay when applicable.

---

## Review & Acceptance Checklist
*GATE: Automated checks run during main() execution*

### Content Quality
- [x] No implementation details (languages, frameworks, APIs) beyond clarified numeric / textual constants
- [x] Focused on user value and gameplay experience
- [x] Written for non-technical stakeholders
- [x] All mandatory sections completed

### Requirement Completeness
- [x] No [NEEDS CLARIFICATION] markers remain
- [x] Requirements are testable and unambiguous
- [x] Success criteria are measurable
- [x] Scope is clearly bounded
- [x] Dependencies and assumptions identified (dungeon generation algorithm, LOS algorithm, restart loop)

---

## Execution Status
*Updated by main() during processing*

- [x] User description parsed
- [x] Key concepts extracted
- [x] Ambiguities marked
- [x] User scenarios defined
- [x] Requirements generated
- [x] Entities identified
- [x] Clarifications resolved
- [ ] Review checklist passed (pending automation)

---
