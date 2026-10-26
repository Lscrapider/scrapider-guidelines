# Capability Scenario Examples

Read the relevant examples after capability selection, using the
[description rules](../agents-md-authoring.md#capability-entry-format). Translate and adapt the
selected scenario to the target project; preserve its confirmed choices and constraints. Use one
combined entry for the jointly selected frontend-design and impeccable pair. Keep other capabilities'
distinct responsibilities and omit routine host operations and generic fallback declarations.

## Coverage

| Capability | Separate selection | Combined scenarios |
| --- | --- | --- |
| Codegraph | [Codegraph](retrieval.md#r2-codegraph) | [Relationship-aware project workflow](retrieval.md#relationship-aware-project-workflow); [IDE support](ide-and-library-docs.md#codegraph-and-jetbrains); [external docs](ide-and-library-docs.md#codegraph-and-context7) |
| Serena (LSP), only when explicitly selected | [Serena](retrieval.md#r4-serena-lsp) | [Codegraph + Serena](retrieval.md#r3-codegraph-with-serena-lsp); [IDE + Serena](ide-and-library-docs.md#jetbrains-and-serena); [coding workflow](retrieval.md#precise-symbol-project-workflow) |
| DeusData Codebase Memory | [Codebase Memory](retrieval.md#r5-deusdata-codebase-memory) | [Explicitly partitioned graph services](retrieval.md#r6-explicitly-partitioned-codegraph-and-codebase-memory) |
| Repomap | [Repomap](retrieval.md#a1-repomap) | [Repomap + Codegraph](retrieval.md#a2-repomap-with-codegraph) |
| Semantic search, including `@teknologika/mcp-codebase-search` | [Semantic search](retrieval.md#a3-semantic-search) | [Semantic and structural retrieval](retrieval.md#a4-codegraph-with-semantic-search) |
| AST extraction, including `syphon1c/code-scale-mcp` | [AST extraction](retrieval.md#a5-ast-symbol-extractor) | [AST extraction + Codegraph](retrieval.md#a6-codegraph-with-ast-symbol-extraction) |
| RTK command-output filtering | [RTK](retrieval.md#a7-rtk) | [RTK + Codegraph](retrieval.md#a8-codegraph-with-rtk); [RTK + gateway](retrieval.md#a11-codegraph-with-rtk-and-mcp-slim) |
| MCP Slim and host tool discovery | [Gateway with selected services](retrieval.md#a9-mcp-slim-schema-gateway-with-selected-mcp-services) | [Gateway + Codegraph](retrieval.md#a10-codegraph-with-mcp-slim-schema-gateway); [gateway + RTK](retrieval.md#a11-codegraph-with-rtk-and-mcp-slim) |
| JetBrains IDE MCP | [IDE entry](ide-and-library-docs.md#jetbrains-mcp) | [IDE + Context7](ide-and-library-docs.md#jetbrains-and-context7); [graph + IDE + docs](ide-and-library-docs.md#codegraph-jetbrains-and-context7) |
| Context7 | [External library docs](ide-and-library-docs.md#context7) | [IDE + Context7](ide-and-library-docs.md#jetbrains-and-context7); [graph + Context7](ide-and-library-docs.md#codegraph-and-context7) |
| Planning with Files | [Planning choices](planning-and-collaboration.md#planning) | [Durable notes + one execution plan](planning-and-collaboration.md#planning) |
| OpenSpec | [Specification owner](planning-and-collaboration.md#planning) | [OpenSpec + distinct investigation notes](planning-and-collaboration.md#planning) |
| Superpowers planning skills | [Paired writing-plans and executing-plans, only where permitted](planning-and-collaboration.md#planning) | Keep the pair as one plan owner; use a separate notes layer only for distinct evidence. |
| GitHub Spec Kit | [Full specification workflow](planning-and-collaboration.md#planning) | Implementation follows its artifacts; select it instead of another owner for the same specification. |
| Scrapider and existing coding standards | [Separate coding choices](design-and-coding.md#coding-standards) | [Project workflows](#complete-project-workflow-choices); [web design and coding](design-and-coding.md#web-ui-design-and-implementation) |
| frontend-design and impeccable | [Direction-only or refinement-only](design-and-coding.md#web-ui-design-and-implementation) | [New direction + refinement + coding; existing UI + coding](design-and-coding.md#web-ui-design-and-implementation) |
| compose-expert and Scrapider Android guidance | [Separate Android choices](design-and-coding.md#android-and-compose) | [Compose specialist + Android standards](design-and-coding.md#android-and-compose) |
| Author-selected Apple Design, Product Design, and ImageGen | [Individual extensions](design-and-coding.md#author-selected-android-design-extensions) | [Named design, evidence, implementation, and asset combinations](design-and-coding.md#author-selected-android-design-extensions) |

## Complete Project Workflow Choices

Use a scenario only when the author selected its capabilities. Preserve each capability's use
conditions and cooperation; they do not all have to run on every task.

For local changes without an additional retrieval service, record only the selected
[coding-guidance entry](design-and-coding.md#coding-standards).

- [Relationship-aware project workflow](retrieval.md#relationship-aware-project-workflow): selected Codegraph retrieval and Scrapider coding guidance.
- [Precise-symbol project workflow](retrieval.md#precise-symbol-project-workflow): selected Codegraph, explicitly selected Serena, and Scrapider coding guidance.

## Collaboration

Write selected roles from the [five-role catalog](planning-and-collaboration.md#collaboration), including
both reviewers when chosen, in the project's collaboration section. IDE support does not turn the
standards reviewer into a tool entry. Keep each role's responsibility and handoff there once.
When both reviews are selected, distinguish functional correctness from standards conformance.

## Overlapping Responsibilities

| Overlap or conflict | Combined case | Separate case |
| --- | --- | --- |
| Two graph services | Use [explicit coverage partitions](retrieval.md#r6-explicitly-partitioned-codegraph-and-codebase-memory) only for a verified gap. | Select Codegraph or Codebase Memory as the primary graph; do not query both for the same evidence. |
| Graph, IDE, and LSP symbol results | Use [conditional symbol support](retrieval.md#r3-codegraph-with-serena-lsp) or [IDE + Serena](ide-and-library-docs.md#jetbrains-and-serena) for unresolved bindings. | Select the service that covers the question. |
| Existing outlines and repomap | Use [repomap with Codegraph](retrieval.md#a2-repomap-with-codegraph) only for missing orientation. | Use the existing outline without adding repomap when it already locates the target. |
| Semantic candidates and structural evidence | Use [semantic + graph retrieval](retrieval.md#a4-codegraph-with-semantic-search) for different evidence gaps. | Use [semantic search](retrieval.md#a3-semantic-search) or a structural profile alone when sufficient. |
| Existing symbol extraction and an AST extractor | Use [AST + graph retrieval](retrieval.md#a6-codegraph-with-ast-symbol-extraction) only for missing source bodies. | Retain existing extraction or [AST extraction](retrieval.md#a5-ast-symbol-extractor), not duplicate extraction paths. |
| Output filtering and schema discovery | [RTK + MCP Slim](retrieval.md#a11-codegraph-with-rtk-and-mcp-slim) assigns shell output and tool discovery separately. | Select only the layer matching the actual output or discovery gap; skip the gateway when the host already supplies deferred discovery. |
| Installed API facts and external docs | [IDE + Context7](ide-and-library-docs.md#jetbrains-and-context7) preserves project authority. | Use local evidence directly for established usage; select [Context7](ide-and-library-docs.md#context7) for unresolved external facts. |
| Multiple planning or specification frameworks | [Planning combinations](planning-and-collaboration.md#planning) allow distinct evidence notes alongside one task/specification owner. | Select one framework when both would own the same plan or specification. |
| New direction, refinement, and coding | [Web combinations](design-and-coding.md#web-ui-design-and-implementation) separate the accepted direction from refinement and implementation rules. | Select direction-only, refinement-only, or the existing coding standard for a task confined to that responsibility. |
| Android design conventions and platform rules | [Author-selected Android combinations](design-and-coding.md#author-selected-android-design-extensions) retain Android and project authority. | Use only the selected design, Compose, or engineering capability whose task scenario applies. |

If no distinct responsibilities can be assigned, present the alternatives for selection instead of
combining them. Do not preserve competing owners merely to include every available tool.
