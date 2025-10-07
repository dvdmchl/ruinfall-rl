# Data Model: ESC Key Navigation

## Entities
### NavigationState
| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| mode | enum(Gameplay, PauseMenu, PostRun) | Primary high-level mode | Non-null |
| menuDepth | int | Depth >0 means submenu level | >=0 when mode=PauseMenu else 0 |
| transitionActive | boolean | True during non-interruptible transition | If true & no dialog/overlay → ESC ignored |
| cutsceneActive | boolean | True if cutscene playing | Interruptible per FR-014 |
| postRun | boolean | True if in end-of-run screen | Mutually exclusive with cutsceneActive |

### OverlayEntry
| Field | Type | Description | Constraints |
| id | String | Unique overlay identifier | Non-empty, unique in stack |
| type | String | Overlay type (inventory,map,other) | Lowercase slug |
| openedAt | long (ms) | Timestamp when opened | >=0 |
| pausesWorld | boolean | Whether overlay contributes to pausing | For MVP always true |

### OverlayStack
| Field | Type | Description | Constraints |
| entries | List<OverlayEntry> | LIFO stack of overlays | Top = last item |

### DialogState
| Field | Type | Description | Constraints |
| present | boolean | Whether a blocking dialog is shown | If present=true, dialogType != null |
| dialogType | String? | Semantic type (confirm, alert, etc.) | Nullable only when present=false |

### ESCDebounceState
| Field | Type | Description | Constraints |
| lastAcceptedEpochMillis | long | Timestamp of last accepted ESC action | Initial = -1 |

### ESCActionRecord
| Field | Type | Description | Constraints |
| timestamp | long | When action executed | >=0 |
| actionType | enum(EscActionType) | Classified action | Non-null |

### TelemetryConfig
| Field | Type | Description | Constraints |
| enabled | boolean | Toggle for logging | Default true |

### EscActionType (enum)
Values: OpenMenu, NavigateBack, DismissDialog, ResumeGameplay, CloseOverlay, InterruptCutscene

## Derived / Computed
- pausingLayerCount = (PauseMenu open?1:0) + count(overlays where pausesWorld=true)
- worldPaused = pausingLayerCount > 0

## Invariants
1. If menuDepth == 0 then back navigation from PauseMenu top-level resumes gameplay (unless pre-run state).
2. If OverlayStack empty AND no PauseMenu AND cutsceneActive=false AND worldPaused=false.
3. A single ESC discrete press mutates at most one of: DialogState, OverlayStack, cutsceneActive, menuDepth/worldPaused.
4. If transitionActive=true and DialogState.present=false and OverlayStack empty → ESC produces no action (ignored).

## State Transitions Overview
| Input | Current Key States | Result | ActionType |
|-------|--------------------|--------|------------|
| ESC | Gameplay, no overlays, no dialog | Open Pause Menu | OpenMenu |
| ESC | PauseMenu depth>0 | Decrease depth by 1 | NavigateBack |
| ESC | Dialog present | Dismiss dialog | DismissDialog |
| ESC | Overlays present (no dialog) | Pop top overlay | CloseOverlay |
| ESC | Cutscene active (no dialog/overlay) | cutsceneActive=false + open Pause Menu | InterruptCutscene |
| ESC | PauseMenu top-level (entered from gameplay), no overlays | Close menu, resume | ResumeGameplay |
| ESC | Transition active (no higher precedence) | No state change | (ignored) |

## Validation Rules
- Debounce: Accept only if (now - lastAcceptedEpochMillis) >= 200 ms.
- Only one state mutation path executed per accepted press.
- Telemetry recorded only if enabled AND action executed.

## Error Handling
All operations are in-memory; invalid states prevented by invariants & controlled mutation methods.

