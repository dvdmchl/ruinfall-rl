# Feature Specification: Move Seed Text to Main Menu Screen

**Feature Branch**: `004-move-seed-text`  
**Created**: 2025-10-07  
**Status**: Draft (Ready for Planning)  
**Input**: User description: "Move seed text to main menu screen"

## Execution Flow (main)
```
1. Parse user description from Input
2. Extract key concepts (player, seed display, pre-run visibility)
3. Resolve ambiguities via Decisions & Assumptions
4. Populate User Scenarios & Testing
5. Generate Functional Requirements (testable)
6. Identify Key Entities
7. Verify no unresolved ambiguities remain
8. Mark spec Ready for Planning
```

---

## ⚡ Quick Guidelines
- ✅ Focus on WHAT users need and WHY
- ❌ Avoid HOW to implement (no tech stack, APIs, code structure)
- 👥 Written for business stakeholders, not developers

---

## Decisions & Assumptions
(Resolved from originally ambiguous areas; all are now part of scope.)
1. Manual Entry: Player MAY manually enter a seed in a text input on the main menu.
2. Visibility: Seed appears only on the main menu (not in-game HUD / overlays) once feature is complete.
3. Label Wording: Use label "World Seed:" before the value.
4. Default Seed Generation: On first showing the main menu in an application session, a random valid seed is generated automatically.
5. Persistence: The current seed persists across returning to the main menu (same application session) until the player manually changes it or clicks Randomize.
6. Crash/Forced Exit: On application restart, a new random seed is generated (no cross-session persistence required at this stage).
7. Randomize Action: Provide a "Randomize" button that replaces the current seed with a new one immediately.
8. Copy Action: Provide a "Copy" button that copies the current seed to clipboard and triggers a brief visual confirmation.
9. Input Rules: Allowed characters: A–Z, a–z, 0–9. Case-insensitive; display normalized as uppercase.
10. Length Limit: Maximum 16 characters. Excess input is ignored (not truncated mid-stream after acceptance—keystrokes beyond 16 are not applied).
11. Empty Input Handling: If the field becomes empty when Start Run is pressed, a new random seed is generated and used (and displayed) before world generation proceeds.
12. Non-Alphanumeric Characters: Rejected silently (they do not appear in the field) to keep UX friction low.
13. Application Timing: Seed value used for generation is the value displayed at the instant Start Run is activated.
14. Concurrent Activation: Double activation (e.g. double click / rapid key press) must not cause mismatch; first accepted activation locks the seed for that run.
15. Live Update: The seed value updates in real time as the player types; no separate confirm step.
16. Determinism Scope: Same seed within same game version yields same deterministic world (broader generation contract acknowledged, not redefined here).

---

## User Scenarios & Testing *(mandatory)*

### Primary User Story
As a player preparing to start (or restart) a new run, I want to clearly see and optionally adjust the world seed on the main menu so I can reproduce, share, or experiment with procedural worlds before launching the run.

### Acceptance Scenarios
1. Given the player opens the main menu for the first time this session, When the menu finishes loading, Then a label "World Seed:" and a 1–16 char alphanumeric seed (uppercase) are visible.
2. Given the seed field is focused, When the player types valid alphanumeric characters, Then those characters appear (up to 16) in uppercase and immediately reflect the pending run seed.
3. Given the player clicks Randomize, When the action completes, Then the seed field is replaced with a new valid seed different from the previous one and displayed instantly.
4. Given the player clicks Copy, When the action completes, Then the current seed is available in the clipboard and a brief confirmation indicator becomes visible (and disappears automatically within a short duration).
5. Given the player has a displayed seed, When they press Start Run, Then the run uses exactly that displayed seed and the same seed could be re-entered later to recreate the same world layout.
6. Given the player started a run and later returns to the main menu in the same session, When the menu is shown again, Then the previously used seed remains displayed unchanged.
7. Given the player clears the input entirely, When they press Start Run, Then a new random seed is generated, displayed, and used for generation (no error dialog shown).
8. Given the player attempts to input invalid characters (e.g. punctuation or spaces), When they press those keys, Then those characters do not appear and the current seed remains composed only of valid alphanumeric characters.
9. Given the player double-clicks Start Run rapidly, When the game processes the request, Then only one run starts and the seed used equals the one displayed at the moment of the first activation.

### Edge Cases
- Input length boundary: Attempting to enter a 17th character → It is ignored; field remains at 16 chars.
- All characters deleted then rapidly pressing Start → System generates and displays a new random seed before generation begins; no crash.
- Randomize pressed repeatedly very quickly → Each accepted press updates seed; final displayed seed before Start is authoritative.
- Copy pressed after an immediate Randomize → Copied value matches the seed currently displayed post-randomization.
- Attempted paste containing invalid characters (e.g. "abc-123_!!XYZ") → Field ends up as "ABC123XYZ" (only valid chars, truncated to 16 if needed).

## Requirements *(mandatory)*

### Functional Requirements
- **FR-001**: System MUST display the current world seed on the main menu with label "World Seed:" before any run starts.
- **FR-002**: System MUST auto-generate a valid seed when the main menu is first shown in a session.
- **FR-003**: System MUST allow manual entry of a seed consisting only of alphanumeric characters (A–Z, 0–9) up to 16 characters.
- **FR-004**: System MUST normalize user-entered seed to uppercase for display while treating input case-insensitively for determinism.
- **FR-005**: System MUST ignore (not insert) any non-alphanumeric character input or paste content.
- **FR-006**: System MUST prevent the seed field from exceeding 16 characters (excess input is discarded at entry time).
- **FR-007**: System MUST provide a Randomize action that replaces the current seed with a newly generated valid seed immediately.
- **FR-008**: System MUST provide a Copy action that places the exact currently displayed seed into the system clipboard and gives visual confirmation.
- **FR-009**: System MUST use exactly the currently displayed seed value when the player starts a run.
- **FR-010**: System MUST generate and use a new random seed if the player attempts to start a run with an empty seed field (and display it before generation proceeds).
- **FR-011**: System MUST persist the seed value while the application remains running (returning to main menu does not auto-randomize).
- **FR-012**: System MUST ensure double activation of the start action does not produce multiple differing seeds; only the first activation seed is used.
- **FR-013**: System MUST ensure that identical seed strings produce identical deterministic worlds under the same game version.
- **FR-014**: System MUST remove any prior in-run seed display (if it existed) so that the primary canonical source for the next run's seed is the main menu only.

### Key Entities
- **Seed**: Reproducibility token for procedural world generation. Attributes: value (uppercase alphanumeric ≤16), origin (Generated | UserEntered | Randomized), sessionPersistence (boolean within session), lastUpdated (timestamp – conceptual for reasoning, not prescribing storage).

---

## Review & Acceptance Checklist

### Content Quality
- [x] No implementation details (languages, frameworks, APIs)
- [x] Focused on user value and business needs
- [x] Written for non-technical stakeholders
- [x] All mandatory sections completed

### Requirement Completeness
- [x] No [NEEDS CLARIFICATION] markers remain
- [x] Requirements are testable and unambiguous  
- [x] Success criteria are measurable
- [x] Scope is clearly bounded
- [x] Dependencies and assumptions identified (see Decisions & Assumptions)

---

## Execution Status
*Updated by main() during processing*

- [x] User description parsed
- [x] Key concepts extracted
- [x] Ambiguities marked (resolved)
- [x] User scenarios defined
- [x] Requirements generated
- [x] Entities identified
- [x] Review checklist passed

---
