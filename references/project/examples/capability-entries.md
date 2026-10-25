# Capability Scenario Examples

Read the relevant examples after capability selection, using the
[description rules](../agents-md-authoring.md#capability-entry-format). Translate and adapt the
selected scenario to the target project; preserve its confirmed choices and constraints. A scenario
may contain several cooperating capabilities, each written as its own peer entry.

## Coverage

| Capability or responsibility | Separate selection | Combined scenarios |
| --- | --- | --- |
| Codegraph | [Codegraph + native](retrieval.md#r2-codegraph-with-native-retrieval) | [Relationship-aware project workflow](#relationship-aware-project-workflow); [IDE support](ide-and-library-docs.md#codegraph-native-tools-and-jetbrains); [external docs](ide-and-library-docs.md#codegraph-native-tools-and-context7) |
| Serena (LSP), only when explicitly selected | [Serena + native](retrieval.md#r4-serena-lsp-with-native-retrieval) | [Codegraph + Serena + native](retrieval.md#r3-codegraph-with-serena-lsp-and-native-retrieval); [IDE + Serena](ide-and-library-docs.md#jetbrains-and-serena); [coding workflow](#precise-symbol-project-workflow) |
| DeusData Codebase Memory | [Codebase Memory + native](retrieval.md#r5-deusdata-codebase-memory-with-native-retrieval) | [Explicitly partitioned graph services](retrieval.md#r6-explicitly-partitioned-codegraph-and-codebase-memory) |
| Repomap | [Repomap + native](retrieval.md#a1-repomap-with-native-retrieval) | [Repomap + Codegraph + native](retrieval.md#a2-repomap-with-codegraph-and-native-retrieval) |
| Semantic search, including `@teknologika/mcp-codebase-search` | [Semantic search + native](retrieval.md#a3-semantic-search-with-native-retrieval) | [Semantic and structural retrieval](retrieval.md#a4-codegraph-with-semantic-search-and-native-retrieval) |
| AST extraction, including `syphon1c/code-scale-mcp` | [AST extraction + native](retrieval.md#a5-ast-symbol-extractor-with-native-retrieval) | [AST extraction + Codegraph](retrieval.md#a6-codegraph-with-ast-symbol-extraction-and-native-retrieval) |
| RTK command-output filtering | [RTK + native](retrieval.md#a7-rtk-with-native-retrieval) | [RTK + Codegraph](retrieval.md#a8-codegraph-with-rtk-and-native-retrieval); [RTK + gateway](retrieval.md#a11-codegraph-with-rtk-mcp-slim-and-native-retrieval) |
| MCP Slim and host tool discovery | [Gateway with selected services](retrieval.md#a9-mcp-slim-schema-gateway-with-selected-mcp-services) | [Gateway + Codegraph](retrieval.md#a10-codegraph-with-mcp-slim-schema-gateway-and-native-retrieval); [gateway + RTK](retrieval.md#a11-codegraph-with-rtk-mcp-slim-and-native-retrieval) |
| JetBrains IDE MCP | [IDE entry](ide-and-library-docs.md#jetbrains-mcp) | [IDE + Context7](ide-and-library-docs.md#jetbrains-and-context7); [graph + IDE + docs](ide-and-library-docs.md#codegraph-native-tools-jetbrains-and-context7) |
| Context7 | [External library docs](ide-and-library-docs.md#context7) | [IDE + Context7](ide-and-library-docs.md#jetbrains-and-context7); [graph + Context7](ide-and-library-docs.md#codegraph-native-tools-and-context7) |
| Standards review, including its internal inspection route | [Standards reviewer](planning-and-collaboration.md#collaboration) | [Standards review with IDE support](ide-and-library-docs.md#ide-inspections-and-standards-review) |
| Native planning and Planning with Files | [Individual planning choices](planning-and-collaboration.md#planning) | [Durable notes + one execution plan](planning-and-collaboration.md#planning) |
| OpenSpec | [Specification owner with native execution](planning-and-collaboration.md#planning) | [OpenSpec + distinct investigation notes](planning-and-collaboration.md#planning) |
| Superpowers planning skills | [Paired writing-plans and executing-plans, only where permitted](planning-and-collaboration.md#planning) | Keep the pair as one plan owner; use a separate notes layer only for distinct evidence. |
| GitHub Spec Kit | [Full specification workflow](planning-and-collaboration.md#planning) | Implementation follows its artifacts; select it instead of another owner for the same specification. |
| Primary agent and five Scrapider roles | [Direct work, explorer, developer, code reviewer, standards reviewer, generalist](planning-and-collaboration.md#collaboration) | [Primary + explorer + developer; functional + standards review](planning-and-collaboration.md#collaboration) |
| Scrapider and existing coding standards | [Separate coding choices](design-and-coding.md#coding-standards) | [Project workflows](#complete-project-workflow-choices); [web design and coding](design-and-coding.md#web-ui-design-and-implementation) |
| frontend-design and impeccable | [Direction-only or refinement-only](design-and-coding.md#web-ui-design-and-implementation) | [New direction + refinement + coding; existing UI + coding](design-and-coding.md#web-ui-design-and-implementation) |
| compose-expert and Scrapider Android guidance | [Separate Android choices](design-and-coding.md#android-and-compose) | [Compose specialist + Android standards](design-and-coding.md#android-and-compose) |
| Author-selected Apple Design, Product Design, and ImageGen | [Individual extensions](design-and-coding.md#author-selected-android-design-extensions) | [Named design, evidence, implementation, and asset combinations](design-and-coding.md#author-selected-android-design-extensions) |

## Complete Project Workflow Choices

Use a scenario only when the author selected its capabilities. Keep them as separate peer entries,
with task-specific calls conditional; they do not all have to run on every task.

### Lightweight Project Workflow

For local changes without an additional retrieval service, use the selected
[coding-guidance entry](design-and-coding.md#coding-standards). Local search and file operations
remain the direct execution path; do not add a separate native-search capability entry.

### Relationship-Aware Project Workflow

```md
- **Codegraph** — Cross-file relationships and impact evidence.
  - **Scenario**: Unresolved call paths, dependencies, or impact questions covered by the project index.
  - **Boundary**: Codegraph supplies relationship evidence; local search covers literals and uncovered source. Coding policy remains with project instructions and the selected engineering guidance.
  - **Fallback**: Use local search and targeted reads for unavailable, stale, or incomplete graph evidence.

- **Scrapider Guidelines** — Engineering rules for implementation and review.
  - **Scenario**: Implementing, refactoring, debugging, or reviewing code under the selected Scrapider rules.
  - **Boundary**: Retrieval establishes current code facts; Scrapider guides changes and review without overriding explicit project contracts or user instructions.
  - **Fallback**: Follow existing project standards and report any material rule gap if the skill is unavailable.
```

### Precise-Symbol Project Workflow

Use this scenario only for an explicit Serena selection; it is not a default recommendation.

```md
- **Codegraph** — Relationship evidence for the selected code paths.
  - **Scenario**: Call, dependency, or impact questions remain unresolved and are covered by the graph.
  - **Boundary**: Codegraph owns relationships; Serena handles only remaining precise-symbol gaps, and local search handles direct text and uncovered source.
  - **Fallback**: Use local search and targeted reads when the graph is unavailable or incomplete.

- **Serena (LSP)** — Supplementary symbol evidence.
  - **Scenario**: Recurring symbol questions remain after graph and local investigation, with verified language support and resource capacity.
  - **Boundary**: Serena fills only the unresolved binding or reference gap; it does not repeat answered graph queries or replace the selected coding rules.
  - **Fallback**: Use available graph or local evidence and state any unresolved symbol question.

- **Scrapider Guidelines** — Engineering rules for the resulting changes.
  - **Scenario**: Implementing or reviewing code under the project's selected Scrapider rules.
  - **Boundary**: Retrieval services supply evidence; project contracts and Scrapider guidance govern implementation and review.
  - **Fallback**: Follow existing project standards if the skill is unavailable.
```

## Overlapping Responsibilities

| Overlap or conflict | Combined case | Separate case |
| --- | --- | --- |
| Two graph services | Use [explicit coverage partitions](retrieval.md#r6-explicitly-partitioned-codegraph-and-codebase-memory) only for a verified gap. | Select Codegraph or Codebase Memory as the primary graph; do not query both for the same evidence. |
| Graph, IDE, and LSP symbol results | Use [conditional symbol support](retrieval.md#r3-codegraph-with-serena-lsp-and-native-retrieval) or [IDE + Serena](ide-and-library-docs.md#jetbrains-and-serena) for unresolved bindings. | Select the service that covers the question, retaining native fallback. |
| Existing outlines and repomap | Use [repomap with Codegraph](retrieval.md#a2-repomap-with-codegraph-and-native-retrieval) only for missing orientation. | Use the existing outline without adding repomap when it already locates the target. |
| Semantic candidates and structural evidence | Use [semantic + graph retrieval](retrieval.md#a4-codegraph-with-semantic-search-and-native-retrieval) for different evidence gaps. | Use [semantic + native](retrieval.md#a3-semantic-search-with-native-retrieval) or a structural profile alone when sufficient. |
| Existing symbol extraction and an AST extractor | Use [AST + graph retrieval](retrieval.md#a6-codegraph-with-ast-symbol-extraction-and-native-retrieval) only for missing source bodies. | Retain existing extraction or [AST + native](retrieval.md#a5-ast-symbol-extractor-with-native-retrieval), not duplicate extraction paths. |
| Output filtering and schema discovery | [RTK + MCP Slim](retrieval.md#a11-codegraph-with-rtk-mcp-slim-and-native-retrieval) assigns shell output and tool discovery separately. | Select only the layer matching the actual output or discovery gap; use native deferred discovery when available. |
| Native edits and IDE semantics | [IDE combinations](ide-and-library-docs.md#selection-boundaries) assign semantic operations separately from ordinary file edits. | Use native tools alone for tasks covered by their existing behavior. |
| Installed API facts and external docs | [IDE + Context7](ide-and-library-docs.md#jetbrains-and-context7) preserves project authority. | Use local evidence directly for established usage; select [Context7](ide-and-library-docs.md#context7) for unresolved external facts. |
| Multiple planning or specification frameworks | [Planning combinations](planning-and-collaboration.md#planning) allow distinct evidence notes alongside one task/specification owner. | Select one framework when both would own the same plan or specification. |
| Functional and standards review | [Both reviewers](planning-and-collaboration.md#collaboration) cover distinct review questions under primary-agent acceptance. | Use the matching reviewer alone when only that review scope applies. |
| New direction, refinement, and coding | [Web combinations](design-and-coding.md#web-ui-design-and-implementation) separate the accepted direction from refinement and implementation rules. | Select direction-only, refinement-only, or the existing coding standard for a task confined to that responsibility. |
| Android design conventions and platform rules | [Author-selected Android combinations](design-and-coding.md#author-selected-android-design-extensions) retain Android and project authority. | Use only the selected design, Compose, or engineering capability whose task scenario applies. |

If no distinct responsibilities can be assigned, present the alternatives for selection instead of
combining them. Do not preserve competing owners merely to include every available tool.
