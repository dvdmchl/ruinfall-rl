# Ruinfall Roguelike (Minimal Core)

Implements the minimal single-level roguelike core (feature 001-build-a-minimal) plus the
new interactive object system from feature 002:
- Procedural rooms and corridors (seeded) with player + roaming enemy
- LOS radius 8 with fog-of-war (UNSEEN / MEMORY / VISIBLE)
- Contact damage, HP 5, death overlay (`R` to restart)
- Interactive objects (enemies, chests, NPCs) with hover nameplates, selection highlight,
  and a side panel of localized details
- Context menus for selected objects; placeholder actions log attempts and surface toast
  feedback while Inspect remains enabled
- Middle-click stack menu to resolve overlapping objects and pick a new selection
- ESC prioritises closing context menus, then clearing selection, before exiting

## Controls
- Arrow / WASD: move the player (throttled for held input)
- Mouse move: hover objects to preview nameplates
- Left click: select visible object or clear selection when clicking empty tiles
- Right click: select (if needed) and open the context menu
- Middle click: open stack menu when multiple interactives occupy the tile
- Mouse wheel: cycle hovered stack without opening the menu
- ESC: close context menu, then clear selection, else exit

## ESC Key Behavior (Feature 005)
Precedence-driven, debounced (>=200ms) logic resolves exactly one action per press:
Order of consideration:
1. Transition active -> IGNORED (no state change)
2. Dialog open -> dismiss dialog
3. Any overlay(s) open -> close top overlay
4. Cutscene active -> interrupt & open pause menu
5. No menu open -> open pause menu (depth=1)
6. Menu depth >1 -> navigate back (depth-1)
7. Menu depth ==1 -> resume gameplay
8. Otherwise -> NO_OP (nothing to do)

Telemetry records each meaningful action (excluding IGNORED / NO_OP) when enabled.

## Telemetry
In-memory logger (temporary) captures EscActionType + timestamp.
Enable/disable via `TelemetryConfig.setEnabled(boolean)` in tests (default enabled).

## Build
```
mvn clean verify
```

## Run
```
mvn -DskipTests exec:java -Dexec.mainClass=org.dreamabout.sw.game.ruinfall.RuinfallApp -- -seed=12345
```
Use `--seed=` for deterministic generation.

## Performance Logging
Enable timing logs:
```
mvn -Druinfall.debugPerf=true -DskipTests exec:java -Dexec.mainClass=org.dreamabout.sw.game.ruinfall.RuinfallApp
```

## Tests
TDD suite covers generation connectivity, room counts, LOS, fog transitions, damage cadence,
enemy movement, restart flow, performance budgets, interaction contracts, and UI placement
edge cases.

## Recent Hover / Nameplate Improvements (Feature 003)
- Added HoverMath for deterministic cursor→tile mapping with viewport offsets.
- Hover auto-refreshes on object movement (enter/leave hovered tile) and camera scroll.
- Stale hover invalidates on removal or movement away (fallback to next stack object or clear).
- Performance guard ensures only active tile stack inspected (O(k)).
- Nameplate & highlight update without requiring extra mouse movement.

## License
See LICENSE.