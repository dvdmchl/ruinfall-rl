# Repository Guidelines

## Language & Style
- All generated content **must be in English (US)**, including:
    - code comments and identifiers
    - commit messages
    - documentation and explanations
    - test names and log outputs
- If the human operator writes in another language, interpret intent but **normalize output to English (US)**.
- Never mirror or switch to Czech automatically.
- Follow standard **Java 21** conventions (four-space indentation, UTF-8 encoding).
- Use clear, descriptive variable and method names; avoid abbreviations.
- Write concise Javadoc for all public methods.
- Use `record` or `sealed` types when they improve design clarity.
- Prefer functional constructs (Streams, Optionals) where suitable.
- Maintain a consistent and deterministic coding style across agents and Copilot.

---

## Project structure & module organization
Treat `.specify/memory/constitution.md` as the canon for automation changes before touching prompts or templates.  
Keep agent presets inside `.github/prompts/` and document the reason for each change.  
Each feature branch starts with  
`powershell -File .specify\scripts\powershell\create-new-feature.ps1 "short summary"`,  
which generates `specs/NNN-short-title/` containing `spec.md`, `plan.md`, and `tasks.md`.

Runtime code belongs in `src/` with partner assets in `assets/`, keeping design docs and tooling isolated from the game.  
Place future test suites under `src/tests/` to align with GUT discovery.

---

## Build, test, and development commands
- `powershell -File .specify\scripts\powershell\check-prerequisites.ps1 -Json`  
  Audits required tooling and reports active specs before work begins.
- `powershell -File .specify\scripts\powershell\update-agent-context.ps1`  
  Refreshes Specify agent memory after specs or task lists change.

---

## Coding style & naming conventions
- Use UTF-8 encoding and wrap Markdown near 100 characters.
- PowerShell: verb-noun function names, PascalCase parameters, explicit error handling.
- GDScript: filenames snake_case, classes PascalCase, group lifecycle callbacks together.
- Java: standard 21 style, Spotless formatting, Checkstyle rules in CI.

---

## Testing guidelines
Document planned coverage in each `specs/.../tasks.md`, mapping tests to requirements and edge cases.  
Automate gameplay checks with GUT in `src/tests/`   
Maintain a manual smoke checklist for combat, inventory, and UI flows, stored beside the relevant spec for reviewers.

---

## Commit & pull request guidelines
Follow the short, imperative commit style and include the feature ID,  
for example `123-add-inventory-ui`.  
Reference the matching `specs/NNN-short-title/` folder in every PR description,  
call out the principal scenes or scripts touched, and attach GIFs or screenshots for visual updates.  
Before review, rerun `check-prerequisites.ps1` to catch missing artifacts or tooling gaps.

---

## Security & configuration tips
Keep API keys or platform secrets in a local `.env` excluded from version control and mention required variables in the spec’s non-functional requirements.  
When adjusting automation, confirm compatibility with `.specify/templates/` so newly scaffolded specs remain consistent.

---

## Setup
- Java 21, Maven 3.9+
- **Build:** `mvn -B clean verify`
- **Run (seeded):**  
  `mvn -DskipTests exec:java -Dexec.mainClass=org.dreamabout.sw.game.ruinfall.RuinfallApp -- -seed=12345`

---

## Test
- Unit/integration:  
  `mvn -B -Dtestfx.headless=true -Djava.awt.headless=true test`
- Performance toggle:  
  `-Druinfall.debugPerf=true`

---

## Project Map
- `src/main/java/...` — source code
- `specs/`      — Spec-Kit iteration specifications
- `.github/workflows/` — CI configuration
- `scripts/`     — cross-platform utility scripts

---

## Conventions
- Spotless + Checkstyle mandatory (CI gate)
- Commits → Conventional Commits (`feat`, `fix`, `test`, `chore`)
- Small, reviewable PRs (≤ 3 files / ≈ 200 LOC)

---

## Guardrails
- Always read the relevant `specs/...` and **generate tests first** from the “Acceptance Criteria”.
- Respect the unified English rule from `constitution.md` and `copilot-instructions.md`.
- Do not add new external dependencies without explicit approval (TODO in PR).
- Avoid any I/O outside the project (network/files) unless explicitly required by tests.
- Keep generated code deterministic and idempotent across runs.
- Ask for clarification in English if intent is ambiguous.

---

## Definition of Done
- ✅ All tests green in CI.
- ✅ Acceptance criteria covered by unit and e2e tests.
- ✅ All code comments and identifiers are in English (US).
- ✅ Spotless and Checkstyle pass without violations.
- ✅ README / CHANGELOG updated.
- ✅ No unauthorized dependencies added.  
