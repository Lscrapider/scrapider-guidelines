# Planning and Collaboration Examples

Use these optional entries when a project author chooses planning or collaboration capabilities
for `AGENTS.md`. Add only routes that match the project's work. These examples do not install or
register a framework or role, guarantee host availability, or authorize execution. User instructions
and applicable model, host, and project policies govern every selection. If a selected role is not
available, the primary agent assumes its responsibility.

## Planning

The primary agent uses native planning and execution by default for localized work and one-session
tasks; this baseline does not require another tool. Keep one canonical owner for each formal
specification and task plan. OpenSpec, GitHub Spec Kit, and the explicitly selected Superpowers plan
pair are alternatives when they would own the same artifacts. Do not assume they interoperate. A
second capability may complement the selected owner only when its role is distinct and all work
follows one authoritative task record.

### Cross-session notes with one execution plan

- **Planning with Files** — A persistent work record for investigations and tasks that continue
  across sessions.
  - **Scenario:** Findings, decisions, open questions, or progress must survive context changes, and
    implementation also needs one explicit task list.
  - **Boundary:** Planning with Files owns durable evidence and progress. The primary agent's native
    plan is the single execution task list; do not copy that list into a competing checklist.
  - **Fallback:** If Planning with Files is unavailable, preserve existing records and continue with
    already permitted tools where possible. Ask the author to change the selected workflow only when
    a new capability is necessary to meet the continuity requirement.

### Formal change specification with native implementation

- **OpenSpec** — A formal change-specification workflow for reviewable requirements, implementation
  tasks, and decisions.
  - **Scenario:** The project requires a traceable change specification and implementation tasks,
    while code can be implemented with native tools or an assigned developer.
  - **Boundary:** OpenSpec owns the canonical specification, task list, and formal decisions. Native
    implementation follows those artifacts without creating another plan or changing their
    authority.
  - **Fallback:** If OpenSpec is unavailable, preserve existing specifications and tasks and
    continue permitted implementation where possible. Ask the author about a replacement only if
    continuing requires a different specification owner or a new framework.

### Formal change plus separate cross-session evidence

- **OpenSpec** — The authority for the formal change requirements, implementation tasks, and
  decisions.
  - **Scenario:** A formal OpenSpec change is required and substantial investigation or session
    notes must persist beyond the specification.
  - **Boundary:** OpenSpec owns requirements, task list, and formal decisions; it remains the only
    specification and task-plan owner in this combination.
  - **Fallback:** If OpenSpec is unavailable, preserve current artifacts and continue permitted work
    where possible; ask the author before changing the selected specification workflow.

- **Planning with Files** — A separate record for evidence and progress that do not belong in the
  formal change specification.
  - **Scenario:** The cross-session notes are independently useful and are not already covered by
    the OpenSpec artifacts.
  - **Boundary:** Planning with Files owns only investigation evidence and session progress; it does
    not create duplicate requirements, decisions, or implementation tasks.
  - **Fallback:** If it is unavailable or adds no distinct record, keep OpenSpec as the sole plan
    and preserve necessary notes in their current approved location.

### Explicitly selected Superpowers plan-to-execution workflow

- **Superpowers `writing-plans`** — The planning skill in an explicitly selected Superpowers
  plan-to-execution pair.
  - **Scenario:** The author selects the pair, the host exposes both skills, and user, model, host,
    and project policies permit their use.
  - **Boundary:** `writing-plans` produces the one authoritative plan consumed by `executing-plans`;
    no other framework owns a competing specification or task list.
  - **Fallback:** If unavailable or policy disallows it, do not invoke it. Preserve existing
    permitted plans and continue with allowed work where possible; ask the author to change plan
    ownership only if continued work requires it.

- **Superpowers `executing-plans`** — The execution skill paired with the selected `writing-plans`
  capability.
  - **Scenario:** The project has selected the pair and the work is ready to proceed against its
    authoritative plan.
  - **Boundary:** `executing-plans` follows the same plan owned by `writing-plans`; use
    another-stage capability only when responsibilities are explicit and the same authoritative task
    record remains available. If that record cannot be shared, choose one plan owner instead of
    assuming compatibility.
  - **Fallback:** If unavailable or policy disallows it, do not invoke it. Continue only under an
    existing permitted plan; ask the author before adopting a different execution workflow.

### Project-required full specification-led workflow

- **GitHub Spec Kit** — A full workflow for projects that require linked specification, planning,
  implementation, and decision records.
  - **Scenario:** The project explicitly requires the specification-led lifecycle as its canonical
    workflow.
  - **Boundary:** GitHub Spec Kit owns the formal specification and task plan. Native code
    implementation may follow those artifacts, but a second framework must not own a competing
    specification or task list.
  - **Fallback:** If unavailable, preserve existing artifacts and continue permitted implementation
    where possible. Ask the author to approve a replacement only if a different specification owner
    is necessary; otherwise report the remaining capability gap without adopting another framework.

