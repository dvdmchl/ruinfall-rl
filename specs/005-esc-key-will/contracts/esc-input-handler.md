# Contract: ESCInputHandler

## Purpose
Central entry point for processing a discrete ESC key press enforcing precedence, debounce, and telemetry.

## Operation Signature (conceptual)
```
EscActionTypeOrIgnored handleEscPress(long nowMillis)
```

## Inputs
- nowMillis (long): Current monotonic milliseconds for debounce check.

## Preconditions
- NavigationState, DialogState, OverlayStackService, TelemetryLogger injected and non-null.

## Algorithm (High-Level)
1. If (nowMillis - lastAccepted) < 200 → return Ignored (debounce) (no telemetry).
2. Determine precedence target:
   a. If dialog present → action = DismissDialog
   b. Else if overlay stack not empty → action = CloseOverlay
   c. Else if cutscene active → action = InterruptCutscene
   d. Else if transition active → return Ignored (no telemetry)
   e. Else if gameplay & no pause menu → action = OpenMenu
   f. Else if pause menu depth > 0 → action = NavigateBack
   g. Else if pause menu depth == 0 & enteredFromGameplay → action = ResumeGameplay
   h. Else → return Ignored
3. Execute state mutation for chosen action.
4. Update lastAcceptedEpochMillis = nowMillis.
5. If telemetry enabled → record(action, nowMillis).
6. Return action.

## Return Values
Enum EscActionType: OpenMenu, NavigateBack, DismissDialog, ResumeGameplay, CloseOverlay, InterruptCutscene, Ignored.

## Postconditions
- At most one state mutation executed.
- Debounce timestamp updated only on executed action.
- Telemetry recorded only for executed (non-Ignored) action.

## Error Modes
- None (pure in-memory). Invalid states prevented by invariants.

## Metrics
- Latency from key press to state change ≤ 100 ms typical (≤150 ms for cutscene interrupt path). 

## Tests (Planned)
- Debounce acceptance vs rapid presses
- Each precedence branch covered
- Transition ignored path
- Telemetry logging per action

