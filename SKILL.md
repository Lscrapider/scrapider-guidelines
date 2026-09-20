---
name: scrapider-guidelines
description: Use when a coding agent writes, edits, refactors, debugs, or reviews code under Scrapider engineering rules, especially for Spring Boot, Python, Android Kotlin, Jetpack Compose, or standards-conformance reviews.
---

# Scrapider Guidelines

Use this skill to keep implementation and review simple, minimally scoped, aligned with the project,
safe for existing contracts, and verifiable. Load only the references routed by the task.

## Before Coding

Before editing code, state material assumptions or uncertainty and tell the user:

1. The problem or goal being solved.
2. The implementation approach.
3. The files expected to change.
4. The verification method.

For small, unambiguous changes, state these in one or two sentences instead of a full plan.

If the request is ambiguous in a way that changes public interfaces, data shape, credentials, deployment topology, persistence, security posture, or irreversible operations, stop and ask. Do not guess silently.

If the ambiguity only affects implementation mechanics and the existing code, Jenkinsfile, Compose file, Dockerfile, or project documentation already shows a working local pattern, follow the existing pattern and make the smallest direct change.

When multiple solutions are possible, prefer the simpler one and briefly explain the tradeoff. Push
back on unnecessary complexity, broad rewrites, vague requirements, or risky designs before
implementing them. A pushback states the concrete technical reason and the simpler alternative, and
covers every high-risk item named in the request. When the user still insists, implement as
requested, record the objection once in the delivery, and neither silently shrink the change nor
refuse to deliver.

## Engineering Discipline

### Simplicity First

- Write the minimum code that solves the current request.
- Do not add speculative features, configuration, extension points, or abstractions.
- Add defensive checks only under the validation and error-boundary rules below.
- If a change grows large, re-check whether a smaller direct change would solve the same problem.
- For existing CI, Docker, or deployment changes, prefer the minimum migration path that preserves the current runtime model, routing model, output mode, service names, credentials, and deployment topology.
- Do not introduce a new deployment architecture, output format, proxy layer, runtime mode, or packaging model unless the existing runtime cannot consume the requested artifact or the user explicitly asks for that broader migration.

### Reuse Before Rebuild

- Before adding a helper, wrapper, validator, or query, search the repository, language standard library, framework, and approved dependencies for an equivalent capability.
- Reuse an existing capability only when its behavior and contract are equivalent. Do not replace a stricter domain validation with a generic utility that changes its semantics.
- Do not load a full collection and filter it in memory when an existing precise query expresses the same ordering and conditions. Keep the full read when the caller truly needs the full collection or equivalence cannot be proven.

### No Accidental Layers

- Add an architectural layer, module, class, or call-path wrapper only when it owns a distinct domain contract or technical boundary: domain invariant, transaction, authorization, state transition, conversion boundary, external-system boundary, or stable multi-step operation.
- A small private function or method inside an existing file is not an architectural layer. Extract one only when it removes verified duplication or materially improves readability without creating a new cross-file abstraction.
- Do not introduce a layer merely to rename or forward a call, perform a trivial null/empty check, or make a short method look organized. Avoid wrapper chains such as `A -> B -> C` when the intermediate layer owns no contract.
- During refactoring, trace callers of replaced code. Migrate callers and remove wrappers, imports, and files that no longer own behavior.
- Preserve a wrapper only for a meaningful contract or required public compatibility. If removal would break external callers, explain the impact instead of adding another layer around it.
- Do not split a cohesive implementation into additional files solely for theoretical extensibility.

### Surgical Changes

- Touch only files and lines that directly serve the user's request.
- Match the repository's existing structure, style, helpers, naming, and dependency choices.
- Do not refactor adjacent code just because it could be better.
- Do not reformat unrelated code.
- Remove imports, variables, functions, classes, or config that the current change made unused.
- Mention unrelated dead code or design issues instead of fixing them opportunistically.

### Validation and Error Boundaries

Apply these rules while writing the first implementation, not only during cleanup or review.
Use the guarantees already provided by actual callers, types, deserialization, frameworks, and
storage. Add a check only for a business-required condition that is not already guaranteed and
needs meaningful handling at this point. Think about necessity before choosing validation syntax;
do not output a per-field rationale or checklist unless requested.

- Consume the fields the operation needs. Unrelated extra JSON fields are not errors; a DTO,
  field list, or example payload does not establish a closed schema.
- Do not hand-write field-set gates with `TSet`, `Set`, `keySet`, set differences, field counts,
  or equivalent loops and chains of `if`. Do not introduce unknown-field rejection merely for
  strictness. Normal business uses of sets and conditionals are unaffected.
- Trust established guarantees along the current call path. Do not repeat upstream checks in
  downstream functions or invent hypothetical callers to justify defensive code.
