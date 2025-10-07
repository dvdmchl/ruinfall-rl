# Contract: Coordinate Mapping

Function: `mapCursorToTile(sceneX, sceneY, offsetX, offsetY, tileSize)`
Returns: `(tileX, tileY)`

## Behavior
| Case | Inputs (sceneX, sceneY, offsetX, offsetY, tileSize) | Expected | Notes |
|------|-----------------------------------------------------|----------|-------|
| Origin | 0,0,0,0,32 | (0,0) | baseline |
| Boundary floor | 31.9,0,0,0,32 | (0,0) | still tile 0 |
| First switch | 32,0,0,0,32 | (1,0) | boundary crossing |
| Positive offset inside | 15,0,16,0,32 | (0,0) | 31/32 floor 0 |
| Positive offset crossing | 15,0,20,0,32 | (1,0) | 35/32 floor 1 |
| Negative offset outside | 5,0,-6,0,32 | (-1,-1?) | sentinel for outside map (treat <0 as -1) |
| Fractional safe | 10,0,0.9,0,32 | (0,0) | float stability |
| Fractional crossing | 10,0,31.2,0,32 | (1,0) | 41.2/32 floor 1 |

## Rules
1. Compute worldX = sceneX + offsetX; worldY = sceneY + offsetY.
2. tileX = floor(worldX / tileSize); tileY = floor(worldY / tileSize).
3. If tileX < 0 or tileY < 0 → return (-1,-1) sentinel.
4. (Future) Optionally clamp to map max bounds when map size known.

## Error Handling
- tileSize <= 0 → IllegalArgumentException.
- NaN inputs → IllegalArgumentException.

## Test Responsibilities
Unit test must assert each behavior row; include epsilon tolerance for floating edge reproduction not required due to floor semantics.

