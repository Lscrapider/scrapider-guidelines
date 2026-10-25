# Optional Capability Recommendations

Load this reference when selecting workflows for a project's `AGENTS.md`. When code retrieval is in
scope, choose one retrieval profile alongside any selected coding, planning, or UI capabilities.
Do not install tools, change configuration, build indexes, or create files beyond the target
`AGENTS.md` during authoring.

Recommend capability configurations that can be used without paid licenses or subscriptions.
Check that the required operations are available in the free configuration; exclude paid-only
plugins and paid feature dependencies from proposals.
Keep licensing and pricing labels out of generated `AGENTS.md` entries. Use capability names such as
`Serena (LSP)` and describe their responsibilities and routing.

Apply the [selection and entry rules](agents-md-authoring.md#recommend-and-let-the-author-choose)
when proposing or recording the combinations below.
After selection, use the [scenario examples](examples/capability-entries.md) for individual
capabilities, adopted combinations, and alternatives with overlapping responsibilities.

- [Present workflow combinations](#present-workflow-combinations)
- [Capability types](#capability-types)
- [Code retrieval recipes](#code-retrieval-recipes)
- [Optional retrieval and output layers](#optional-retrieval-and-output-layers)
- [Planning and execution](#planning-and-execution)
- [Design and implementation](#design-and-implementation)

## Present Workflow Combinations

For an author without established choices, start with suitable task scenarios and named combinations.
Use these profiles as a starting point, not a fixed menu or a requirement to adopt every layer:

| Scenario | Candidate combination and workflow | Selection boundary |
| --- | --- | --- |
| Localized changes | **Lightweight:** native search and targeted reads + the project's coding guidance + native planning and verification. | Use when paths or identifiers locate the affected implementation; trace remaining callers with scoped searches. |
| Cross-module changes | **Relationship-aware:** Codegraph + native retrieval + the chosen coding guidance. | Select under the [retrieval-profile conditions](#select-one-retrieval-profile). |
| Unresolved symbol or dependency facts | Use the open IDE or local dependency artifacts alongside the selected retrieval tools and coding guidance. | Retain a Serena route only when explicitly selected under the [Serena conditions](#serena-with-language-servers). |

Name the actual coding guidance in each proposal. Preserve existing project standards; offer Scrapider
Guidelines as an optional coding layer for code changes or standards review, without inferring adoption
from this authoring task. Keep retrieval and coding responsibilities separate.

Add planning and UI capabilities only under their conditions in the sections below; selecting a
retrieval profile does not require either layer. Do not turn the profiles into mandatory tiers.

## Capability Types

Classify a capability by its exposed operations; do not add workflow steps solely for its packaging.

| Type | Responsibility | Examples |
| --- | --- | --- |
| MCP service | Callable operations that retrieve information or perform actions. | Codegraph, Serena, a semantic search service. |
| Skill | Task-specific instructions with their own applicability conditions. | Scrapider Guidelines, frontend-design, impeccable. |
| Plugin | A host integration or distribution that may include skills, tools, commands, or hooks. | Host-specific bundles of skills and MCP tools. |
| CLI | A program invoked through the shell; integrations may wrap it. | RTK, OpenSpec, Codegraph's CLI. |

Verify callable operations in the target host before relying on a capability. Unless the target host
is specified, retain stable capability names and resolve invocation aliases at use time; do not
replace them with the authoring host's plugin prefixes, model routing, or
configuration paths. For an explicitly selected host, use its verified exposed names. Verify command
availability on that host before declaring it unavailable. Keep version-specific setup and launcher
procedures in the selected skill rather than copying them into `AGENTS.md`.

## Code Retrieval Recipes

Recommend one profile for the project's recurring tasks; reuse a confirmed selection. Do not require
every tool in the selected profile on every task.

### Select One Retrieval Profile

Recommend the least extensive profile that covers the project's recurring queries. Preserve an
explicitly chosen profile, while keeping unused tools optional within it.
During authoring, check capability fit against documented operations, language support, and available
project evidence; this does not require installing a tool or running a new benchmark.

| Profile | Select when | Use within this profile; skip when unnecessary |
| --- | --- | --- |
| **Direct or fallback path — no additional retrieval service** | Paths or identifiers are known, changes stay within one module, or no suitable indexed service is available. | Use local search and targeted reads. Describe this route within the selected capability's boundary or fallback, not as a separate tool entry. |
| **Relationship-aware — Codegraph + native retrieval** | Cross-file flows, dependencies, and impact analysis recur, and a suitable graph index is available. | Use Codegraph for those relationship questions and reuse returned source. Use native retrieval for literals, configuration, documentation, current edits, and uncovered evidence. Where the tool contract permits, directly read a known local implementation; skip broad graph exploration when no relationship question remains. |
| **Author-selected variant — Codegraph + Serena (LSP) + native retrieval** | The author explicitly selected Serena and its [support conditions](#serena-with-language-servers) are met. | Keep the Codegraph/native division above; use Serena only for unresolved symbol queries. Do not add this variant by default. |

Do not select a profile solely from repository size or same-named methods. Skip optional tools once
the question is answered without changing the project's selected profile.

Honor actual host and tool routing requirements. If an exposed contract requires Codegraph first for
code questions or edits, preserve that condition and its allowed fallbacks; a known path alone does
not override it. Do not impose that host-specific requirement on implementations without it. Tool
selection does not authorize indexing, installation, semantic edits, or configuration changes.

Write the selected profile using the [capability entry format](agents-md-authoring.md#capability-entry-format).
Keep necessary local fallback behavior explicit without creating an entry for native search itself.

### Retrieve Enough Evidence with Fewer Repeated Calls

Call supporting tools only for unresolved evidence; do not impose one fixed sequence. Expand to a
complete file when the selected body omits declarations or initialization that affect the answer.

Skip discovery for a known target. For an unknown target, narrow by path or symbol before retrieving
its body. Expand to callers, types, or configuration only for a concrete evidence gap; complete
truncated bodies before drawing conclusions about their behavior.

Reuse existing indexes and complete returned source; do not retrieve the same body through another
tool without a freshness or completeness gap.

Choose retrieval granularity for the question. Retrieve several needed methods together when they
sit in a small, cohesive class or file, rather than through separate discovery and body requests. Batch
independent reads whose targets are already known; keep queries that depend on earlier results
sequential.
Before stopping, check that the answer addresses the requested facts with adequate evidence; do not
expand a bounded question into an exhaustive account of every surrounding implementation detail.

For a handoff, identify the unresolved question, known path or symbol, and source already obtained.
Across message or language boundaries, verify the relevant producer, routing and consumer evidence;
do not report a path as absent solely from a missing graph edge.

### Codegraph Query Scope

Confirm the Codegraph package and exposed operations before use. Scope queries to the affected
symbols and paths, verify returned targets, and query only missing evidence.
Resolve ambiguous same-name relationships against receiver declarations and call-site source; use a
precise-symbol tool when that evidence still leaves the binding unresolved.
Use native retrieval for an unavailable index, uncovered content, or stale results, within the exposed
contract. Respond to actual coverage gaps rather than rebuilding or rechecking the index on every task.

### Serena with language servers

Do not recommend Serena by default. Preserve it only when the author explicitly selects it for
recurring definition, implementation, or reference queries that remain
unresolved by the graph and native evidence. Verify the project root, required toolchains, query
support, and host memory capacity before adoption. Prefer an already-open IDE or local dependency
artifacts when they resolve that gap.

Skip Serena for answered questions, ordinary text or configuration searches, unsupported languages,
or insufficient host memory; use native retrieval for the remaining evidence. If a query returns no
matches or excessive results, refine its path or symbol scope before retrying.

### External library docs service (context7)

A docs service such as context7 supplies external library version and API facts.

- **Trigger only** when adopting or upgrading a dependency, including forked or renamed libraries, or when a
  version-sensitive API fact remains unanswered by repository precedent and local dependency artifacts.
- For dependency selection or an upgrade, verify the current stable version and compatibility with the project's
  framework line before selecting a version.
- Do not trigger for day-to-day implementation the repository already demonstrates. Use local
  dependency jars for signature checks only; take version selection and compatibility conclusions
  from the docs service.
- Verify the resolved library identity against its Maven/npm coordinates; reject a mismatched result.
- On conflict, the project's declared version (`pom.xml` / `package.json` / lockfile) wins over any
  doc snippet. Verify language-level correctness by compilation. If the service is unavailable or
  lacks version-matched evidence, consult official library documentation and report any unresolved fact.

### IDE tool surface on an open project

A built-in IDE MCP surface (such as a JetBrains IDE server) applies only to the project the IDE has
open, and every call must explicitly target that project. Within that scope it serves the edit loop,
not general retrieval: **retrieval questions stay on the selected retrieval profile; ordinary file
creation and text edits stay with native tools. Semantic rename and post-edit formatting use the IDE
in the scenarios below.**

- **Semantic rename**: use IDE refactoring to rename variables, fields, methods, classes, or other
  code symbols, especially when bindings are ambiguous or reflection strings may refer to them.
  For an unambiguous rename, native edits with a reference sweep are acceptable when the project's
  tool-routing contract permits them. Verify reflective strings separately.
- **Per-file quick diagnostics**: check the edited file for diagnostics; retain model review for
  design and security findings.
- **Incremental single-file build**: check compilation after a file edit when the IDE supports it.
- **Third-party symbol facts**: inspect the resolved artifact and declaration when dependency
  versions or signatures are ambiguous.
- **Debugger sessions**: inspect a reproduced failure when static evidence leaves its runtime state
  or cause unresolved.
- **Reformat after writing**: use the IDE formatter after native file edits as a best-effort finishing
  step. Follow project conventions, including existing files when no code-style configuration exists;
  on formatter failure, finish natively.

Do not use this surface for large-file slicing.

Scripted standards checks belong to [scrapider-standards-reviewer](../../agents/scrapider-standards-reviewer.md).
Its role definition loads the inspection library and handles coverage and candidate confirmation;
do not turn that internal mechanism into a main-agent procedure in project `AGENTS.md`.

### Alternatives for a Specific Unmet Need

Preserve the author's existing or explicitly selected alternatives. A confirmed standalone Serena
(LSP) + native profile can serve precise symbol work without a graph
service. Codebase Memory + native reads is another possible primary profile for outlines, graph
queries, and symbol source. Evaluate either as a replacement profile, not an extra default layer.

Use `DeusData/codebase-memory-mcp` only as a selected retrieval profile. Complete truncated source
before interpreting it, and resolve uncovered content or empty results with native evidence.
Do not add it to duplicate an existing graph service.

Use a selected repomap when module or symbol paths are unknown and existing outlines do not locate
them. Skip it for known targets or sufficient outlines.

## Optional Retrieval and Output Layers

Add a layer only when its selection condition below is met; do not adopt the table as a single stack.

| Optional combination | Select when | Boundaries and fallback |
| --- | --- | --- |
| Chosen retrieval profile + RTK CLI command filters | Supported Git, build, test, or log output is truncated or repeatedly exceeds the host's output limit. | Use a verified host integration or supported CLI operation; otherwise use bounded native commands. Keep raw output available when filtering obscures evidence. Do not use it to filter MCP schemas. |
| Structural retrieval + one semantic search service | The task is described conceptually and identifiers or structural queries do not locate the implementation. A service such as `@teknologika/mcp-codebase-search` can supply candidate code chunks. | Confirm package identity, language coverage, index freshness, and host support. Verify candidates against current code; trace relationships only for unresolved flow questions. Reuse existing semantic search rather than adding duplicate indexes. |
| Chosen retrieval recipe + an AST symbol extractor | A symbol body remains unlocated by existing tools in a supported language; evaluate `syphon1c/code-scale-mcp`. | Verify package identity and extraction support. Prefer existing symbol tools or bounded native reads when they suffice. |
| Selected MCP services behind a schema gateway | The host eagerly loads selected services' tool definitions and lacks deferred discovery; evaluate MCP Slim (`dopatools/mcp-slim`). | Verify discovery and proxy support, including which downstream connections the gateway owns. Retain native MCP connections when proxying is unsupported. |
| Chosen retrieval profile + an external library docs service (such as context7) | Apply the [external library docs conditions](#external-library-docs-service-context7). | Keep version selection, local signature checks, identity verification, and conflict handling within that section's boundaries. |
| Chosen retrieval profile + an IDE tool surface (such as a JetBrains IDE built-in MCP) | Apply the [IDE operation conditions](#ide-tool-surface-on-an-open-project). | Preserve that section's project scope, native-edit boundary, and manual fallback. |

Before replacing or stacking services, verify package identity, required operations, and overlapping
responsibilities; leave unverified candidates out of the active workflow.

## Planning and Execution

Handle localized changes directly. For multi-step work that needs coordination or depends on earlier
results, use a proportionate plan with observable completion checks, then execute and update it from
evidence. Select one planning approach rather than loading several frameworks for the same task.

| Approach | Type and selection condition | Boundary and fallback |
| --- | --- | --- |
| Native planning + execution checklist | Host capability or concise instructions for coordinated steps completed in one session. | Use without a planning plugin when the host supports the required steps and checks. |
| Planning with Files | Skill for tasks that must resume plans, findings, and progress across sessions. | Verify host support before relying on plugin or hook integrations. |
| OpenSpec | CLI plus skills or commands when the project requires reviewable specifications, implementation tasks, and decision archives. | Skip for a short execution plan without a specification or traceability requirement. |
| Superpowers writing-plans + executing-plans | Two skills used on demand when the user selects them and host and project policy permit them. | Other Superpowers features remain off unless the user explicitly requests them. This limit does not require Superpowers for every plan or prohibit other capabilities. Replace older blanket assignments of design, review, or verification to Superpowers throughout the document. Preserve applicable model and project restrictions. |

Offer full lifecycle frameworks such as GitHub Spec Kit only when the project explicitly requires
specification, planning, implementation, and decision archival in one workflow.

During AGENTS.md authoring, describe the selected workflow for later tasks. Do not run its initializer,
invoke its implementation workflow, or create its plan/specification files.

## Design and Implementation

| Optional combination | Suitable scenario and division of responsibility | Boundary and fallback |
| --- | --- | --- |
| frontend-design → impeccable + Scrapider Guidelines | New web UI or an explicitly requested new visual direction. frontend-design establishes direction; impeccable improves hierarchy, interaction states, accessibility, consistency, and polish; Scrapider guides code changes. | Use each part only for work it contributes. Preserve the selected direction during implementation; use the project's design system and available guidance when a skill is absent. |
| impeccable + Scrapider Guidelines | Local visual or UX improvements to an existing web UI. impeccable guides the UI refinement; Scrapider guides the implementation. | Inherit the existing design language. A new visual direction belongs to the preceding route. Nonvisual logic and API changes do not trigger UI skills. |
| compose-expert + Scrapider Android guidance | Implementation or review of Compose APIs or UI in an Android project, when compose-expert is available. | Keep the project's architecture and design constraints. If the specialist is unavailable, use the existing [Android Kotlin and Compose guidance](../android/android-kotlin-compose.md). |

Match the exposed skill or plugin before use; write only the project's selected routes into `AGENTS.md`.
