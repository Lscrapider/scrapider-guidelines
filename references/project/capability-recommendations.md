# Optional Capability Recommendations

Load this reference when selecting workflows for a project's `AGENTS.md`. Each recipe is independently
usable; choose the ones the project needs. These recommendations do not authorize installing tools,
changing configuration, building indexes, or creating files beyond the target `AGENTS.md`.

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
| Localized changes | Native search and targeted reads + the project's coding guidance + proportionate native planning and verification. Locate the relevant implementation, make the scoped change, and check the affected behavior. | No additional retrieval service; broad relationship questions may need more manual navigation. |
| Cross-module changes | Codegraph + native retrieval + the chosen coding guidance. Use the graph's source and relationships to establish impact, inspect gaps, then implement and verify across affected boundaries. | An available, suitable graph index helps; retain native fallback. |
| Precise symbol or IDE work | Codegraph + Serena + native retrieval + the chosen coding guidance. Codegraph establishes structure; Serena handles remaining symbol/reference questions or supported edits; native tools cover other evidence. | Adds a language backend; JetBrains is optional, and duplicate retrieval should be avoided. |

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
| Plugin | A host integration or distribution that may include skills, tools, commands, or hooks. | Serena's JetBrains IDE plugin; host-specific skill bundles. |
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

Optimize for sufficient evidence with less irrelevant source and fewer repeated searches. Start from
the question and available capabilities. Follow the selected primary tool's contract and call supporting
tools only for unresolved evidence; do not impose one sequence across these independent recipes.
Expand context when needed for correctness; do not impose a blanket ban on reading complete files.

| Recipe | Suitable tasks and division of responsibility | Handoff and fallback |
| --- | --- | --- |
| Native search + targeted reads | Small changes with known locations; literals, configuration, documentation, and current working-tree content. | Search for the entry point and read the necessary context. This is a complete baseline when no indexing service is available. |
| Codegraph + native reads | Cross-file flows, callers, dependencies, and change impact. Codegraph handles structural discovery and can supply relevant source; native reads fill specific gaps. | Make a focused query, reuse its returned source, and inspect only missing or changed context. If the index is absent, stale, or incomplete, use native search and reads. |
| Serena + native reads | Symbol navigation, precise references, diagnostics, and supported semantic edits when Serena is the selected primary retrieval tool. Native tools cover literals and unsupported content. | Use the LSP backend or a suitable JetBrains backend. No graph service is required for this standalone recipe. Fall back to native tools for unsupported languages, stale state, or missing operations. |
| Codegraph + Serena, with an optional JetBrains plugin | Changes that need both a broad call/dependency view and precise symbol work. Codegraph establishes the affected flow; Serena resolves remaining symbol or reference questions and supported edits. | Call the second tool only for an unanswered question. If either is sufficient, stop. If one is unavailable, use the other's supported capabilities and native retrieval for the remaining gaps. |
| repomap + one chosen retrieval recipe | Initial orientation in an unfamiliar repository. The map supplies a bounded outline; the chosen retrieval tool supplies task-specific evidence. | Request a budgeted map when supported, then move to focused retrieval. Skip the map when the relevant module is known or another tool already provided an adequate outline. |

Codegraph implementations and exposed tools can differ. In the andysom25 implementation,
`codegraph_explore` can include source, call paths, and impact information. Do not describe it as
metadata-only or automatically add a second source-extraction service for content already returned.
Confirm the implementation before encoding tool-specific behavior in project instructions.
When Codegraph is the selected primary service, follow its exposed tool contract for the first code
lookup and reuse the returned source. In the combined recipe, Serena fills remaining language-aware
gaps and native tools cover text, configuration, documentation, or missing evidence. Do not import the
standalone Serena or native recipe's starting point into this combined route. When the exposed contract
requires Codegraph first for code questions or edits, a small change or known file is not an exception;
use native retrieval for a missing or unavailable index, uncovered content, or evidence gaps as permitted
by that contract. Respond to reported stale or incomplete results rather than assuming an index must
be rebuilt or rechecked on every task.

### Serena for JetBrains projects

For PyCharm or IntelliJ IDEA projects, Serena MCP can use the optional Serena JetBrains IDE plugin as
its language backend. The IDE plugin supplies IDE language capabilities; the coding agent still
connects through Serena MCP. Open the same project root in the IDE and Serena, and ensure the IDE can
resolve the relevant symbols and dependencies before relying on its results.

The JetBrains plugin is a paid option; Serena's free LSP backend is an alternative. Select one backend
according to the project's language support and needed operations. Do not require both or assume every
JetBrains product, language, or host supports the same operations. If the IDE setup is unavailable,
use the supported LSP backend or native retrieval.

## Optional Retrieval and Output Layers

Add a layer only for an unmet need. These layers address different costs and do not form progressively
better deployment tiers.

| Optional combination | When it helps | Boundaries and fallback |
| --- | --- | --- |
| Chosen retrieval recipe + RTK CLI | Relevant source is already targeted, but supported Git, build, test, or log commands produce excessive output. Retrieval handles source; RTK filters command output. | RTK does not supply symbols or dependencies. Use a confirmed host integration or supported CLI operation; otherwise use native commands with bounded output. Inspect raw output when filtering obscures a failure or needed evidence. |
| Structural retrieval + one semantic search service | The task is described conceptually and identifiers or structural queries do not locate the implementation. A service such as `@teknologika/mcp-codebase-search` can supply candidate code chunks. | Confirm package identity, language coverage, index freshness, and host support. Verify candidate matches against current code and follow relationships as needed. Reuse existing semantic search rather than adding duplicate indexes. |
| Chosen retrieval recipe + an AST symbol extractor | A supported language needs symbol-level source extraction that the existing tools do not provide. `syphon1c/code-scale-mcp` is a candidate to evaluate. | AST extraction and vector search serve different purposes. Do not assume a similarly named npm package is the same project. Prefer existing symbol tools or bounded native reads when they already suffice. |
| Selected MCP services behind a schema gateway | The host loads many tool definitions and does not already provide adequate deferred tool discovery. MCP Slim (`dopatools/mcp-slim`) is one candidate. | Confirm the gateway's actual discovery and proxy support. With a proxy design, the host uses the gateway and it manages downstream connections; listing every server beside it does not establish proxying. Use the host's normal MCP connections when a gateway adds no benefit. |

An integrated context engine may already combine outlines, symbols, graphs, and semantic retrieval.
Confirm the exact implementation and overlapping responsibilities before replacing or stacking services.
A nickname such as CCE does not establish a package identity, token-budget API, or drop-in replacement
for repomap and a symbol extractor. Leave unresolved candidates out of the active workflow.

Keep source retrieval, command output, tool schemas, and repeated calls distinct when assessing cost.
Schema deferral alone does not reduce source payloads; a tool-side cache hit alone does not prove fewer
model-input tokens. Do not promise a fixed savings percentage or label a larger stack the cheapest.
Measure total input, repeated work, and answer quality in the target host if quantitative savings matter.

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
