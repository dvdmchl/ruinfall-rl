# Quickstart: Ruinfall Minimal Core

## Prerequisites
- Java 21 (JDK) installed and on PATH
- Maven 3.9+

## Build
```
mvn clean verify
```

## Run
```
mvn -DskipTests exec:java -Dexec.mainClass=org.dreamabout.sw.game.ruinfall.RuinfallApp
```
Optional deterministic seed:
```
mvn -DskipTests exec:java -Dexec.mainClass=org.dreamabout.sw.game.ruinfall.RuinfallApp -Dexec.args="--seed=12345"
```

## Debug / Performance
Set system property to enable perf logs:
```
java -Druinfall.debugPerf=true -jar target/ruinfall-rl-0.0.1-SNAPSHOT.jar
```
(Or with Maven exec: `mvn -Druinfall.debugPerf=true -DskipTests exec:java ...`)

Restart (R) uses a new seed each time unless `--seed=` provided initially.

## Controls (MVP)
- Arrow Keys: Move (one tile per press)
- ESC: Exit
- After death: R to restart

## Expected Loop
1. Map appears (rooms & corridors, fog everywhere else)
2. Move to explore; tiles reveal within radius 8
3. Encounter roaming enemy; contact causes -1 HP (visual feedback)
4. At 0 HP overlay appears; press R to regenerate a new dungeon

## Development Notes
- Core logic will be test-driven (generation, LOS, damage cadence)
- Avoid launching full FXGL window in unit tests; isolate logic into plain classes
- Use provided seed logging to reproduce issues

## Troubleshooting
| Issue | Cause | Resolution |
|-------|-------|------------|
| Window fails to open (no graphics) | Headless environment | Run locally with display or configure headless test stubs |
| Non-deterministic map with seed | Seed not parsed | Check `--seed=` prefix & console log |
| Slow generation | Excessive room attempts | Reduce max rooms or attempt cap constant |

## Next Steps
Run `/tasks` planning (Phase 2) to generate ordered TDD task list.
