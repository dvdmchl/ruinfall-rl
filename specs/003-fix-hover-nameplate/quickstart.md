# Quickstart: Hover / Nameplate Fix

## Goal
Validate correct hover behavior under movement and camera scrolling.

## Prerequisites
- Java 21 installed
- Maven wrapper or local Maven

## Commands
```
mvn -q -DskipTests package
mvn -q test -Dtest=HoverCoordinateMappingTest
mvn -q test -Dtest=HoverMovementRefreshIntegrationTest
```

## Planned New Tests
1. HoverCoordinateMappingTest (unit)
   - Verifies mapping for matrix in research.md (origin, boundary, fractional, negative offset sentinel, crossing boundary).
2. HoverMovementRefreshIntegrationTest (integration)
   - Simulates: cursor over stationary tile; enemy moves onto tile → hover updates without mouse move.
   - Enemy leaves tile → hover clears.
3. HoverViewportScrollIntegrationTest (integration)
   - Cursor stationary; camera offset changes; object under new mapping becomes hovered.
4. HoverStaleInvalidationTest (integration)
   - Hovered object removed; stack fallback or clear occurs.
5. NameplatePlacementTest (integration)
   - After scroll and movement, nameplate anchored to current hovered object's tile.

## Manual Smoke (Optional)
1. Run game; hover an enemy; move player causing camera recenter; ensure nameplate follows correct enemy without moving mouse.
2. Observe enemy walking away; nameplate disappears.

## Troubleshooting
- Off-by-one: Log computed tile indices; compare with expected based on TILE_SIZE constant.
- No update on camera move: Ensure viewport change hook calls recompute method.

## Next Steps
After confirming tests fail (red) implement mapping + refresh logic then rerun full test suite.

