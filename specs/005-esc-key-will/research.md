# Research: ESC Key Menu Navigation Behavior

## Focus Areas
1. Debounce Strategy
2. Precedence Enforcement
3. Pause Semantics Across Layers
4. Telemetry Toggle & Action Classification
5. Transition (Screen Fade) Guard

## Decisions
### Debounce Strategy
- Decision: Use monotonic time (System.nanoTime converted to ms) with >=200 ms threshold between accepted ESC actions.
- Rationale: Simple, deterministic, avoids wall-clock skew.
- Alternatives: Timer task scheduling (overhead), event queue buffering (unnecessary complexity).

### Precedence Enforcement
- Decision: Single resolver method returns first actionable layer in order: Dialog > Overlay > Cutscene > (Normal handling) while Transition ignores ESC unless Dialog/Overlay present.
- Rationale: Deterministic, testable, ensures only one action per discrete press.
- Alternatives: Chain-of-responsibility objects (overkill now), priority queue (dynamic cost not justified).

### Pause Semantics
- Decision: Maintain integer pausingLayerCount = (#open Overlays with pausesWorld=true) + (1 if Pause Menu open). World considered paused when count > 0.
- Rationale: Simple O(1) check; future non-pausing overlays easily supported.
- Alternatives: Boolean flags scattered across services (risk inconsistency), reference counting by type (redundant).

### Telemetry Toggle
- Decision: Global TelemetryConfig.enabled (default true, from config flag). Only record after action executed (not ignored presses).
- Rationale: Clean analytics; no noise from ignored transitions.
- Alternatives: Record all inputs (inflated dataset), runtime toggle (out of initial scope).

### Transition Guard
- Decision: If transitionActive == true and no Dialog or Overlay present, ESC is ignored (no queue, no telemetry).
- Rationale: Prevents partial UI state & flicker.
- Alternatives: Queue action until after transition (adds complexity & potential surprises).

## Data Impact
All state in-memory; no persistence; telemetry ephemeral list cleared on session end.

## Performance Considerations
- Resolver O(1) (checks booleans / top-of-stack only)
- Overlay close/open O(1)
- Debounce check O(1)
- No per-frame polling introduced; all triggered by input or state changes.

## Testing Strategy Mapping
| Concern | Test Type | Representative Tests |
|---------|-----------|----------------------|
| Debounce 200 ms | Unit | ESCDebounceTest (single accept vs rapid repeats) |
| Precedence | Unit + Integration | ESCPrecedenceTest, ESCDialogDismissTest, ESCOverlayPauseResumeTest |
| Pause counting | Unit | ESCOverlayPauseResumeTest (open/close overlays + menu) |
| Cutscene interrupt | Integration | ESCCutsceneInterruptTest |
| Transition ignore | Integration | ESCTransitionIgnoreTest |
| Back navigation | Integration | ESCPauseMenuNavigationTest |
| Telemetry categories | Unit | ESCTelemetryLoggingTest |

## Alternatives Considered (Summary)
- Event bus for ESC events: Deferred (complexity not warranted yet).
- Global GameState enum only: Insufficient for stacked overlays & dialogs.
- Queued ESC actions during transitions: Added complexity without user value.

## Open Issues
None (all clarifications resolved in spec).

## Ready Status
All research questions answered. Proceed to design and contracts (completed in Phase 1).

