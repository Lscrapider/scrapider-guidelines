---
name: scrapider-guidelines
description: Use when a coding agent writes, edits, refactors, debugs, or reviews code under Scrapider engineering rules, especially for Spring Boot, Python, Android Kotlin, Jetpack Compose, or standards-conformance reviews.
---

# Scrapider Guidelines

Guide implementation and review under the project's engineering rules: understand the request,
place behavior at the right responsibility boundary, preserve business contracts within the
requested scope, and verify the result. Keep the design and change as simple as that work permits.

This entry point defines the working principles, coding workflow, and reference routing. Detailed
rules are grouped by the decisions they govern and apply during both implementation and review.

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

Define observable success criteria and verify them. For meaningful multi-step work, connect each
step to a useful check. Continue from evidence until the criteria are met or a concrete blocker is
reported; an attempted edit or a passing unrelated check does not establish completion.

## Coding Workflow

### Before Coding

- Read applicable project instructions and inspect the implementation, callers, and tests. Explicit
  user direction and project instructions take precedence over this skill. Use the routing below
  to load the relevant general and technology-specific rules before making implementation choices.
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

- Search for equivalent project, standard-library, framework, or approved-library capabilities
  before adding helpers, wrappers, validators, or queries.
- Make the smallest coherent change under the applicable rules. Preserve the business behavior the
  task does not change, keep responsibilities clear, and migrate affected callers when replacing code.
- Decide whether a check or abstraction is needed before adding it. Apply the validation and error
  rules during implementation, rather than adding defenses first and removing them in review.
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

Load the categories relevant to the actual task. General rules and technology-specific rules work
together; package maps and examples do not require creating layers the project does not need.

### General Engineering and Task Rules

| Category | When to load | Reference |
| --- | --- | --- |
| Code design and changes | All code and build/runtime configuration implementation, refactoring, debugging, and review; covers scope, contracts, reuse, responsibilities, and replacement | [Code Design and Changes](references/shared/code-design-and-changes.md) |
| Validation and errors | Input contracts, validation, defensive branches, or exceptions, including ordinary HTTP/MQ implementation and decisions about internal checks | [Validation and Boundaries](references/shared/validation-and-boundaries.md) |
| Standards review | An explicit review request, or changes affecting other modules, public/business contracts, data, integrations, concurrency, deployment, or coordinated behavior across files; the reference defines triggers, exclusions, and dispatch | [Standards Review](references/shared/standards-review.md) |
| Git operations | Before any Git state mutation, including staging, committing, branching, or pushing; the reference defines authorization and commit format | [Git Operations and Commits](references/shared/git-commits.md) |

### Technology and Scenario Rules

| Technology or task | When to load | Reference |
| --- | --- | --- |
| Spring Boot backend | Any Java Spring Boot implementation or review | [Spring Boot Backend](references/java/spring-boot-backend.md) |
| Maven multi-module | Multiple Maven modules, parent/aggregator POMs, module ownership, dependencies, splitting, or Spring configuration placement; load with the backend reference | [Spring Boot Multi-Module](references/java/spring-boot-multi-module.md) |
| Python | Any Python implementation or review | [Python Code Organization](references/python/python-code-organization.md) |
| Android Kotlin / Compose | Any Android implementation or review | [Android Kotlin Compose](references/android/android-kotlin-compose.md) |
| Android UI from a design | A screenshot, mockup, design image, or existing product UI is the implementation source; load with the Android reference | [Android UI Implementation](references/android/android-ui-implementation-from-design.md) |
| Android product UI design | Designing or generating Android product UI without a fixed design image; load with the Android reference | [Android Product UI Design](references/android/android-product-ui-design.md) |
| Local full-stack deployment | The user's Jenkins host-build, runtime-only Docker, Compose, CI secret-file, and optional Nginx profile is in scope | [Local Deployment](references/shared/full-stack-docker-compose-ci-deployment.md) |

For plain Java outside Spring Boot, use the repository's own conventions. The local deployment
reference is optional and user-local: load it only if present and this profile applies. Its absence
does not block work, and generic Docker/CI work does not trigger it.
