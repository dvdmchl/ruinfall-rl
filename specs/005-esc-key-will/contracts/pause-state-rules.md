# Contract: Pause & Precedence Rules

## Precedence Order
Dialog > Overlay > Cutscene > (Transition ignore) > Normal Menu Logic

## Decision Matrix (Simplified)
| Dialog | OverlayCount | Cutscene | Transition | PauseMenuOpen | menuDepth | Result Action |
|--------|--------------|----------|------------|---------------|-----------|---------------|
| Y | * | * | * | * | * | DismissDialog |
| N | >0 | * | * | * | * | CloseOverlay |
| N | 0 | Y | * | * | * | InterruptCutscene (open menu depth 0) |
| N | 0 | N | Y | * | * | Ignored |
| N | 0 | N | N | N | - | OpenMenu |
| N | 0 | N | N | Y | >0 | NavigateBack |
| N | 0 | N | N | Y | 0 | ResumeGameplay (if enteredFromGameplay & no overlays) |

## State Transition Notes
- InterruptCutscene sets cutsceneActive=false then opens menu (counts as one action).
- NavigateBack never closes menu if depth>1; just depth--.
- ResumeGameplay only valid if previous state was gameplay before menu stack.

## Timing Rule
- All actions require debounce satisfied (>=200 ms since last accepted ESC action).

## Ignored Condition
- TransitionActive true AND no Dialog AND no Overlay → ESC ignored (no telemetry).

## Telemetry Classification
| Action | EscActionType |
|--------|---------------|
| OpenMenu | OpenMenu |
| Submenu back | NavigateBack |
| Dialog dismissed | DismissDialog |
| Pause menu closed → gameplay | ResumeGameplay |
| Overlay closed | CloseOverlay |
| Cutscene interrupted | InterruptCutscene |

## Tests (Planned)
- Matrix row coverage.
- Transition ignore path.
- Dialog + Overlay coexist (dialog wins).

