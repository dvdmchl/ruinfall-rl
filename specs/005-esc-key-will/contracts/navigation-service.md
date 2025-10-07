# Contract: NavigationStateService

## Purpose
Manage pause menu visibility, submenu depth, and resume semantics.

## Responsibilities
- Track whether Pause Menu is open and current submenu depth.
- Provide navigation back (depth decrement) and resume conditions.
- Coordinate with OverlayStackService for consolidated pause state.

## Key Methods (Conceptual)
| Method | Description | Effects |
|--------|-------------|---------|
| openPauseMenu() | Open top-level menu if not already | Sets mode=PauseMenu, menuDepth=0, increments pause count |
| enterSubmenu() | Navigate into submenu | menuDepth++ |
| navigateBack() | If menuDepth>0 decrement else close & resume | menuDepth-- or triggers resume check |
| closePauseMenu() | Close menu entirely | Clears mode to Gameplay, menuDepth=0, decrement pause count |
| isPauseMenuOpen() | Returns boolean | Read only |
| shouldResumeGameplay() | Check if pausingLayerCount==0 | Returns boolean |
| markEnteredFromGameplay() | Flag for resume semantics | Internal flag updated |

## State Interaction
- Works with OverlayStackService.pausingLayerCount.
- Resume triggers only when (menu not open AND pausing overlays count == 0).

## Invariants
- menuDepth == 0 iff top-level.
- When Pause Menu closed → menuDepth reset to 0.

## Tests (Planned)
- Open → depth transitions
- Back navigation at depth>0
- Back navigation at depth=0 resumes only when no overlays
- Idempotent openPauseMenu()

