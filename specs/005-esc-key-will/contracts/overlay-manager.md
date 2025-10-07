# Contract: OverlayStackService

## Purpose
Maintain LIFO stack of overlays and compute their contribution to world pause state.

## Responsibilities
- Push/pop overlay entries.
- Track pausesWorld flag.
- Provide top overlay for precedence decision.

## Key Methods
| Method | Description | Effects |
|--------|-------------|---------|
| openOverlay(type, pausesWorld=true) | Push overlay entry | Adds to stack, increments pause count if pausesWorld |
| closeTopOverlay() | Pop top entry if exists | Removes entry; adjusts pause count |
| hasOverlays() | Returns stack non-empty | Read only |
| topOverlay() | Returns top entry or null | Read only |
| pausingOverlayCount() | Count of overlays with pausesWorld=true | Read only |

## Invariants
- No duplicate id in stack (ids unique per open instance).
- pausingOverlayCount recomputed or updated incrementally.

## Tests (Planned)
- Open / close sequence maintains correct count.
- Multiple overlays closing in LIFO order.
- Interaction with pause menu (resume only when both cleared).

