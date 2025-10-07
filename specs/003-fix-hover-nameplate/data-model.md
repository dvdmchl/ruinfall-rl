# Data Model: Hover / Nameplate Fix

## Entities
### ViewportOffset
| Field | Type | Constraints | Description |
|-------|------|------------|-------------|
| x | double | any | Current horizontal camera translation in scene pixels |
| y | double | any | Current vertical camera translation in scene pixels |

### HoverState
| Field | Type | Constraints | Description |
|-------|------|------------|-------------|
| tileX | int | ≥ -1 | Tile X currently under cursor (or -1 none) |
| tileY | int | ≥ -1 | Tile Y currently under cursor (or -1 none) |
| hoveredObjectId | String? | null allowed | ID of interactive object currently targeted |
| stackIndex | int | ≥ 0 when hoveredObjectId != null | Position in stack for cycling |

### InteractiveObject (excerpt - existing)
| Field | Type | Constraints | Description |
|-------|------|------------|-------------|
| id | String | unique | Identifier |
| tileX | int | ≥ 0 | Current tile X |
| tileY | int | ≥ 0 | Current tile Y |
| summary | String | non-empty | For nameplate display |

## Derived / Utility
### Coordinate Mapping Function
Signature: `TileCoord mapCursorToTile(double sceneX, double sceneY, double offsetX, double offsetY, int tileSize)`
Return: record TileCoord(int tileX, int tileY)
Logic: floor((scene + offset)/tileSize); if <0 → -1 sentinel.

## State Transitions
1. Movement Refresh
```
IF (oldTile == hoveredTile) OR (newTile == hoveredTile) THEN recompute stack & hoveredObjectId.
```
2. Camera Offset Change
```
Recompute tile from stored lastMouseSceneX/Y; update tileX/tileY; update hoveredObjectId accordingly.
```
3. Object Leaves Hovered Tile
```
IF tile now empty → hoveredObjectId=null, stackIndex=0.
ELSE hoveredObjectId=topOfStackId, stackIndex=0 (reset cycle baseline).
```
4. Mouse Scroll Cycle
```
IF stack size >1 cycle (stackIndex = (stackIndex + dir + size) % size) update hoveredObjectId.
```

## Validation Rules
- If tileX == -1 OR tileY == -1 → hoveredObjectId MUST be null and stackIndex = 0.
- stackIndex < stackSize for that tile when hoveredObjectId != null.

## Invariants
- HoverState reflects either no tile (-1,-1) or a valid in-bounds tile.
- hoveredObjectId MUST belong to an InteractiveObject whose (tileX,tileY) == HoverState.tileX,tileY.

## Notes
- No persistence needed; transient runtime state.
- Determinism: All state changes originate from explicit method calls (mouse move, movement trigger, camera change, scroll cycle).

