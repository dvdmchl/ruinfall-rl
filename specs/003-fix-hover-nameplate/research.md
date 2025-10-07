- Risks: Negative coordinates (outside map) must yield -1 or be clamped externally. Current spec uses (-1,-1) sentinel.

### 2. Hover Refresh Triggers
- Movement of any InteractiveObject can affect hover only if oldTile == hoveredTile OR newTile == hoveredTile.
- Camera (viewport) offset change invalidates mapping even if cursor static.
- Decision: Provide explicit `recomputeHoverFromStoredCursor()` called when viewport offset mutates.

### 3. Stale State Invalidation
- When hovered object leaves tile and stack now empty: clear hover (tile remains but hoveredObjectId = null) OR set tile = -1,-1? Decision: retain tile coordinates (user may still be pointing at tile) but hoveredObjectId cleared; stackIndex reset to 0.
- If other interactives remain: pick deterministic top-of-stack (existing ordering logic) and set hoveredObjectId accordingly.

### 4. Determinism & Tests
- Avoid reliance on frame/render timing; tests will call mapping + refresh directly.
- Provide pure mapping method to enable focused unit test.

### 5. Performance Considerations
- Complexity: O(k) per affected tile (k = stack size). No global scan.
- Movement trigger checks constant-time comparison of coordinates before optional stack evaluation.

## Decisions Summary
| Area | Decision | Rationale | Alternatives | Status |
|------|----------|-----------|-------------|--------|
| Coordinate mapping | Central pure function | Testability, reuse | Inline math scattered | Accepted |
| Movement refresh | Conditional trigger on affected tile only | Performance | Global scan each frame | Accepted |
| Camera change | Recompute using stored cursor position | Correctness; cheap | Require user mouse move | Accepted |
| Stale invalidation | Clear or rebind depending on stack contents | Consistent feedback | Always clear | Accepted |
| Determinism | No RNG; pure method + explicit triggers | Stable tests | Poll-based sampling | Accepted |

## Open Questions
(None) – All clarified by override to proceed.

## Risk Assessment
| Risk | Impact | Likelihood | Mitigation |
|------|--------|-----------|------------|
| Subtle off-by-one with negative offsets | Incorrect selection near map edges | Low | Floor with prior bounds check; tests for negative offset case |
| Missed viewport change hook | Stale hover persists | Medium | Add integration test simulating camera recenter event |
| Performance regression if accidentally iterating all objects | Frame drops | Low | Code review + unit test limiting operations to stack |

## Test Inputs Matrix (Coordinate Mapping)
| sceneX | sceneY | offsetX | offsetY | TILE_SIZE | Expected tileX | Notes |
|--------|--------|---------|---------|-----------|----------------|-------|
| 0 | 0 | 0 | 0 | 32 | 0 | origin |
| 31.9 | 0 | 0 | 0 | 32 | 0 | boundary still tile 0 |
| 32.0 | 0 | 0 | 0 | 32 | 1 | first tile switch |
| 15.0 | 0 | 16 | 0 | 32 | 0 | offset pushes into same tile |
| 15.0 | 0 | 20 | 0 | 32 | 1 | crosses boundary after offset |
| 5.0 | 0 | -6 | 0 | 32 | -1 | outside (negative) sentinel |
| 5.0 | 0 | 30 | 0 | 32 | 1 | fractional addition crossing boundary |
| 10.0 | 0 | 0.9 | 0 | 32 | 0 | fractional offset stays floor 0 |
| 10.0 | 0 | 31.2 | 0 | 32 | 1 | 42.1 / 32 floor 1 |

## Conclusion
Research complete; all requirements have a concrete, testable approach. Proceed to design artifacts.
# Research: Fix stale hover / nameplate

**Feature**: 003-fix-hover-nameplate  
**Date**: 2025-10-07  
**Goal**: Ensure hover + nameplate always reflect visually hovered object under cursor after camera scrolling or object movement without requiring mouse motion.

## Topics Investigated
### 1. Coordinate Translation Accuracy
- Requirement: FR-001, FR-006
- Tile mapping must incorporate current viewport offset (may be fractional) before dividing by TILE_SIZE.
- Chosen Formula: `tileX = floor( (sceneX + viewportOffsetX) / TILE_SIZE )`; similarly for Y.
- Justification: Stable across fractional offsets, insensitive to small float noise (<1px) if flooring used.
- Edge Validation: sceneX near boundary (TILE_SIZE-0.1) with offset 0.9 still produces correct tile.

