# Optional Capability Recommendations

Load this reference when selecting workflows for a project's `AGENTS.md`. When code retrieval is in
scope, choose one retrieval profile alongside any selected coding, planning, or UI capabilities.
Each profile is independently usable. These recommendations do not authorize installing tools,
changing configuration, building indexes, or creating files beyond the target `AGENTS.md`.

Recommend capability configurations that can be used without paid licenses or subscriptions.
Check that the required operations are available in the free configuration; exclude paid-only
plugins and paid feature dependencies from proposals.
Keep licensing and pricing labels out of generated `AGENTS.md` entries. Use capability names such as
`Serena (LSP)` and describe their responsibilities and routing.

Offer the author a small set of relevant named options with a recommendation, purpose, and tradeoffs.
Reuse choices already made; let the author select a proposal or supply an alternative before treating
an unresolved option as adopted. Write only selected responsibilities into `AGENTS.md`, not the full
comparison or observations about the current installation and index state.

- [Present workflow combinations](#present-workflow-combinations)
- [Capability types](#capability-types)
- [Code retrieval recipes](#code-retrieval-recipes)
- [Optional retrieval and output layers](#optional-retrieval-and-output-layers)
- [Planning and execution](#planning-and-execution)
- [Design and implementation](#design-and-implementation)

## Present Workflow Combinations

For an author without established choices, start with suitable task scenarios and named combinations.
Use these profiles as a starting point, not a fixed menu or a requirement to adopt every layer:

| Scenario | Candidate combination and workflow | Main tradeoff |
| --- | --- | --- |
| Localized changes | **Lightweight:** native search and targeted reads + the project's coding guidance + proportionate native planning and verification. Locate the relevant implementation, make the scoped change, and check the affected behavior. | No additional retrieval service; broad relationship questions may need more manual navigation. |
| Cross-module changes | **Relationship-aware:** Codegraph + native retrieval + the chosen coding guidance. Use source and relationships to establish impact, inspect gaps, then implement and verify across affected boundaries. | An available, suitable graph index helps; retain native fallback. |
| Verified symbol gaps across modules | **Precise-symbol enhancement (conditional):** Codegraph + Serena (LSP) + native retrieval + the chosen coding guidance. Codegraph establishes structure; Serena handles unresolved symbol/reference questions; native tools cover other evidence. | Verify that Serena can resolve the relevant gap. Extra capabilities do not establish lower Token use or faster completion. Serena runs separate language-server processes with substantial resident memory; weigh that cost against alternatives already present in the workflow (an already-open IDE tool surface, local dependency jars) before adopting it. |

Name the actual coding guidance in each proposal. Preserve existing project standards; offer Scrapider
Guidelines as an optional coding layer when appropriate, without inferring adoption from this authoring
task. A retrieval MCP and a coding skill are complementary layers, not substitutes.

Adapt the profile with the planning, design, or collaboration route the task needs. For example, a web
UI workflow can add frontend-design followed by impeccable for a new direction, or impeccable for
existing UI work. Explain that addition's role and cost rather than enabling UI skills for every task.
For plans, compare only relevant alternatives from the planning section; the selected retrieval profile
does not require a planning plugin. Do not turn these profiles into progressively mandatory tiers.

The recommendation conversation can explain alternatives. The resulting `AGENTS.md` keeps only the
chosen names, triggers, responsibilities, and necessary handoffs; omit the comparison and each skill's
internal loading or execution procedure.

## Capability Types

Classify a capability by what it provides and how the target host exposes it. A plugin can package
skills and MCP connections; its packaging is not an additional workflow step.

| Type | Responsibility | Examples |
| --- | --- | --- |
| MCP service | Callable operations that retrieve information or perform actions. | Codegraph, Serena, a semantic search service. |
| Skill | Instructions loaded when a task needs their workflow or expertise. | Scrapider Guidelines, frontend-design, impeccable. |
| Plugin | A host integration or distribution that may include skills, tools, commands, or hooks. | Host-specific bundles of skills and MCP tools. |
| CLI | A program invoked through the shell; integrations may wrap it. | RTK, OpenSpec, Codegraph's CLI. |

Confirm availability in the target host. A named product, installed package, or bundled role definition
is not evidence that the active agent can call it. Preserve confirmed user selections; keep unselected
alternatives out of the generated project rules. The authoring host does not establish the project's
future host. Unless the target host is specified, retain stable capability names and resolve invocation
aliases at use time; do not replace them with the authoring host's plugin prefixes, model routing, or
configuration paths. For an explicitly selected host, use its verified exposed names. A command missing
from one catalog or skill document is not proof that it was removed from another host. Keep version-
specific setup and launcher procedures in the selected skill rather than copying them into `AGENTS.md`.

## Code Retrieval Recipes

Optimize for sufficient evidence with less irrelevant source and fewer repeated model turns. Recommend
one profile for the project's recurring needs; reuse a confirmed selection. Keep only that profile in
`AGENTS.md`, grouped with the other selected capabilities. Do not copy the three-profile menu into the
project file. A profile defines available responsibilities, not tools that every task must call.

### Select One Retrieval Profile

Recommend the least extensive profile that covers the project's observed needs. Offer the third profile
when a concrete symbol or reference gap remains and Serena's relevant language capability is verified;
do not infer that gap from repository size or the presence of same-named methods alone. Preserve an
explicitly chosen profile, while keeping unused tools optional within it.
During authoring, check capability fit against documented operations, language support, and available
project evidence; this does not require installing a tool or running a new benchmark.

| Profile | Select when | Use within this profile; skip when unnecessary |
| --- | --- | --- |
| **Lightweight — native search + targeted reads** | Work is usually local, paths or identifiers are known, or no suitable indexed service is available. This is a complete option for repositories of any size. | Use filename and literal search, then read the complete relevant implementation. Trace callers with scoped searches when needed. No MCP call is required by this profile; reconsider the choice only if repeated relationship work makes native navigation inefficient. |
| **Relationship-aware — Codegraph + native retrieval** | Cross-file flows, dependencies, and impact analysis recur, and a suitable graph index is available. | Use Codegraph for those relationship questions and reuse returned source. Use native retrieval for literals, configuration, documentation, current edits, and uncovered evidence. Where the tool contract permits, directly read a known local implementation; skip broad graph exploration when no relationship question remains. |
| **Precise-symbol enhancement — Codegraph + Serena (LSP) + native retrieval** | Recurring symbol/reference questions remain unresolved by the current graph and native evidence, Serena is verified to cover those queries, and the host can afford Serena's separate language-server processes and resident index memory. Prefer an already-running IDE tool surface or local dependency artifacts when either resolves the same gap before adding Serena. | Use Codegraph and native retrieval as above. Use Serena for an unresolved definition, implementation, or precise reference question in a supported language. Skip Serena when source or relationships already answer the question, for ordinary text/configuration searches, when its language backend cannot resolve the target, or when its resident memory cost outweighs the gap it closes. |

A large repository may benefit from the third profile, but size does not require it. Select it for a
recurring semantic-navigation need and usable language support. A simple task in that profile may use
only native retrieval; an answered graph query may need no Serena call. These are conditional routes
within the selected profile, not a switch to another project-wide policy.

Honor actual host and tool routing requirements. If an exposed contract requires Codegraph first for
code questions or edits, preserve that condition and its allowed fallbacks; a known path alone does
not override it. Do not impose that host-specific requirement on implementations without it. Tool
selection does not authorize indexing, installation, semantic edits, or configuration changes.

When writing the selected profile into `AGENTS.md`, retain a short grouped entry with each included
tool's trigger and responsibility, when to skip it, and the shared handoff or fallback. Give distinct
responsibilities readable subitems or table line breaks. Keep native retrieval explicit even in an
enhanced profile. Omit unselected tools, parameter tutorials, benchmark results, and fixed savings claims.

### Retrieve Enough Evidence with Fewer Repeated Calls

Start from the question and available capabilities. Call supporting tools only for unresolved evidence;
do not impose one fixed sequence. Expand context when needed for correctness, including complete files
when appropriate.

Where the service separates discovery from source retrieval, first narrow by path and symbol using
outlines, signatures, or bounded search results. Then request the complete relevant function, method,
or class, expanding to callers, types, or configuration only for a concrete evidence gap. Skip broad
orientation when the target is already known. Follow pagination or narrow the scope when output is
truncated; a partial snippet is not a complete implementation.

Reuse existing indexes and already-returned source. A service that supplies both discovery and source
does not need a second MCP just to retrieve the same code. If the selected primary tool returns bounded
source in one call, reuse that result instead of forcing an extra outline-and-read sequence.

Choose retrieval granularity for the question. Several needed methods in a small, cohesive class or
file may be cheaper to retrieve together than through separate discovery and body requests. Batch
independent reads whose targets are already known; keep queries that depend on earlier results
sequential. Judge the whole task's evidence and model turns, not the shortest individual response.
Before stopping, check that the answer addresses the requested facts with adequate evidence; do not
expand a bounded question into an exhaustive account of every surrounding implementation detail.

For a handoff, identify the unresolved question, known path or symbol, and source already obtained.
Do not ask the second tool to repeat a complete body solely because both tools are in the profile.
Across message or language boundaries, verify the relevant producer, routing and consumer evidence;
a missing graph edge does not prove that the path is absent.

### Codegraph Query Scope

Codegraph implementations and exposed tools can differ; confirm the package and actual schema rather
than transferring behavior from a similarly named project. A service exposing `codegraph_explore` can
include source, call paths, and impact information. Reuse complete returned source. Where `maxFiles`
is exposed, treat it as a source-file selection limit, not a Token budget or a guarantee that the target
definition is selected. Use task-specific terms and a proportionate scope, verify the returned targets,
and query only missing evidence. Do not invent a caller-controlled `maxTokens` parameter.
Resolve ambiguous same-name relationships against receiver declarations and call-site source; use a
precise-symbol tool when that evidence still leaves the binding unresolved.
Use native retrieval for an unavailable index, uncovered content, or stale results, within the exposed
contract. Respond to actual coverage gaps rather than rebuilding or rechecking the index on every task.

### Serena with language servers

Use Serena's language-server (LSP) backend. The coding agent connects to Serena MCP, which uses
supported language servers for the activated project's languages. Confirm the project root and the
required language-server dependencies and toolchains before relying on symbol results. Serena runs
as separate processes with their own indexes: treat that resident memory as part of the cost of
adopting it, and when an already-open IDE tool surface or local dependency artifacts resolve the
same gap, prefer them before adding Serena.

Choose language servers whose required operations are available without paid features. Language
coverage and semantic operations vary by server; verify support for the actual query or edit, and
use native retrieval for unsupported content or remaining evidence gaps.

With the classic interface, use a scoped `find_symbol` or a file's `get_symbols_overview` for discovery
when needed, then request the selected body with `include_body=true`. For a known target, request that
body directly. Use references only for a remaining relationship question. `relative_path` narrows the
search; `max_matches` is a refinement threshold, not pagination. An empty or oversized result calls for
checking the path or symbol pattern, not repeated broad queries. Follow the installed schema's limits.

### IDE tool surface on an open project

A built-in IDE MCP surface (such as a JetBrains IDE server) applies only to the project the IDE has
open, and every call must explicitly target that project. Within that scope it serves the edit loop,
not retrieval:

- **Semantic rename**: run it through the IDE when the symbol is not unique or reflection strings may
  reference it; a manual rename plus a text-search sweep remains cheaper for unique symbols.
- **Per-file quick diagnostics**: a seconds-level regression check after editing one file; it does not
  replace model review for design or security findings.
- **Incremental single-file build**: sub-second feedback after an edit, versus a full CLI build.
- **Third-party symbol facts**: the IDE resolves the exact artifact version and declaration the
  classpath actually uses — an arbiter when versions are ambiguous.
- **Custom inspection scripts**: mechanical coding standards (layering bans, required javadoc, import
  rules) can be captured as reusable IDE inspection scripts for whole-project scans, leaving semantic
  judgments to the standards review.

- **Reformat after writing**: one call normalizes generic layout (indentation, spacing, braces,
  annotation line breaks). Without a project code-style config it falls back to IDE defaults, so
  project-specific conventions (comment templates, import grouping) still follow existing files.
  Treat it as a best-effort finishing step — it can time out on a busy IDE backend. File creation
  and edits otherwise stay with native tools.

Retrieval questions stay on the selected retrieval profile; this surface measured quality parity with
graph-plus-native retrieval at higher token and call counts. Large-file slicing through this surface
is unreliable on a busy backend; do not route work around it.

### Alternatives for a Specific Unmet Need

The three profiles are the default proposals, not a ban on the author's existing or preferred tools.
A confirmed standalone Serena (LSP) + native profile can serve precise symbol work without a graph
service. Codebase Memory + native reads is another possible primary profile for outlines, graph
queries, and symbol source. Evaluate either as a replacement profile, not an extra default layer.

For `DeusData/codebase-memory-mcp`, narrow with `search_graph` or `get_file_outline`, then use the returned
qualified name with `get_code_snippet(source_mode="full")`. Follow source continuation to complete the
needed body; `auto` may return an outline for a large container. Use bounded `trace_path` only for a
relationship gap. Its `max_output_tokens` is a sizing hint, not measured model usage. Best-effort index
coverage and empty results do not establish that an implementation is absent; use native evidence for
uncovered content. Do not add it merely to duplicate an existing graph service.

A selected repomap can provide a budgeted initial outline inside a profile when no existing tool
supplies adequate orientation. Skip it for known modules or an already sufficient outline; it does not
require a separate mandatory retrieval sequence.

## Optional Retrieval and Output Layers

Add a layer only for an unmet need. These layers address different costs and do not form progressively
better deployment tiers.

| Optional combination | When it helps | Boundaries and fallback |
| --- | --- | --- |
| Chosen retrieval profile + RTK CLI command filters | Relevant source is already targeted, but supported Git, build, test, or log commands produce excessive output. Prefer the OSS command-filter edition when it meets the need. | This filters shell output, not MCP schemas. Use a verified host integration or supported CLI operation; otherwise use bounded native commands. Keep raw output available when filtering obscures evidence. RTK's reported token estimates are not host-measured usage. |
| Structural retrieval + one semantic search service | The task is described conceptually and identifiers or structural queries do not locate the implementation. A service such as `@teknologika/mcp-codebase-search` can supply candidate code chunks. | Confirm package identity, language coverage, index freshness, and host support. Verify candidate matches against current code and follow relationships as needed. Reuse existing semantic search rather than adding duplicate indexes. |
| Chosen retrieval recipe + an AST symbol extractor | A supported language needs symbol-level source extraction that the existing tools do not provide. `syphon1c/code-scale-mcp` is a candidate to evaluate. | AST extraction and vector search serve different purposes. Do not assume a similarly named npm package is the same project. Prefer existing symbol tools or bounded native reads when they already suffice. |
| Selected MCP services behind a schema gateway | The host loads many tool definitions and does not already provide adequate deferred tool discovery. MCP Slim (`dopatools/mcp-slim`) is one candidate. | Confirm the gateway's actual discovery and proxy support. With a proxy design, the host uses the gateway and it manages downstream connections; listing every server beside it does not establish proxying. Use the host's normal MCP connections when a gateway adds no benefit. |
| Chosen retrieval profile + an external library docs service (such as context7) | **Trigger only when** adopting a new dependency or a recently forked/renamed library, or facing a version-sensitive API fact with no local evidence: confirm the current stable version and its compatibility with the project's framework line (for example, which starter adapter matches the Spring Boot version). | Do not query for implementation the repository already demonstrates. Local dependency jars can verify API signatures but reflect only cached, possibly outdated versions. Verify the resolved library identity against its coordinates — same-name libraries exist on docs services. Language-level pitfalls (checked exceptions and the like) still need compile verification, not docs. |
| Chosen retrieval profile + an IDE tool surface (such as a JetBrains IDE built-in MCP) | IDE-exclusive operations on the IDE-open project inside the edit loop: semantic rename, per-file quick diagnostics, incremental single-file build, debugger sessions, third-party symbol facts, and mechanical coding standards captured as reusable inspection scripts. | Do not add this layer for retrieval questions: measured quality parity with graph-plus-native retrieval at higher token and call counts. File creation and edits stay with native tools; a post-write reformat is a best-effort finishing step. Large-file slicing is unreliable on a busy backend. Calls must explicitly target the IDE-open project, and renames or builds apply only there. Deep design or security findings still need model review, not IDE inspections. |

An integrated context engine may already combine outlines, symbols, graphs, and semantic retrieval.
Confirm the exact implementation and overlapping responsibilities before replacing or stacking services.
A nickname such as CCE does not establish a package identity, token-budget API, or drop-in replacement
for repomap and a symbol extractor. Leave unresolved candidates out of the active workflow.

Keep source retrieval, command output, tool schemas, and repeated calls distinct when assessing cost.
Schema deferral alone does not reduce source payloads; a tool-side cache hit alone does not prove fewer
model-input tokens. Do not promise a fixed savings percentage or label a larger stack the cheapest.
For quantitative comparisons, use host-reported usage and distinguish total input, cached input,
uncached input and output. Include retries and model turns; distinguish code actually retrieved from
facts correctly explained in the answer. Keep model, task, host presentation and quality expectations
comparable, and report any unresolved differences instead of treating a single run as a backend ranking.
Record which tools were actually used. An available but unused tool cannot explain an observed saving;
an availability comparison does not establish that tool's benefit when invoked.

When controlling a bridge or adapter, avoid presenting equivalent text and structured results twice.
Check equivalence and retain unique fields, continuation and error information; preserve raw results
when an audit needs them. Verify any saving in the actual host. This does not authorize changing the
host or installing a gateway during AGENTS.md authoring.

## Planning and Execution

Handle small, clear changes directly. For meaningful multi-step work, use a proportional plan that
connects steps to observable completion checks, then execute and update it from evidence. Select one
planning approach rather than loading several frameworks for the same task.

| Approach | Type and suitable scenario | Cost and selection boundary |
| --- | --- | --- |
| Native planning + execution checklist | Host capability or concise instructions. Suitable for work that fits one session and needs a few dependent steps. | Usually the simplest starting point. No planning plugin is needed when the host already provides the required behavior. |
| Planning with Files | Skill, with optional host-specific plugin or hook integrations. Suitable for long tasks that must preserve plans, findings, and progress across sessions. | Persistent files and repeated context injection have a cost. Skill support does not imply hook support; check the actual host integration. |
| OpenSpec | CLI plus skills or commands. Suitable when a team needs reviewable change specifications, implementation tasks, and an archive of decisions. | Adds artifacts and process. Prefer it for requirements traceability, rather than as a universal replacement for a short execution plan. |
| Superpowers writing-plans + executing-plans | Two skills used on demand when the user selects them and host and project policy permit them. | Other Superpowers features remain off unless the user explicitly requests them. This limit does not require Superpowers for every plan or prohibit other capabilities. Replace older blanket assignments of design, review, or verification to Superpowers throughout the document. Preserve applicable model and project restrictions. |

Full lifecycle frameworks such as GitHub Spec Kit are candidates for projects that explicitly need
that process. They are not a default token-saving upgrade. Judge alternatives by needed persistence,
traceability, host support, and instruction overhead rather than assuming one is universally better.

During AGENTS.md authoring, describe the selected workflow for later tasks. Do not run its initializer,
invoke its implementation workflow, or create its plan/specification files.

## Design and Implementation

| Optional combination | Suitable scenario and division of responsibility | Boundary and fallback |
| --- | --- | --- |
| frontend-design → impeccable + Scrapider Guidelines | New web UI or an explicitly requested new visual direction. frontend-design establishes direction; impeccable improves hierarchy, interaction states, accessibility, consistency, and polish; Scrapider guides code changes. | Use each part only for work it contributes. Preserve the selected direction during implementation; use the project's design system and available guidance when a skill is absent. |
| impeccable + Scrapider Guidelines | Local visual or UX improvements to an existing web UI. impeccable guides the UI refinement; Scrapider guides the implementation. | Inherit the existing design language. A new visual direction belongs to the preceding route. Nonvisual logic and API changes do not trigger UI skills. |
| compose-expert + Scrapider Android guidance | Android projects using Compose that need detailed Compose API or UI implementation guidance, when compose-expert is available. | Keep the project's architecture and design constraints. If the specialist is unavailable, use the existing [Android Kotlin and Compose guidance](../android/android-kotlin-compose.md). |

Names and packaging vary by host. Match the actual exposed skill or plugin rather than assuming that
a similarly named bundle has identical instructions. Write only the project's relevant routes into
`AGENTS.md`; capability recommendations remain optional unless the author explicitly adopts a rule.
