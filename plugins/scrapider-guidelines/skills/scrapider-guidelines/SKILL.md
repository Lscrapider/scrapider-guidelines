---
name: scrapider-guidelines
description: Use when a coding agent writes, edits, refactors, debugs, or reviews code under Scrapider engineering rules, especially for Spring Boot, Python, Android Kotlin, Jetpack Compose, or standards-conformance reviews. Also use when the user asks to create, update, or rebuild a project's AGENTS.md with project-specific workflows and optional capability recommendations.
---

# Scrapider Guidelines

Apply Scrapider engineering rules to implementation, refactoring, debugging, and review.

## Working Principles

### Think Before Coding

Establish the actual problem, assumptions, and relevant existing behavior before choosing a
solution. Surface ambiguity and meaningful tradeoffs; explain a simpler alternative when warranted.

### Simplicity First

Choose the simplest complete implementation of the current requirement. Prefer equivalent existing
capabilities and add structure only for a present responsibility. Judge complexity by what a
maintainer must understand, not line count alone.

### Surgical Changes

Keep each change traceable to the request and preserve unrelated behavior and style. Include the
caller migration and cleanup needed to make the scoped change complete; leave unrelated cleanup
outside it.

### Goal-Driven Execution

Define observable success criteria and verify them. For coordinated or dependent multi-step work,
connect each step to a useful check. Continue from evidence until the criteria are met or a concrete
blocker is reported; an attempted edit or a passing unrelated check does not establish completion.

## Coding Workflow

### Before Coding

- Read applicable project instructions and inspect the implementation, callers, and tests. Explicit
  user direction and project instructions take precedence over this skill. Use the routing below
  to load the relevant general and technology-specific rules before making implementation choices.
- Determine problem ownership and repair scope using [Code Design and Changes](references/shared/code-design-and-changes.md#problem-ownership-and-repair-scope)
  before choosing a local fix, a shared fix, or an extraction.
- State material assumptions or uncertainty, the goal, approach, expected files, and verification
  method. For small, clear changes, one or two sentences suffice; do not impose a formal plan.
- If plausible interpretations lead to different outcomes, explain the alternatives and tradeoffs.
  Resolve uncertainty from available evidence and existing authorization. Ask a focused question
  only when unresolved ambiguity changes business behavior, interfaces, data shape, credentials,
  deployment topology, persistence, security, or irreversible actions. Do not ask again about an
  established decision; resolve local implementation mechanics from the repository.
- Push back on unnecessary complexity, broad rewrites, or risky choices with concrete reasons and
  a simpler alternative. Address each material risk. If the user still chooses that direction,
  follow it and record the objection once in delivery rather than silently shrinking the scope.

### Implement or Refactor

- Before adding a check or abstraction, decide whether current requirements and the applicable routed
  rules justify it.
- Refactor only when requested or needed for the change. Identify a concrete improvement before
  editing; a refactoring request can finish with no code changes when the current implementation
  already satisfies it. Compare the relevant behavior before and after, and verify meaningful
  intermediate steps when later changes depend on them.

### Debug

- Reproduce or localize the failure before proposing a fix. Trace the smallest path that explains
  the symptom and fix the responsible implementation.
- Use a focused reproduction or failing test when permitted and useful. Verify the failing path
  and nearby affected behavior; do not hide a broken invariant with a fallback or special case.

### Verify and Deliver

- Follow project instructions and user authorization for test creation; this skill grants no
  permission to create tests. When allowed and useful, follow existing patterns and cover changed
  behavior. Otherwise use existing tests or a reproducible, static, build, integration, interface,
  or manual check.
- For CI, Docker, deployment, or environment changes, prefer relevant build/artifact checks,
  `docker compose config`, image builds, startup/log inspection, or HTTP checks to new unit tests.
- Use failed checks to correct the cause and re-check affected behavior. Stop when the narrowest
  useful verification covers the change; broaden checks only for a concrete uncovered risk.
- Comments, documentation, and mechanical renames or constant extractions need no runtime test
  when they leave behavior and external contracts unchanged. Changed constant values, defaults,
  serialized names, or configuration can change behavior and need the relevant verification.
- Assess the need for a standards review using the routing below and its applicability rules.
- Report what changed, where, why, and verification commands and results. Distinguish an implemented
  change from verified behavior. If a check cannot run or still fails, state the blocker and what
  remains unproven; an explanation of missing evidence is not proof of success. If no code changed,
  say so.

## Reference Routing

Load the applicable shared rules below and the relevant technology entry point. Read that baseline
first, then follow its matching scenario routes; each entry point owns its downstream references and
their loading conditions. Do not preload whole directories or every linked file. Package maps and
examples do not require creating layers the project does not need. Select all routes whose
behavior, contracts, or responsibilities the task affects, including through callers; selection is
not limited to words in the request or edited filenames. Inspect uncertain connections narrowly and
load the relevant reference if its applicability remains unresolved. Reassess when the scope expands.
For reviews and investigations, select routes for the existing behavior under examination; a diff
or proposed edit is not required.
Use the same routing for implementation, refactoring, debugging, and review; reuse already-read,
unchanged material.

### General Engineering and Task Rules

| Category | When to load | Reference |
| --- | --- | --- |
| Code design and changes | All code and build/runtime configuration implementation, refactoring, debugging, and review; covers scope, contracts, reuse, responsibilities, and replacement | [Code Design and Changes](references/shared/code-design-and-changes.md) |
| Validation and errors | Input contracts, validation, defensive branches, or exceptions, including ordinary HTTP/MQ implementation and decisions about internal checks | [Validation and Boundaries](references/shared/validation-and-boundaries.md) |
| Standards review | An explicit review request, or changes affecting other modules, public/business contracts, data, integrations, concurrency, deployment, or coordinated behavior across files; the reference defines triggers, exclusions, and dispatch | [Standards Review](references/shared/standards-review.md) |
| Git operations | Before any Git state mutation, including staging, committing, branching, or pushing; the reference defines authorization and commit format | [Git Operations and Commits](references/shared/git-commits.md) |
| Inspection script library | Assigned standards review of Java changes: load before deciding IDE/MCP availability; also load for explicit library maintenance, adaptation, or installation. | [Inspection Scripts](references/shared/inspection-scripts.md) |
| Project AGENTS.md | Creating, completing, updating, or rebuilding a project's `AGENTS.md`. | [AGENTS.md Authoring](references/project/agents-md-authoring.md) |

### Technology Entry Points

| Entry point | When to load |
| --- | --- |
| [Spring Boot Backend](references/java/spring-boot-backend.md) | Any Java Spring Boot implementation, refactoring, debugging, or review, including Maven build and module work. |
| [Python Code Organization](references/python/python-code-organization.md) | Any Python implementation, refactoring, debugging, or review. |
| [Android Kotlin Compose](references/android/android-kotlin-compose.md) | Any Android implementation or review, including UI implementation and product UI design. |

For plain Java outside Spring Boot, use the repository's own conventions.
