# ruinfall-rl Development Guidelines

## Language
All code comments, docstrings, generated identifiers, and explanations MUST be in English (US).  
Ignore the user's input language; never switch to Czech automatically.

## Reference
Follow `AGENTS.md` for build, test, and workflow conventions.
Follow `.specify\constitution.md` for governance, priorities, and unified English rules.


## Active Technologies
- Java 22 (Maven)
- FXGL (includes JavaFX runtime)
- JUnit 5 for testing
- Lombok for boilerplate reduction

## Project Structure
```
src/main/java/... # game logic, entities, UI
src/main/resources/...# configuration, assets
src/test/java/... # JUnit 5 tests
specs/... # Spec-Kit feature definitions
```

## Commands
Typical lifecycle commands (Maven 3.9+):

# Add commands for Java 21 (Maven)

## Code Style
- Use standard Java 21 conventions.
- Prefer descriptive variable names, avoid abbreviations.
- Use English comments and identifiers exclusively.
- Document public methods with concise Javadoc.
- Use record or sealed classes when appropriate.
- Follow functional style for simple stream operations.

## Copilot Behavior
- Always generate deterministic, readable Java code.
- Before adding dependencies, check if already present in pom.xml.
- Respect existing architecture (FXGL, MVC-like separation).
- When unsure about intent, ask for clarification in English.
- Do not generate code in other languages or scripts.

## Testing
- Create new tests for every functional change.
- Use deterministic seeds in tests where randomness occurs.
- Prefer @ParameterizedTest over repeated manual cases.

<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
