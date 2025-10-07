# Quickstart: ESC Key Navigation Feature

## Prerequisites
- Java 21 installed
- Maven available in PATH

## Build & Test
```bash
mvn -q test
```
(Windows CMD) :
```cmd
mvn -q test
```

## Targeted Test Execution (examples)
```bash
mvn -q -Dtest=ESCDebounceTest test
mvn -q -Dtest=ESCPrecedenceTest test
```

## Manual Validation Checklist
| Step | Action | Expected |
|------|--------|----------|
| 1 | Start game (gameplay) press ESC once | Pause Menu opens, world pauses |
| 2 | Press ESC rapidly 5x (<1s) | Only initial open (no flicker) |
| 3 | Open Settings → Audio (submenu), press ESC | Returns to Settings root |
| 4 | From root submenu path resolved, press ESC | Close Pause Menu, world resumes |
| 5 | Open inventory overlay (no menu), verify world paused | World progression halted |
| 6 | Open map overlay, press ESC | Map closes, inventory remains, still paused |
| 7 | Press ESC again | Inventory closes, gameplay resumes |
| 8 | Trigger cutscene then ESC | Cutscene interrupted, Pause Menu shown |
| 9 | During screen transition press ESC | Ignored (no partial UI) |
| 10 | Open dialog over submenu then ESC | Dialog dismissed only |
| 11 | With telemetry enabled perform each action | All action types logged exactly once per press |
| 12 | Telemetry enabled: perform dialog->overlay->cutscene->open/back/resume sequence | Exactly 6 records (all action types except IGNORED/NO_OP) |

## Telemetry Inspection (if exposed)
- Check in-memory log (temporary debugging utility) prints categorized actions.

## Telemetry Programmatic Check
```java
TelemetryLogger logger = new TelemetryLogger(TelemetryConfig::isEnabled);
// after exercising handler: logger.getRecords()
```

## Troubleshooting
| Symptom | Possible Cause | Fix |
|---------|----------------|-----|
| Multiple actions per press | Debounce not enforced | Verify timestamp logic (>=200 ms) |
| Menu reopens instantly after resume | Key repeat not filtered | Confirm discrete press detection (key-down vs repeat) |
| Overlays not pausing world | pausesWorld flag not honored | Inspect OverlayStackService pause count logic |
| Telemetry empty | Config disabled | Ensure TelemetryConfig.enabled = true |

## Next
Proceed to /tasks once all planned tests are added and failing (before implementation).
