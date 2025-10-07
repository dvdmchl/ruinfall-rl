# Data Model: Interactive Object System

## Entities

### InteractiveObject (interface)
Fields / Methods:
- String getId()
- String getDisplayName()
- InteractiveObjectType getType()
- String getShortSummary()
- int getTileX(), getTileY()
- void setTilePosition(int x, int y) (mutable for movement)

### InteractiveObjectType (enum)
- ENEMY, NPC, CHEST

### InteractiveAction (enum)
- INSPECT (enabled)
- ATTACK (disabled placeholder, ENEMY only)
- LOOT (disabled placeholder, CHEST only)
- TALK (disabled placeholder, NPC only)

### InteractiveActionItem (value)
- String code
- String labelKey
- boolean enabled

### InteractiveRegistry
State:
- Map<String, InteractiveObject> byId
- Map<Long, LinkedHashSet<String>> tileMap (key = ((long)x << 32) | (y & 0xffffffff)) preserving insertion order
Methods:
- register(obj)
- unregister(id)
- moveObject(id, newX,newY)
- List<InteractiveObject> getStackAt(int x,int y) (ordered by priority then insertion)
- Optional<InteractiveObject> getPrimaryAt(x,y)

### SelectionState
- String selectedId (nullable)
- long changedAtNano

### SelectionManager
Responsibilities:
- select(InteractiveObject)
- clear()
- getState()
- validateSelection(VisibilityChecker)

### HoverState
- int hoverX, hoverY (tile)
- String hoveredId (resolved object) nullable
- int stackIndex (for cycling)

### HoverManager
- onHoverTile(x,y, registry)
- cycle(delta, registry)
- getCurrentObject(registry)

### NameplateModel
- String objectId
- List<String> lines (name + summary)
- int stackSize
- int stackIndex

### ContextMenuModel
- String objectId
- List<InteractiveActionItem> actions

### SidePanelViewModel
- String selectedObjectId
- String headerName
- String typeLabel
- String description
- List<InteractiveActionItem> actions

## Relationships
- Registry is source of truth; managers store only ids + indices.
- UI controller queries registry to build models each frame or on events.

## State Transitions
SelectionState:
- null -> id (on left click or programmatic select)
- id -> null (on ESC, empty tile click, visibility invalidation)

HoverState:
- (x1,y1,*) -> (x2,y2, reset index) on new tile
- index cycles within stack bounds on scroll input

## Validation Rules
- Selection only allowed if object currently visible (guarded externally by visibility checker).
- Hover object must be visible; else hoveredId = null.
- Cycling only if stack size > 1.
- Context menu single open at a time (enforced by controller, not stored here).


