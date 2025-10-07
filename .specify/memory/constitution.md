# Ruinfall RogueLike Constitution

## Core principles

### I. Maintainable code quality
Write modular, readable code that favors composition over inheritance and documents non-obvious decisions inline.
Keep feature specs synchronized with the implementation status and remove dead code paths during refactoring.

### II. Test-first reliability (non-negotiable)
Design each feature with explicit success, failure, and edge scenarios captured in `specs/NNN-short-title/tasks.md` before coding.
Automate coverage with GUT suites under `src/tests/`, keep fixtures deterministic, and guard regressions with smoke scripts.

### III. Cohesive player experience
Preserve a consistent control scheme, UI language, and feedback cadence across scenes; new interactions must prototype UX copy and signaling in the spec.
Accessibility defaults (readable fonts, adjustable audio, color-safe palettes) must be validated in manual checklists prior to acceptance.

### IV. Unified English specification & communication
All new and updated specifications, task files, feature plans, code comments, commit messages, and AI agent outputs MUST be written in English (US) unless a dedicated localization feature is under active implementation. Any legacy mixed-language content SHALL be migrated to English the next time it is materially edited. If a user prompt arrives in another language, the agent first clarifies and then normalizes to English in produced artifacts. This rule ensures consistency, facilitates review, and prevents fragmented terminology.

## Delivery constraints
- Adhere to FXGL-compatible assets, scripts, and tooling checked into `src/` and `assets/`.
- Scripts altering automation must be mirrored in `.specify/templates/` to keep scaffolding reproducible.
- Secrets and environment configuration remain local via `.env` with required keys listed in non-functional requirements.
- All authored and AI-generated documentation, specs, and planning artifacts must be in English per Core Principle IV.

## Workflow expectations
Run `pwsh -File .specify\scripts\powershell\check-prerequisites.ps1 -Json` before reviews, update agent context after spec edits,
and include performance plus UX validation results in PR descriptions. Code review approval requires green automated tests and a completed
manual smoke log covering combat, inventory, and UI loops. Agents must reject or rewrite non-English specification drafts into English (US) prior to acceptance.

## Governance
This constitution supersedes conflicting guidance; amendments demand a documented rationale, reviewer consensus, and a migration plan covering code,
tests, and player experience impacts. CI, reviewers, and agents must block merges that violate any core principle or delivery constraint.

**Version**: 1.0.1 | **Ratified**: 2025-10-06 | **Last amended**: 2025-10-06 (Added English-only specification policy)