- Prefer direct use and the existing exception boundary when a pre-check adds no required
  recovery, business distinction, or protection against a concrete harmful effect. Do not replace
  redundant branches with equally redundant annotations, assertions, validators, or wrappers.
- Keep necessary authorization, business invariants, and atomic state-transition protection.
  Being an external input, crossing a layer, or mentioning security is not by itself evidence
  that an additional check is needed.

Before implementing, changing, refactoring, or reviewing input contracts, validation, defensive
branches, or exception handling, load
[Validation and Boundaries](references/shared/validation-and-boundaries.md). This includes normal
HTTP/MQ implementation and decisions about whether internal functions need checks, even when the
user has not explicitly requested validation work. The reference supplies the detailed decision
rules and examples for both implementation and review.

### Business Contract Preservation

- Treat existing default values, thresholds, enums, and strategy parameters as business contracts.
- Prefer reusing existing constants, configuration properties, or enums for new functionality.
- Do not redefine equivalent parameters under new names.
- When consolidating a duplicated value into a single definition, first search the whole system — including components in other languages or repositories that speak the same protocol (for example a Python worker or an Android app) — for independently defined copies of the same value. Migrate only the copies the task covers, keep the remaining copies' values byte-identical, and name the cross-language linkage and its synchronization responsibility in the delivery.
- Do not hard-code magic numbers or magic values when an existing constant, config value, or enum already represents the concept.
- Do not change existing defaults without explicit confirmation. When the request itself already confirms the change, execute it directly; blocking a confirmed change with another round of confirmation is a violation, not caution.
- When a new requirement conflicts with an existing default or threshold, stop and clarify whether the change is global, scenario-specific, or still expected to reuse the existing contract.
- If a different value is required, explain the reason and wait for confirmation before changing or introducing it.

### Verification Mindset

- Define success criteria before or during implementation.
- For bug fixes, prefer a focused reproduction or failing test before changing behavior.
- Follow repository instructions such as `AGENTS.md` and explicit user direction to determine whether new tests may be created. This skill does not independently grant or deny test-creation permission.
- When new tests are allowed or required, follow the repository's existing test pattern when one exists, and add or update focused tests for behavior changes.
- When new tests are not allowed or not appropriate, use existing tests plus the closest reproducible, static, build, integration, interface, or manual verification available, and report remaining risk.
- For configuration, CI, Docker, deployment, or environment changes, prefer operational verification such as build commands, generated artifact checks, `docker compose config`, Docker image builds, container startup, logs, and curl checks. Do not default to adding unit tests for deployment-only changes.
- Verification is sufficient once the narrowest check that covers the changed behavior passes; run broader checks only when the change's risk warrants them.
- Comment-, rename-, constant-, and documentation-only changes need no runtime verification; say so instead of inventing one, and do not cite a full test-suite run as their verification.
- Do not run extra checks merely to make the report look complete.
- Do not claim success without command output, test results, or a clear explanation of why verification could not run.

## Coding Workflow

When implementing:

1. Inspect the existing code and tests before choosing an approach.
2. Make the smallest coherent change.
3. Search project-local, language-standard-library, framework, and approved-library capabilities before adding a helper, wrapper, validator, or query.
4. Apply the validation and error-boundary rules while writing code; decide whether a check is needed before adding it, rather than adding defenses first and removing them in review.
5. Follow the repository's test-creation policy, then run the narrowest verification that proves the changed behavior.

## Debugging Workflow

When fixing bugs:

1. Reproduce or localize the failure before proposing a fix.
2. Identify the smallest code path that explains the symptom.
3. Fix the cause, not only the visible symptom.
4. Follow the repository's test-creation policy; add a regression test when required or permitted, otherwise use the closest reproducible regression check.
5. Verify the failing path and any nearby affected path.

## Refactoring Rules

Refactor only when the user asks for it or when it is required to make the requested change cleanly.

- Keep behavior unchanged unless behavior change is explicitly requested.
- Preserve public APIs, request/response shapes, and database semantics unless told otherwise.
- Move code in small steps and verify after meaningful changes.
- Do not introduce a new abstraction for one call site.
- Keep handlers, strategies, and stage-specific classes separate when they represent distinct current business semantics, dispatch identities, state keys, or lifecycle stages, even if their current bodies are identical.
- When identical handlers could be either intentional semantic entry points or accidental duplication, decide from code, documentation, tests, and call sites first. Do not ask when existing evidence or explicit user direction already decides.
- When the choice affects only local implementation mechanics, preserve the current structure or make the smallest direct change without asking.
- Ask one focused question only when the choice would materially affect registered or dispatched identities, lifecycle stages, public APIs, business contracts, or a repository-established extension boundary. Briefly explain the concrete keep-versus-merge tradeoff before asking.
- When shared behavior is stable and extraction actually reduces complexity, extract only the smallest shared implementation. Keep distinct semantic entry points and do not add another dispatch or wrapper layer.

