# Ruinfall RogueLike Constitution

## Core principles

### I. Maintainable code quality
Write modular, readable code that favors composition over inheritance and documents non-obvious decisions inline.
Keep feature specs synchronized with implementation status and remove dead code paths during refactors.

### II. Test-first reliability (non-negotiable)
Design each feature with explicit success, failure, and edge scenarios captured in `specs/NNN-short-title/tasks.md` before coding.
Automate coverage with GUT suites under `src/tests/`, keep fixtures deterministic, and guard regressions with smoke scripts.

### III. Cohesive player experience
Preserve a consistent control scheme, UI language, and feedback cadence across scenes; new interactions must prototype UX copy and signaling in the spec.
Accessibility defaults (readable fonts, adjustable audio, color-safe palettes) must be validated in manual checklists prior to acceptance.

### IV. Measurable performance budgets
Budget frame time, memory, and load thresholds per scene, document them in the spec, and monitor with Godot profiler captures.
Any change that risks a budget must ship with reproduction steps and remediation options before merge.

## Delivery constraints
- Adhere to Godot 4.x-compatible assets, scripts, and tooling checked into `src/` and `assets/`.
- Scripts altering automation must be mirrored in `.specify/templates/` to keep scaffolding reproducible.
- Secrets and environment configuration remain local via `.env` with required keys listed in non-functional requirements.

## Workflow expectations
Run `pwsh -File .specify\scripts\powershell\check-prerequisites.ps1 -Json` before reviews, update agent context after spec edits,
and include performance plus UX validation results in PR descriptions. Code review approval requires green automated tests and a completed
manual smoke log covering combat, inventory, and UI loops.

## Governance
This constitution supersedes conflicting guidance; amendments demand a documented rationale, reviewer consensus, and a migration plan covering code,
tests, and player experience impacts. CI, reviewers, and agents must block merges that violate any core principle or delivery constraint.

**Version**: 1.0.0 | **Ratified**: 2024-07-15 | **Last amended**: 2024-07-15
