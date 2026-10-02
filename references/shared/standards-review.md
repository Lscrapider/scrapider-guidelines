# Scrapider Standards Review

Use this reference to decide when to run a standards review and to perform that review against
`scrapider-guidelines`. The primary agent handles applicability and dispatch; the assigned reviewer
follows the read-only procedure and output contract below.

## Applicability — Primary Agent

A standards review is a full pass over the applicable rules. Run it once per completed user request
when explicitly requested, or when behavior or contracts change across any of these boundaries:

- Production code in another module, package, or team calls, imports, or depends on the changed
  code; test callers and same-package callers alone do not trigger a review.
- Public or business contracts: endpoints, request/response shapes, published APIs, error codes,
  enums, defaults, or thresholds depended on by other code.
- Persistence, SQL semantics, transactions, or data migration.
- MQ, scheduled jobs, Redis, authorization, configuration integration, concurrency, or data integrity.
- CI, Docker, Compose, or deployment topology.
- Coordinated changes across files or modules that must stay consistent.

Skip automatic review for documentation, comments, mechanical renames, and local implementation
changes that affect none of these boundaries. A filename or the presence of tests does not by itself
establish such an impact. A purely additive public surface with no existing callers needs
review only when it establishes defaults, error styles, or naming conventions later code will copy.
Another concrete risk can warrant review; do not invent one for a low-risk change. Subtasks are not
separate review points. After fixes, re-check the fixed points without repeating the full review.

## Dispatch — Primary Agent

- If assigned the `scrapider-standards-reviewer` role, perform the review directly; never delegate.
- Otherwise, dispatch exactly one read-only standards reviewer when subagents are available and
  permitted for the request. This skill launches no other review roles. If delegation is unavailable
  or disallowed, review directly and state that it was performed by the primary agent.
- Supply the repository path, exact scope or diff range, relevant request, known project instruction
  paths, and the exact skill root used for the task. Do not supply conclusions or expected findings.
- Use this document as the sole standards-review procedure and output contract. The reviewer returns
  the report to the primary agent, who evaluates it alongside other evidence.

## Reviewer Scope and Boundaries

- Act as the leaf `scrapider-standards-reviewer`; never dispatch another agent or reviewer.
- Check every applicable rule in the current `SKILL.md` and its routed references, including
  [Code Design and Changes](code-design-and-changes.md) for all code and configuration reviews.
- Do not perform a general business-logic, correctness, syntax, security, or test review. Report
  those concerns only when they prove an explicit Scrapider rule violation, such as Business
  Contracts, Validation and Boundaries, or Coding Workflow.
- Remain read-only. Do not modify files, Git state, dependencies, services, issues, or external
  systems. Review large scopes in multiple passes yourself.

## Inputs

Use the provided repository path, review scope or diff range, relevant user request, project
instructions, and supplied skill root; use the installed copy only when no root was supplied.
Resolve an unclear scope from the request and repository state only when the result is unambiguous;
otherwise return `Blocked` instead of guessing.

## Review Procedure

1. Read the applicable project instructions.
2. Read the entire current `scrapider-guidelines/SKILL.md`.
3. Determine the technologies and task types in scope. Load only the reference documents selected
   by the current `SKILL.md` routing rules for that scope; do not load unrelated references.
4. Inspect the complete scoped diff, including relevant untracked files, plus only the surrounding
   code, callers, tests, configuration, and documentation needed to verify a potential violation.
5. Build an internal checklist from the normative instructions in the loaded material that apply
   to the changed file types and behaviors. Classify each as applicable, not applicable, satisfied,
   violated, or not verifiable; never substitute a remembered shortlist.
6. Re-check potential findings against context and explicit project exceptions. Unless the user
   requested a broader audit, report only issues introduced or exposed by the scoped change.
7. Return the report using the contract below. Do not decide how the primary agent should reconcile
   it with other evidence or reviewers.

Use shell commands only for non-mutating inspection, such as `git status`, `git diff`, `git show`,
searches, artifact-free parsers, or existing validation-output inspection. Do not run commands that
rewrite files, generate artifacts, install dependencies, start services, or mutate Git state.

## Finding Calibration

- **Critical**: credible risk of data loss, security failure, irreversible damage, or a broken
  public or business contract.
- **Important**: a confirmed violation that should be corrected before acceptance, including
  unjustified layers, misplaced responsibilities, duplicated boundaries, or missing verification
  that the Coding Workflow rules require.
- **Minor**: a confirmed, non-blocking consistency or maintainability violation.

A preference is not a finding. Every finding needs a specific rule, code evidence, and concrete
impact.

For design and replacement work, apply [Code Design and Changes](code-design-and-changes.md).
Trace affected callers and old entry points; report compatibility aliases, fallback paths, and
forwarding glue that keep a superseded implementation alive. Do not propose compatibility code as
the remediation. Distinguish actual business or external-system boundaries from speculative layers.

For input contracts, validation, defensive branches, and exception handling, apply
[Validation and Boundaries](validation-and-boundaries.md), including its review guidance. Judge
checks by necessity and existing guarantees, not their count or syntax. Do not request additional
checks merely for strictness or completeness; identify the actual missing guarantee and required
behavior. Likewise, report redundant checks when the scoped code establishes their redundancy.

## Output Contract

Start directly with findings. If there are no confirmed findings, say so explicitly.

### Findings

Group findings under `Critical`, `Important`, and `Minor`. For each finding include:

- `file:line`;
- violated section or reference rule;
- concrete evidence and impact;
- the smallest compliant remediation, when not obvious.

### Questions or Unverifiable Rules

List only missing evidence that could materially change a finding. Do not turn uncertainty into a
violation.

### Coverage

Name the reviewed scope, project instructions read, every Scrapider reference loaded, core areas
checked, and any applicable area that could not be verified.

### Verdict

Return one of:

- `Pass`: no confirmed Scrapider rule violations;
- `With fixes`: one or more confirmed violations;
- `Blocked`: the skill, scope, or required project evidence was unavailable.
