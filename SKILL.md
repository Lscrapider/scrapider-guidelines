---
name: scrapier-guidelines
description: Apply Scrapier's personal coding habits and Karpathy-style engineering discipline when Codex writes, edits, refactors, debugs, or reviews code. Use for code tasks that need upfront implementation logic, explicit assumptions, minimal surgical changes, verification criteria, and Spring Boot backend layering conventions.
---

# Scrapier Guidelines

Use this skill to make Codex follow Scrapier's personal coding style: state the plan before editing, keep changes minimal, preserve existing project style, respect Spring Boot layering, and verify the result before claiming success.

## Operating Rules

Before editing code, tell the user:

1. The problem or goal being solved.
2. The implementation approach.
3. The files expected to change.
4. The verification method.

If the request is ambiguous or has multiple reasonable interpretations, stop and ask. Do not guess silently.

## Karpathy-Style Discipline

- State assumptions and tradeoffs before implementation.
- Prefer the minimum code that solves the requested problem.
- Avoid speculative abstractions, unused flexibility, and "future-proof" code.
- Touch only files and lines that directly serve the request.
- Match the repository's existing structure, style, helpers, and naming.
- Remove unused code introduced by the current change.
- Mention unrelated issues instead of fixing them opportunistically.
- Define verifiable success criteria and loop until they are checked.

## Coding Workflow

When implementing:

1. Inspect the existing code and tests before choosing an approach.
2. Make the smallest coherent change.
3. Use project-local helpers and framework conventions before adding new utilities.
4. Add or update tests when behavior changes or risk is non-trivial.
5. Run the most focused useful verification command available.

When finishing, report:

1. What actually changed.
2. The verification command and result.
3. Any verification that could not be completed, with the reason.

## Spring Boot Backend

When working in any Spring Boot backend, follow the layered architecture and object placement rules in `references/spring-boot-backend.md`.

Load that reference before generating, changing, refactoring, or reviewing Java Spring Boot backend code.
