# Data Model: Ruinfall Minimal Core

Spec: `specs/001-build-a-minimal/spec.md`

## Entities
### Player
| Field | Type | Notes |
|-------|------|-------|
| x | int | Tile coordinate X |
| y | int | Tile coordinate Y |
| hp | int | Current HP (>=0) |
| maxHp | int | Constant 5 for MVP |
| lastDamageTurn | int | Turn id when last damage applied |

### Enemy
| Field | Type | Notes |
|-------|------|-------|
| x | int | Tile coordinate X |
| y | int | Tile coordinate Y |
| lastDamageTurn | int | Parallel to Player gate |

### Dungeon
| Field | Type | Notes |
|-------|------|-------|
| width | int | Map width in tiles |
| height | int | Map height in tiles |
| tiles | Tile[][] | 2D grid |
| rooms | List<Room> | Placed rooms |
| seed | long | RNG seed used |

### Room
| Field | Type | Notes |
|-------|------|-------|
| x | int | Top-left X |
| y | int | Top-left Y |
| width | int | Width in tiles |
| height | int | Height in tiles |
| centerX | int | Cached center X |
| centerY | int | Cached center Y |

### Corridor
| Field | Type | Notes |
|-------|------|-------|
| points | List<Point> | Ordered floor tiles carved |

### Tile
| Field | Type | Notes |
|-------|------|-------|
| type | TileType | WALL or FLOOR |
| visibility | VisibilityState | UNSEEN / MEMORY / VISIBLE |

### VisibilityState (enum)
Values: UNSEEN, MEMORY, VISIBLE

### TurnSystem
| Field | Type | Notes |
|-------|------|-------|
| currentTurn | int | Increment each player action |
| runEnded | boolean | True when player HP == 0 |

### HUD
| Field | Type | Notes |
|-------|------|-------|
| hpLabel | UI Node | Displays HP: current/max |
| overlayNode | UI Node | Death overlay text (hidden until death) |

## Relationships
- Player & Enemy positions refer to Dungeon.tiles indices.
- Visibility recalculated from Player (x,y) each turn.
- TurnSystem coordinates sequencing for damage gating.

## State Transitions
### Visibility
UNSEEN -> VISIBLE (first time in LOS)  
UNSEEN -> MEMORY (not direct; occurs via VISIBLE then out-of-LOS)  
VISIBLE -> MEMORY (on LOS recompute when not re-hit)  
MEMORY -> VISIBLE (when LOS re-exposes)  
No reverse to UNSEEN.

### HP & Run State
- hp decreases by 1 on contact when lastDamageTurn < currentTurn
- If hp reaches 0: set runEnded=true, freeze movement

## Validation Rules
- hp clamped [0, maxHp]
- Movement only onto FLOOR tiles within bounds
- Enemy move only cardinal into FLOOR or stay
- LOS radius ≤ 8 Euclidean (distance check before ray)

## Derived/Computed
- Room center: centerX = x + width/2 (int division); centerY likewise
- Distance for LOS candidate: sqrt(dx*dx+dy*dy) ≤ 8 (squared compare <= 64)

## Notes
Model intentionally simple (no inheritance). Future expansions may introduce systems (Inventory, Combat) separated cleanly.