## Standards Review

A standards review is a full pass over the applicable rules, so it is expensive. Decide whether that cost is
worth it by judging the change's blast radius yourself; the question is not "did code change" but
"can a mistake here escape this file and reach other code, data, or systems".

Run one review per completed user request when the change crosses such a boundary:

- Module, service, or team boundary: another module or package calls, imports, or depends on the changed code; same-package callers do not count.
- Public or business contract: HTTP endpoints, request/response shapes, published APIs, error codes, enums, defaults, or thresholds that other code depends on.
- Persistence or transactions: schema, SQL semantics, transaction boundaries, or data migration.
- Messaging or middleware: MQ producers or consumers, scheduled jobs, Redis, auth, or config integration.
- Concurrency, authorization, or data-integrity-sensitive logic.
- CI, Docker, Compose, or deployment topology.
- Coordinated multi-file or multi-module changes that must stay consistent with each other.

Do not review when no such boundary is crossed, for example:

- A few lines inside a single file with no interface change, such as a log message, typo, exception text, or local refactor.
- A rename, comment, Javadoc, or documentation-only change.
- A new private helper, constant, or branch with a single caller in the same file.
- A one- or two-file fix whose files are not called from outside their module and cross none of the boundaries above.
- A purely additive public surface with no existing callers; review it once only when it establishes defaults, error styles, or naming conventions that later code will copy.

If the user explicitly asks for a review, review regardless of size. If no listed boundary applies
but the change is still risky for another concrete reason, review it anyway. Do not stretch a listed
boundary to justify reviewing a low-risk change, and do not skip a review by ignoring a boundary
that is clearly crossed. Subtask boundaries inside a larger request are not review points. After
applying review fixes, re-check only the fixed points; do not run a second full review.

When a review runs, load and follow
`references/shared/standards-review.md` as the sole review procedure and output contract. The review
must cover every applicable rule in this `SKILL.md` and every reference routed by the actual scope,
not a remembered shortlist.

- If assigned the `scrapider-standards-reviewer` role, perform the review directly and never
  delegate; this is a leaf role.
- Otherwise, when the host supports subagents, dispatch exactly one read-only standards reviewer.
  This skill does not launch additional review roles; other review workflows are governed elsewhere.
- Without subagent support, perform the same review directly and state that it could not be isolated.
- Give the reviewer the repository path, exact scope or diff range, relevant user request, and known
  project-instruction paths. Do not provide conclusions or expected findings.
- Return the report to the primary agent. The primary agent decides how to use it with other
  evidence or reviewers; this skill does not arbitrate conflicts.

## Communication Rules

Be direct and specific:

- Say what changed, where it changed, and why.
- Name verification commands and results.
- If verification fails, report the failure and the next useful step.
- Mention verification you could not run only when it leaves a material risk uncovered.
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

## Reference Routing

For plain Java code outside a Spring Boot backend, follow the repository's own conventions and contributing guide.

### Spring Boot Backend

When working in any Spring Boot backend, follow the package responsibilities, layered architecture, and object placement rules in `references/java/spring-boot-backend.md`.

Load that reference before generating, changing, refactoring, or reviewing Java Spring Boot backend code.

When the repository has multiple Maven modules, or the task involves parent and aggregator POMs, module responsibilities, cross-module dependencies, module splitting, or deciding which module owns a Java file or Spring configuration, also load `references/java/spring-boot-multi-module.md`. Treat the single-module package map as the responsibility baseline; the multi-module reference explains how those responsibilities are distributed across modules without overriding the repository's established architecture.

### Local Full Stack Docker Compose Deployment

When creating, adopting, changing, or reviewing the user's local deployment profile that combines
Jenkins host builds, runtime-only Docker images, Docker Compose, CI secret files, and optional Nginx
routing, load `references/shared/full-stack-docker-compose-ci-deployment.md` if present. Do not load
it solely because another deployment uses Docker, Compose, CI, or Nginx. This reference is
user-local; its absence must not block the task.

### Python Code Organization

When working on Python code, follow the package and module organization rules in `references/python/python-code-organization.md`.

Load that reference before generating, changing, refactoring, or reviewing Python code.

### Android Kotlin Compose

When working on Android code, follow the Kotlin, Jetpack Compose, UI data mapping, and error handling rules in `references/android/android-kotlin-compose.md`.

Load that reference before generating, changing, refactoring, or reviewing Android code.

When implementing Android UI from a screenshot, mockup, design image, or existing product UI, also load `references/android/android-ui-implementation-from-design.md`.

When designing or generating Android product UI without a fixed design image, also load `references/android/android-product-ui-design.md`.
