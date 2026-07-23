---
name: scrapider-guidelines
description: Apply Scrapider coding guidelines when Codex writes, edits, refactors, debugs, or reviews code. Use for code tasks that need upfront implementation logic, explicit assumptions, minimal surgical changes, verification criteria, Spring Boot backend layering conventions, Python AI/agent/training project organization, and Android Kotlin/Jetpack Compose UI rules.
---

# Scrapider Guidelines

Use this skill to constrain Codex's coding behavior: think before changing code, explain the implementation logic, keep edits minimal, preserve existing project style, respect Spring Boot layering, and verify the result before claiming success.

## Before Coding

Before editing code, tell the user:

1. The problem or goal being solved.
2. The implementation approach.
3. The files expected to change.
4. The verification method.

If the request is ambiguous in a way that changes public interfaces, data shape, credentials, deployment topology, persistence, security posture, or irreversible operations, stop and ask. Do not guess silently.

If the ambiguity only affects implementation mechanics and the existing code, Jenkinsfile, Compose file, Dockerfile, or project documentation already shows a working local pattern, follow the existing pattern and make the smallest direct change.

When multiple solutions are possible, prefer the simpler one and briefly explain the tradeoff. If the requested design is risky, say why before implementing.

## Engineering Discipline

### Think First

- State assumptions and uncertainty before implementation.
- Do not hide confusion. Ask when the missing information changes the design.
- Do not silently choose between conflicting interpretations.
- Push back on unnecessary complexity, broad rewrites, or vague requirements.

### Simplicity First

- Write the minimum code that solves the current request.
- Do not add speculative features, configuration, extension points, or abstractions.
- Do not add defensive branches for impossible states unless the codebase already requires that pattern.
- If a change grows large, re-check whether a smaller direct change would solve the same problem.
- For existing CI, Docker, or deployment changes, prefer the minimum migration path that preserves the current runtime model, routing model, output mode, service names, credentials, and deployment topology.
- Do not introduce a new deployment architecture, output format, proxy layer, runtime mode, or packaging model unless the existing runtime cannot consume the requested artifact or the user explicitly asks for that broader migration.

### Surgical Changes

- Touch only files and lines that directly serve the user's request.
- Match the repository's existing structure, style, helpers, naming, and dependency choices.
- Do not refactor adjacent code just because it could be better.
- Do not reformat unrelated code.
- Remove imports, variables, functions, classes, or config that the current change made unused.
- Mention unrelated dead code or design issues instead of fixing them opportunistically.

### Business Contract Preservation

- Treat existing default values, thresholds, enums, and strategy parameters as business contracts.
- Prefer reusing existing constants, configuration properties, or enums for new functionality.
- Do not redefine equivalent parameters under new names.
- Do not hard-code magic numbers or magic values when an existing constant, config value, or enum already represents the concept.
- Do not change existing defaults without explicit confirmation.
- When a new requirement conflicts with an existing default or threshold, stop and clarify whether the change is global, scenario-specific, or still expected to reuse the existing contract.
- If a different value is required, explain the reason and wait for confirmation before changing or introducing it.

### Verification Mindset

- Define success criteria before or during implementation.
- For bug fixes, prefer a focused reproduction or failing test before changing behavior.
- For behavior changes, add or update focused tests when the repository has a test pattern.
- For configuration, CI, Docker, deployment, or environment changes, prefer operational verification such as build commands, generated artifact checks, `docker compose config`, Docker image builds, container startup, logs, and curl checks. Do not default to adding unit tests for deployment-only changes.
- Run the narrowest useful verification first, then broader checks only when risk warrants it.
- Do not claim success without command output, test results, or a clear explanation of why verification could not run.

## Coding Workflow

When implementing:

1. Inspect the existing code and tests before choosing an approach.
2. Make the smallest coherent change.
3. Use project-local helpers and framework conventions before adding new utilities.
4. Add or update tests when behavior changes or risk is non-trivial.
5. Run the most focused useful verification command available.

## Debugging Workflow

When fixing bugs:

1. Reproduce or localize the failure before proposing a fix.
2. Identify the smallest code path that explains the symptom.
3. Fix the cause, not only the visible symptom.
4. Add a regression check when practical.
5. Verify the failing path and any nearby affected path.

## Refactoring Rules

Refactor only when the user asks for it or when it is required to make the requested change cleanly.

- Keep behavior unchanged unless behavior change is explicitly requested.
- Preserve public APIs, request/response shapes, and database semantics unless told otherwise.
- Move code in small steps and verify after meaningful changes.
- Do not introduce a new abstraction for one call site.

## Review Rules

When reviewing code, lead with concrete findings:

- Prioritize correctness, regressions, data loss, security, API contract breaks, and missing tests.
- Use file and line references when available.
- Separate confirmed issues from questions or assumptions.
- Keep style preferences secondary unless they affect maintainability or consistency.

## Communication Rules

Be direct and specific:

- Say what changed, where it changed, and why.
- Name verification commands and results.
- If verification fails, report the failure and the next useful step.
- If no code was changed, say that clearly.

## Git Commit Rules

Do not create commits, push, create branches, stage files, or otherwise mutate Git state unless the user explicitly asks for that Git action. The rules below only define the format to use after permission exists; they are not permission to commit.

When creating git commits, use one bracketed change key followed by a colon and one or more numbered feature points:

```text
[key] : 1. feature point
        2. feature point
```

Choose the key from the actual staged change set:

- `[add]`: Use only when the commit contains added files or added functionality, with no deletions or modifications to existing behavior.
- `[del]`: Use only when the commit contains deletions, with no additions or modifications.
- `[upd]`: Use when the commit contains any modification, or when additions/deletions are mixed with other changes.

Keep the feature-point list concise:

- Use numbered items.
- Include at most 10 items.
- Merge small related changes into one item instead of listing file-by-file edits.
- Describe user-visible or maintenance-relevant changes, not implementation trivia.

Example:

```text
[upd] : 1. expand coding workflow rules
        2. add Spring Boot backend layering constraints
        3. document commit message format
```

When finishing, report:

1. What actually changed.
2. The verification command and result.
3. Any verification that could not be completed, with the reason.

## Spring Boot Backend

When working in any Spring Boot backend, follow the layered architecture and object placement rules in `references/spring-boot-backend.md`.

Load that reference before generating, changing, refactoring, or reviewing Java Spring Boot backend code.

## Full Stack Docker Compose Deployment

When working on Java backend, Python worker, frontend, Docker Compose deployment, CI secret injection, `.env` wiring, shared runtime environment, or Nginx public routing, load `references/full-stack-docker-compose-ci-deployment.md` if it is present.

## Python Code Organization

When working on Python code, follow the package and module organization rules in `references/python-code-organization.md`.

Load that reference before generating, changing, refactoring, or reviewing Python code.

## Android Kotlin Compose

When working on Android code, follow the Kotlin, Jetpack Compose, UI data mapping, and error handling rules in `references/android-kotlin-compose.md`.

Load that reference before generating, changing, refactoring, or reviewing Android code.

When implementing Android UI from a screenshot, mockup, design image, or existing product UI, also load `references/android-ui-implementation-from-design.md`.

When designing or generating Android product UI without a fixed design image, also load `references/android-product-ui-design.md`.
