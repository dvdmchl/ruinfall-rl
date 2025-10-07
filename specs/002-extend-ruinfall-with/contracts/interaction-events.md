# Interaction Event Contracts (Internal)

These are internal (non-network) semantic events. Tests may simulate these.

| Event | Payload | Description |
|-------|---------|-------------|
| HoverTileChanged | x:int, y:int | Mouse moved to a different tile (map region) |
| HoverStackCycled | direction:int | Scroll wheel adjusted stack index (±1) |
| ObjectSelected | objectId:String | Left-click selection established |
| SelectionCleared | reason:String | Selection cleared (ESC, EmptyClick, VisibilityLost) |
| ContextMenuOpened | objectId:String | Right-click or middle-click stack badge/menu open |
| ContextMenuClosed | reason:String | Closed via OutsideClick, ESC, VisibilityLost |
| StackMenuOpened | x:int, y:int | User requested stack list (badge or middle click) |
| PlaceholderActionInvoked | objectId:String, actionCode:String | Attempted placeholder action (logs + toast) |

These events are not yet formal Java classes; they are documented to guide test naming and manager responsibilities.

