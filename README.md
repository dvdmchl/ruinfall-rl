# Ruinfall Roguelike (Minimal Core)

Implements the minimal single-level roguelike core (feature 001-build-a-minimal):
- Procedural rooms + corridors (seeded)
- One player, one roaming enemy
- LOS radius 8 with fog-of-war (UNSEEN/MEMORY/VISIBLE)
- Contact damage, HP 5 → death overlay (restart with R)

## Build
```
mvn clean verify
```

## Run
```
mvn -DskipTests exec:java -Dexec.mainClass=org.dreamabout.sw.game.ruinfall.RuinfallApp -- -seed=12345
```
(Use `--seed=` for deterministic generation.)

## Performance Logging
Enable timing logs:
```
mvn -Druinfall.debugPerf=true -DskipTests exec:java -Dexec.mainClass=org.dreamabout.sw.game.ruinfall.RuinfallApp
```

## Tests
TDD suite covers generation connectivity, room counts, LOS, fog transitions, damage cadence, enemy movement, restart flow, and performance budgets.

## License
See LICENSE.

