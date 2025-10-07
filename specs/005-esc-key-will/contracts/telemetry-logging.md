# Contract: Telemetry Logging (ESC Actions)

## Purpose
Capture categorized ESC actions for UX analysis while toggle-enabled.

## Action Types
OpenMenu, NavigateBack, DismissDialog, ResumeGameplay, CloseOverlay, InterruptCutscene.

## Configuration
- TelemetryConfig.enabled (boolean, default true). Set via configuration flag at startup.
- No runtime toggle in MVP.

## API (Conceptual)
| Method | Description |
|--------|-------------|
| record(actionType, timestampMillis) | Append action record if enabled |
| getRecords() | Snapshot list (for tests) |
| clear() | Clear all records |

## Behavior Rules
- Do not record ignored presses (debounce or transition ignore).
- Record only after successful state mutation.
- Guarantee insertion order chronological.

## Data Integrity
- Records stored in-memory; not persisted.
- timestampMillis monotonic relative order assumed.

## Tests (Planned)
- Logging disabled: no entries
- Each action type recorded once per trigger
- Ignored presses not recorded
- Ordering preserved for rapid valid presses (>200 ms apart)

