# Contract: Hover Refresh & Invalidation

## Trigger Conditions
| Trigger | Condition | Expected Hover State Action |
|---------|-----------|-----------------------------|
| Mouse Move | Cursor tile changes | Recompute stack; set hoveredObjectId to top or null |
| Object Movement Onto Tile | object.newTile == hoveredTile (cursor static) | If no hoveredObjectId OR different object on same tile → adopt top-of-stack (possibly new object) |
| Object Movement Off Tile | object.oldTile == hoveredTile | If object was hovered: re-evaluate stack (fallback or clear) |
| Camera Scroll | viewportOffset changes | Recompute tile from stored lastMouseSceneX/Y; update hover & nameplate |
| Object Removal | removed.id == hoveredObjectId | Fallback to next stack object else clear hoveredObjectId |
| Scroll Wheel Cycle | delta != 0 and stack size >1 | Adjust stackIndex; update hoveredObjectId |

## State Update Algorithm (Pseudocode)
```
function recomputeHover(tileX, tileY):
  if tileX < 0 or tileY < 0:
     hover.tileX = hover.tileY = -1
     hover.hoveredObjectId = null
     hover.stackIndex = 0
     return
  stack = registry.getStackAt(tileX,tileY)
  if stack.empty:
     hover.hoveredObjectId = null
     hover.stackIndex = 0
  else:
     if hover.stackIndex >= stack.size: hover.stackIndex = 0
     hover.hoveredObjectId = stack[hover.stackIndex].id
```

## Determinism
- Order of objects in stack is stable (existing ordering rules preserved).
- No time-based decay; only explicit triggers mutate state.

## Performance Constraint
- Each trigger inspects at most one tile stack.

## Tests
Integration tests simulate movement + camera scroll without mouse move to assert correct hover updates.


