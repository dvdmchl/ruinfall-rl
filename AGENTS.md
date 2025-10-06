# Repository Guidelines

## Project structure & module organization
Treat `.specify/memory/constitution.md` as the canon for automation changes before touching prompts or templates. Keep agent presets inside `.github/prompts/` and document the reason for each change. Each feature branch starts with `pwsh -File .specify\scripts\powershell\create-new-feature.ps1 "short summary"`, which generates `specs/NNN-short-title/` containing `spec.md`, `plan.md`, and `tasks.md`. Runtime code belongs in `src/` with partner assets in `assets/`, keeping design docs and tooling isolated from the game. Place future test suites under `src/tests/` to align with GUT discovery.

## Build, test, and development commands
- `pwsh -File .specify\scripts\powershell\check-prerequisites.ps1 -Json`: audits required tooling and reports active specs before work begins.
- `pwsh -File .specify\scripts\powershell\update-agent-context.ps1`: refreshes Specify agent memory after specs or task lists change.
- `godot --path src --editor`: opens the project for scene iteration once the Godot project is committed to `src/`.

## Coding style & naming conventions
Use UTF-8 encoding, four-space indentation, and wrap Markdown near 100 characters. PowerShell scripts should follow verb-noun function names with PascalCase parameters and explicit error handling. When adding GDScript, keep filenames snake_case, class names PascalCase, and group lifecycle callbacks (`_ready`, `_process`, `_physics_process`) together.

## Testing guidelines
Document planned coverage in each `specs/.../tasks.md`, mapping tests to requirements and edge cases. Automate gameplay checks with GUT in `src/tests/` and run `godot -s addons/gut/gut_cmdln.gd -gdir=res://tests` locally. Maintain a manual smoke checklist for combat, inventory, and UI flows, and store it beside the relevant spec for reviewer access.

## Commit & pull request guidelines
Follow the existing short, imperative commit style and include the feature ID, for example `123-add-inventory-ui`. Reference the matching `specs/NNN-short-title/` folder in every PR description, call out the principal scenes or scripts touched, and attach GIFs or screenshots for visual updates. Before review, rerun `check-prerequisites.ps1` to catch missing artifacts or tooling gaps.

## Security & configuration tips
Keep API keys or platform secrets in a local `.env` excluded from version control and mention required variables in the spec's non-functional requirements. When adjusting automation, confirm changes stay compatible with `.specify/templates/` so newly scaffolded specs remain consistent.
