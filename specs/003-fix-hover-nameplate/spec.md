# Feature Specification: Fix stale hover / nameplate when objects move or viewport scrolls

**Feature Branch**: `003-fix-hover-nameplate`  
**Created**: 2025-10-07  
**Status**: Draft  
**Input**: User description: "Fix bug when hovering or trying to select moving object like enemy the nameplate appears only when hovering over some older position of the object"

## Execution Flow (main)
```
1. Parse user description from Input
   → If empty: ERROR "No feature description provided"
2. Extract key concepts from description
   → Identify: actors, actions, data, constraints
3. For each unclear aspect:
   → Mark with [NEEDS CLARIFICATION: specific question]
4. Fill User Scenarios & Testing section
   → If no clear user flow: ERROR "Cannot determine user scenarios"
5. Generate Functional Requirements
   → Each requirement must be testable
   → Mark ambiguous requirements
6. Identify Key Entities (if data involved)
7. Run Review Checklist
   → If any [NEEDS CLARIFICATION]: WARN "Spec has uncertainties"
   → If implementation details found: ERROR "Remove tech details"
8. Return: SUCCESS (spec ready for planning)
```

---

## ⚡ Quick Guidelines
- ✅ Focus on WHAT users need and WHY
- ❌ Avoid HOW to implement (no tech stack, APIs, code structure)
- 👥 Written for business stakeholders, not developers

### Section Requirements
- **Mandatory sections**: Must be completed for every feature
- **Optional sections**: Include only when relevant to the feature
- When a section doesn't apply, remove it entirely (don't leave as "N/A")

### For AI Generation
When creating this spec from a user prompt:
1. **Mark all ambiguities**: Use [NEEDS CLARIFICATION: specific question] for any assumption you'd need to make
2. **Don't guess**: If the prompt doesn't specify something (e.g., "login system" without auth method), mark it
3. **Think like a tester**: Every vague requirement should fail the "testable and unambiguous" checklist item
4. **Common underspecified areas**:
   - User types and permissions
   - Data retention/deletion policies  
   - Performance targets and scale
   - Error handling behaviors
   - Integration requirements
   - Security/compliance needs

---

## User Scenarios & Testing *(mandatory)*

### Primary User Story
As a player, when I place my mouse cursor over an interactive moving object (e.g. an enemy) I want the hover highlight / nameplate to correspond to the tile visually under the cursor so I can reliably identify and select moving targets.

### Acceptance Scenarios
1. Given the enemy is stationary and my cursor is over its tile, When the UI updates, Then the enemy nameplate is shown immediately above the enemy.
2. Given my cursor stays still while the camera/viewport scrolls (because the player moved and the camera recenters), When a moving enemy passes under the cursor, Then its nameplate appears without requiring me to move the mouse.
3. Given my cursor is over an enemy and the enemy moves away, When it vacates the tile, Then the nameplate disappears (unless another visible interactive object now occupies the tile) without needing a mouse wiggle.
4. Given the viewport has a non-zero offset, When I click a visible enemy, Then the correct enemy is selected (no tile offset error).
5. Given multiple stacked interactives on a tile under the cursor with a scrolled viewport, When I scroll the mouse wheel to cycle, Then cycling order works identically to the unscrolled case.

### Edge Cases
- Viewport not scrolled (offset 0,0) must still behave exactly as before.
- Viewport partially scrolled with fractional (double) coordinates should still map to correct integer tile indices (floor division semantics).
- Cursor outside map + large viewport offsets: no hover state leakage / stale nameplate.
- Rapid successive movements (enemy + player + camera) in one frame do not produce inconsistent hover state.
- Negative mouse deltas or scrolling while no stack present does nothing harmful.

## Requirements *(mandatory)*

### Functional Requirements
- **FR-001**: System MUST correctly map mouse scene coordinates to world tile coordinates by incorporating current viewport (camera) offset.
- **FR-002**: System MUST refresh hover state (and thus nameplate target) when interactive objects move onto or off the currently hovered tile, even if the mouse hasn’t moved.
- **FR-003**: System MUST keep previous behavior for cycling stacked interactives (scroll wheel) unchanged under viewport offsets.
- **FR-004**: System MUST prevent showing a nameplate for a tile that no longer contains the previously hovered object (stale reference cleared on refresh).
- **FR-005**: System MUST ensure selection (left click) and context menu (right click) act on the visually clicked tile after scrolling.
- **FR-006**: System MUST handle fractional viewport offsets without off-by-one tile errors (floor world / tile size).
- **FR-007**: System MUST include unit test(s) validating coordinate translation with non-zero viewport offsets.
- **FR-008**: System MUST remain deterministic in tests (no RNG dependency for hover logic).
- **FR-009**: System MUST avoid performance regressions (hover refresh remains O(stack) like before; no per-frame full-map scans).

### Key Entities
- **Viewport Offset**: Logical camera translation (x,y) added to scene (mouse) coordinates to derive world coordinates.
- **Hover State**: Existing record storing tileX, tileY, hoveredId, stackIndex; must stay in sync with tile under cursor after moves or refresh triggers.
- **Interactive Object**: Already defined; movement triggers registry updates which must prompt hover reevaluation if relevant.

---

## Review & Acceptance Checklist
*GATE: Automated checks run during main() execution*

### Content Quality
- [x] No implementation details (languages, frameworks, APIs) beyond unavoidable references to existing domain terms
- [x] Focused on user value and business needs (accurate targeting & feedback)
- [x] Written for non-technical stakeholders (keeps technical depth minimal)
- [x] All mandatory sections completed

### Requirement Completeness
- [x] No [NEEDS CLARIFICATION] markers remain
- [x] Requirements are testable and unambiguous  
- [x] Success criteria are measurable (correct tile mapping, immediate refresh, tests pass)
- [x] Scope is clearly bounded (hover / nameplate & input mapping only)
- [x] Dependencies and assumptions identified (depends on existing HoverManager & viewport values)

## Execution Status
*Updated by main() during processing*

- [x] User description parsed
- [x] Key concepts extracted
- [x] Ambiguities marked (none remaining)
- [x] User scenarios defined
- [x] Requirements generated
- [x] Entities identified
- [ ] Review checklist passed (to be confirmed after implementation & tests)
