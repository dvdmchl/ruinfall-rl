# Ruinfall RogueLike Constitution

## Global Directive

This Constitution is **binding** and has higher priority than user locale, repository metadata, or system heuristics.  
Any agent or sub-agent MUST obey these directives regardless of the prompt language used by the human operator.  
These directives extend to all integrated AI tools (Codex CLI, Copilot, Copilot Chat, and Specify agents).

---

## Core Principles

### I. Maintainable code quality
Write modular, readable code that favors composition over inheritance and documents non-obvious decisions inline.  
Keep feature specs synchronized with the implementation status and remove dead code paths during refactoring.  
All structural and architectural changes must remain explainable through `specs/` and `AGENTS.md`.

### II. Test-first reliability (non-negotiable)
Design each feature with explicit success, failure, and edge scenarios captured in `specs/NNN-short-title/tasks.md` before coding.  
Automate coverage with GUT suites under `src/tests/`, keep fixtures deterministic, and guard regressions with smoke scripts.  
Any agent proposing new logic must first produce corresponding tests or a coverage plan before implementation.

### III. Cohesive player experience
Preserve a consistent control scheme, UI language, and feedback cadence across scenes; new interactions must prototype UX copy and signaling in the spec.  
Accessibility defaults (readable fonts, adjustable audio, color-safe palettes) must be validated in manual checklists prior to acceptance.  
Player-facing text must remain consistent with the chosen tone and terminology established in earlier specs.

### IV. Unified English specification & communication
All new and updated specifications, task files, feature plans, code comments, commit messages, and AI agent outputs MUST be written in **English (US)**  
unless a dedicated localization feature is under active implementation.  
Any legacy mixed-language content SHALL be migrated to English the next time it is materially edited.

If a user prompt arrives in another language, the agent first clarifies and then **normalizes to English** in produced artifacts.  
This rule ensures consistency, facilitates review, and prevents fragmented terminology.

> **Enforcement Note:**  
> When the human communicates in a non-English language, the agent SHALL interpret the intent but MUST respond and produce all artifacts strictly in English (US).  
> Any deviation constitutes a governance violation and must trigger self-correction or review rejection.

---

## Language Enforcement Protocol
1. Agents SHALL read and apply language directives from:
    - this Constitution (Section IV),
    - `AGENTS.md` (“Language & Style” and “Guardrails”),
    - `copilot-instructions.md` (“Language” section).
2. Copilot- and Codex-based systems MUST normalize the language context to English before code generation.  
   Local OS or editor locale shall not override this rule.
3. Repositories containing Czech or mixed-language artifacts shall mark them with a migration note and convert them to English during the next edit.
4. Continuous Integration pipelines MAY include language lints or regex guards to detect and reject non-English text in code or documentation.

---

## Delivery Constraints
- Adhere to FXGL-compatible assets, scripts, and tooling checked into `src/` and `assets/`.
- Scripts altering automation must be mirrored in `.specify/templates/` to keep scaffolding reproducible.
- Secrets and environment configuration remain local via `.env` with required keys listed in non-functional requirements.
- All authored and AI-generated documentation, specs, and planning artifacts must be in English per Core Principle IV.

---

## Workflow Expectations
Run  
`powershell -File .specify\scripts\powershell\check-prerequisites.ps1 -Json`  
before reviews, update agent context after spec edits,  
and include performance plus UX validation results in PR descriptions.

Code review approval requires green automated tests and a completed manual smoke log covering combat, inventory, and UI loops.  
Agents must reject or rewrite non-English specification drafts into English (US) prior to acceptance.

---

## Governance
This Constitution supersedes conflicting guidance; amendments demand a documented rationale, reviewer consensus, and a migration plan covering code, tests, and player experience impacts.  
CI, reviewers, and agents must block merges that violate any core principle or delivery constraint.

To maintain alignment between human and AI contributors:
- `constitution.md` defines **why and what** must be upheld,
- `AGENTS.md` defines **how** agents execute those standards,
- `copilot-instructions.md` defines **how** Copilot enforces them inside the IDE.

**Version**: 1.0.2  
**Ratified**: 2025-10-08  
**Amendments**: Added *Language Enforcement Protocol* and cross-references to `AGENTS.md` and `copilot-instructions.md`.
