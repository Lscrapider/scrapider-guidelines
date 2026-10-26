# Planning and Collaboration Examples

Use these examples only for planning workflows or collaboration roles the author selects for
`AGENTS.md`. They describe routing and handoffs; they do not install or register a framework or role,
guarantee host availability, or authorize execution. User instructions and applicable model, host, and project
policies govern each selection.

## Planning

When a project selects a planning workflow, keep one canonical owner for each formal specification
and task plan. OpenSpec, GitHub Spec Kit, and the explicitly selected Superpowers plan pair must
not share ownership of the same artifacts. Do not assume they interoperate. A second
capability may complement the selected owner only when its responsibility is distinct and all work
follows one authoritative task record.

### Cross-session evidence with one task plan

- **Planning with Files** — Preserve investigation records across sessions.
  - **Scenario:** Findings, decisions, open questions, or progress must
    survive context changes.
  - **Boundary:** It owns durable evidence and session progress. Keep the single canonical task
    list with the selected plan owner; do not duplicate it in these notes.

### Formal change specification

- **OpenSpec** — Maintain a traceable change specification, implementation task list, and decisions.
  - **Scenario:** The project requires reviewable requirements, implementation tasks, and formal
    decisions, and the author selected OpenSpec.
  - **Boundary:** OpenSpec owns the canonical specification, task list, and formal decisions.
    Implementation follows those artifacts without creating a competing plan or changing their
    authority.
  - **Fallback:** Preserve existing specifications if OpenSpec is unavailable; confirm a different
    specification owner only when continuing requires replacement.

### Formal change plus separate cross-session evidence

- **OpenSpec** — Own the formal change record.
  - **Scenario:** A formal OpenSpec change is required and substantial investigation or session notes
    must persist beyond the specification.
  - **Boundary:** OpenSpec owns requirements, the task list, and formal decisions; it remains
    the only specification and task-plan owner.

- **Planning with Files** — Record evidence and progress outside the formal change specification.
  - **Scenario:** The cross-session notes are independently useful and are not already covered by
    OpenSpec.
  - **Boundary:** It owns investigation evidence and session progress only; it does not create
    duplicate requirements, decisions, or implementation tasks.

### Explicitly selected Superpowers plan-to-execution workflow

- **Superpowers `writing-plans`** — Produce the plan in an explicitly selected Superpowers pair.
  - **Scenario:** The author selected the pair, the host exposes both skills, and user, model, host, and
    project policies permit their use.
  - **Boundary:** `writing-plans` produces the one authoritative plan consumed by
    `executing-plans`; no other framework owns a competing specification or task list.

- **Superpowers `executing-plans`** — Execute against the pair's selected plan.
  - **Scenario:** The project selected the pair and work is ready to proceed against its
    authoritative plan.
  - **Boundary:** `executing-plans` follows the same plan owned by `writing-plans`. Add
    another planning capability only for a distinct responsibility while retaining that one
    authoritative task record.

### Project-required full specification-led workflow

- **GitHub Spec Kit** — Maintain a linked specification, implementation plan, and decision record.
  - **Scenario:** The project explicitly requires this full lifecycle as its canonical workflow.
  - **Boundary:** GitHub Spec Kit owns the formal specification and task plan. Implementation
    follows those artifacts; another framework must not own a competing specification or task list.
  - **Fallback:** Preserve existing artifacts if the selected components are unavailable; confirm a
    different specification owner only when continuing requires replacement.

## Collaboration

Delegate responsibilities that can be separated and handed off clearly. The primary agent owns task
scope, decisions, integration, and final acceptance. Each role returns findings or results to the
primary agent within the delegation boundaries of its definition.

### Role responsibilities

- **`scrapider-explorer`** — Read-only investigation of code paths, callers, existing behavior, or
  other assigned evidence. Return supporting evidence and distinguish observed facts from inferences.
- **`scrapider-developer`** — Implement the clearly assigned scope and return the change with
  verification evidence and unresolved questions.
- **`scrapider-code-reviewer`** — Read-only review of functional correctness, requirements,
  behavior, and regressions. Return concrete findings and evidence; do not edit or accept the change.
- **`scrapider-standards-reviewer`** — Read-only review of applicable engineering standards when
  explicitly requested or required by project policy. Report concrete rule violations; do not perform
  the functional review, edit, or accept the change.
- **`scrapider-generalist`** — Handle assigned work outside investigation, implementation, and review
  responsibilities without duplicating a specialist's scope.

### Handoffs

For investigation followed by implementation, the explorer returns evidence, affected paths, and
open questions to the primary agent. The primary agent decides scope and assigns bounded implementation
work; the developer returns the change and verification evidence for integration.

When both functional and standards reviews are required, assign the code reviewer to behavior and
regressions and the standards reviewer to applicable rule conformance. The primary agent integrates
their findings and owns fixes and acceptance; the two reviews keep their scopes distinct.