## Collaboration

The primary agent works directly by default for localized or tightly coupled tasks; native edits and
verification are not separate roles or tools to adopt. Delegate only separable responsibilities with
a clear handoff. The primary agent owns task scope, decisions, integration, and final acceptance. If
a selected role is unavailable, the primary agent takes over that responsibility.

### Read-only investigation

- **`scrapider-explorer`** — A read-only investigation role that returns evidence for an assigned
  question.
  - **Scenario:** Code paths, existing behavior, callers, or other evidence are unclear before a
    decision or implementation.
  - **Boundary:** The explorer investigates without changing files or project state and
    distinguishes observed facts from inferences. The primary agent owns decisions and any follow-up
    changes.
  - **Fallback:** The primary agent investigates directly if the role is unavailable.

### Primary agent with investigation and implementation

- **`scrapider-explorer`** — A read-only investigation role that returns evidence for an assigned
  question.
  - **Scenario:** Code paths, existing behavior, callers, or other evidence are unclear before
    implementation.
  - **Boundary:** The explorer investigates without changing files or project state and
    distinguishes observed facts from inferences. The primary agent or developer owns decisions and
    edits.
  - **Fallback:** The primary agent investigates directly if the role is unavailable.

- **`scrapider-developer`** — An implementation role for a clearly assigned change and its
  verification.
  - **Scenario:** The implementation scope can be assigned independently after or alongside
    investigation without conflicting edits.
  - **Boundary:** The developer changes only the assigned scope and returns verification evidence.
    The primary agent owns sequencing, integration, cross-scope decisions, and acceptance.
  - **Fallback:** The primary agent implements the assigned change if the role is unavailable.

### Implementation with a known scope

- **`scrapider-developer`** — An implementation role for a clearly bounded code change.
  - **Scenario:** The requested implementation is understood and can be assigned without separate
    investigation.
  - **Boundary:** The developer owns the assigned changes and their evidence; the primary agent
    retains integration and acceptance.
  - **Fallback:** The primary agent implements the change if the role is unavailable.

### Functional review only

- **`scrapider-code-reviewer`** — A read-only reviewer for functional correctness and regressions.
  - **Scenario:** A change needs an independent review against its request and established behavior.
  - **Boundary:** The reviewer covers functional behavior; standards compliance belongs to an
    already selected standards-review route. The primary agent evaluates findings and owns fixes and
    acceptance.
  - **Skip:** Skip this role for a standards-only assignment; the primary agent or an
    already-selected standards reviewer handles that scope.
  - **Fallback:** The primary agent performs the functional review if the role is unavailable.

### Standards review only

- **`scrapider-standards-reviewer`** — A read-only reviewer of changes against applicable
  engineering standards.
  - **Scenario:** Standards review is explicitly requested or the project's review policy triggers
    it.
  - **Boundary:** The reviewer reports only concrete violations of applicable rules under the
    selected standards-review procedure; functional correctness remains with the primary agent or an
    already selected code reviewer. The standards reviewer does not edit or accept changes.
  - **Skip:** Skip this role for a functional-only review when no standards-review trigger applies;
    the primary agent or an already-selected functional reviewer handles that scope.
  - **Fallback:** The primary agent performs the standards review under the same rules and reporting
    contract if the role is unavailable.

### Work outside specialist responsibilities

- **`scrapider-generalist`** — A generalist role for tasks not well covered by the specialist roles.
  - **Scenario:** The assigned work falls outside investigation, implementation, functional review,
    and standards review responsibilities.
  - **Boundary:** The generalist handles the uncovered scope without duplicating a specialist's
    assignment. The primary agent retains integration and acceptance.
  - **Fallback:** The primary agent handles the work if the role is unavailable.

### Functional and standards reviews together

- **`scrapider-code-reviewer`** — The read-only functional review for requirements, behavior, and
  regressions.
  - **Scenario:** The change needs both behavior review and review against engineering standards.
  - **Boundary:** The code reviewer covers functional correctness and regressions, not standards
    compliance. The primary agent integrates review results and owns fixes and acceptance.
  - **Skip:** Omit this role when no functional review is in scope; retain it when the request or
    review policy requires behavior review.
  - **Fallback:** The primary agent performs the functional review if the role is unavailable.

- **`scrapider-standards-reviewer`** — The read-only review against the project's engineering rules.
  - **Scenario:** The same change also requires an explicit standards pass or meets the
    standards-review trigger.
  - **Boundary:** The standards reviewer checks applicable rule violations, not general functional
    correctness. The primary agent integrates review results and owns fixes and acceptance.
  - **Skip:** Omit this role when no standards-review trigger applies; retain it when explicitly
    requested or required by project policy.
  - **Fallback:** The primary agent performs the standards review under the same rules and reporting
    contract if the role is unavailable.
