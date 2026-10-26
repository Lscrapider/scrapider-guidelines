# Retrieval Capability Examples

Choose a primary indexed retrieval profile only when recurring relationship or symbol queries justify one. Add support layers only when their scenarios apply.

Each fenced block is a complete scenario sample containing one or more peer capability entries. Include a whole sample only when its scenario is selected, and leave alternatives out of `AGENTS.md`.

Serena scenarios apply only when the author explicitly selected it; do not add Serena to the default
Codegraph and IDE workflow. Use available IDE and local dependency evidence for symbol gaps first.

## Index

### Primary profiles

- [R2. Codegraph](#r2-codegraph)
- [R3. Codegraph with Serena LSP](#r3-codegraph-with-serena-lsp)
- [R4. Serena LSP](#r4-serena-lsp)
- [R5. DeusData Codebase Memory](#r5-deusdata-codebase-memory)
- [R6. Explicitly partitioned Codegraph and Codebase Memory](#r6-explicitly-partitioned-codegraph-and-codebase-memory)

### Project workflow examples

- [Relationship-aware project workflow](#relationship-aware-project-workflow)
- [Precise-symbol project workflow](#precise-symbol-project-workflow)

### Support layers and complete combinations

- [A1. Repomap](#a1-repomap); [A2. Repomap with Codegraph](#a2-repomap-with-codegraph)
- [A3. Semantic search](#a3-semantic-search); [A4. Codegraph with semantic search](#a4-codegraph-with-semantic-search)
- [A5. AST symbol extractor](#a5-ast-symbol-extractor); [A6. Codegraph with AST symbol extraction](#a6-codegraph-with-ast-symbol-extraction)
- [A7. RTK](#a7-rtk); [A8. Codegraph with RTK](#a8-codegraph-with-rtk)
- [A9. MCP Slim schema gateway with selected MCP services](#a9-mcp-slim-schema-gateway-with-selected-mcp-services); [A10. Codegraph with MCP Slim schema gateway](#a10-codegraph-with-mcp-slim-schema-gateway); [A11. Codegraph with RTK and MCP Slim](#a11-codegraph-with-rtk-and-mcp-slim)

### Selection notes

- For a single primary graph service, compare [Codegraph](#r2-codegraph) with [DeusData Codebase Memory](#r5-deusdata-codebase-memory). Use [the partitioned combination](#r6-explicitly-partitioned-codegraph-and-codebase-memory) only when each service has a distinct query domain.
- When Serena is explicitly selected, use [Codegraph + Serena](#r3-codegraph-with-serena-lsp) when relationship and precise-symbol queries both recur; use [Serena](#r4-serena-lsp) when graph retrieval is not needed.
- Use [Repomap](#a1-repomap) when existing outlines do not locate unknown paths; use [Repomap with Codegraph](#a2-repomap-with-codegraph) only when that selected profile also needs the missing orientation.
- Use [Semantic search](#a3-semantic-search) for conceptual gaps; add it to [Codegraph](#a4-codegraph-with-semantic-search) only when structural queries still do not locate candidates.
- Use [AST extraction](#a5-ast-symbol-extractor) for a symbol-body gap; combine it with [Codegraph](#a6-codegraph-with-ast-symbol-extraction) only when existing symbol tools do not supply that body.
- RTK filters supported shell output; MCP Slim handles schema discovery. Use [the RTK entry](#a7-rtk) or [Codegraph + RTK](#a8-codegraph-with-rtk) for command output, [the gateway entry](#a9-mcp-slim-schema-gateway-with-selected-mcp-services) or [Codegraph + MCP Slim](#a10-codegraph-with-mcp-slim-schema-gateway) for deferred discovery, and [the combined entry](#a11-codegraph-with-rtk-and-mcp-slim) only when both needs apply.

## Primary Retrieval Profiles

### R2. Codegraph

```markdown
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file flows, dependencies, or impact analysis when the Codegraph package, exposed operations, and suitable index are verified.
  - **Boundary:** Codegraph owns relationship questions. Preserve any project routing contract that requires Codegraph before code questions or edits; a known path does not override that order.
  - **Skip:** Skip broad graph exploration when no relationship question remains.
```

### R3. Codegraph with Serena LSP

```markdown
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file flows, dependencies, or impact analysis when the Codegraph package, exposed operations, and suitable index are verified.
  - **Boundary:** Codegraph handles cross-file relationships; Serena handles only unresolved precise-symbol queries. Do not send the same query to both services.
  - **Skip:** Skip graph exploration when no relationship question remains.
- **Serena (LSP)** — Precise symbol and reference evidence.
  - **Scenario:** Use only when the author explicitly selected Serena, cross-file relationship and precise-symbol queries both recur, and Codegraph does not resolve the precise-symbol questions.
  - **Boundary:** Serena handles precise-symbol queries only; Codegraph retains relationship queries. Do not repeat a query already resolved by Codegraph.
  - **Skip:** Do not use Serena for answered questions, ordinary text or configuration searches, or when the project root, required toolchains, language/query support, or host memory capacity are not verified.
  - **Fallback:** If symbol evidence remains insufficient, report the question as unresolved rather than inferring the missing source.
```

### R4. Serena LSP

```markdown
- **Serena (LSP)** — Precise symbol and reference evidence.
  - **Scenario:** Use as the explicitly selected primary service for recurring precise definition, implementation, or reference queries when graph retrieval is not needed.
  - **Boundary:** Serena handles supported symbol queries only. If Codegraph is selected, reserve it for recurring cross-file relationships; do not send the same query to both services.
  - **Skip:** Do not use Serena for answered queries, ordinary text or configuration searches, or when the project root, required toolchains, language/query support, or host memory capacity are not verified.
```

### R5. DeusData Codebase Memory

```markdown
- **DeusData/codebase-memory-mcp** — Graph, outline, and symbol-source retrieval.
  - **Scenario:** Use as the selected primary service for recurring outline, graph-query, and symbol-source needs after its package identity and host operations are verified.
  - **Boundary:** Codebase Memory handles its selected outline, graph, and symbol-source queries. Do not duplicate a query already covered by another selected graph service.
  - **Skip:** Do not add Codebase Memory when an existing graph service already covers the same recurring queries.
  - **Fallback:** Complete truncated source before interpreting it; empty or stale results do not prove a path or symbol is absent.
```

### R6. Explicitly partitioned Codegraph and Codebase Memory

```markdown
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file flow, dependency, and impact queries when its package and operations are verified.
  - **Boundary:** Assign Codegraph to cross-file flow, dependency, and impact questions. Use Codebase Memory only for explicitly named outline or symbol-source gaps that its verified output covers and the selected Codegraph route does not. Do not send the same query to both services.
  - **Skip:** If query domains overlap or cannot be named, do not route the same query through both services; ask the author which primary service to retain rather than replacing an existing selection.
  - **Fallback:** Complete truncated source before drawing conclusions; empty or stale results do not prove a path or symbol is absent.
- **Codebase Memory** — Graph, outline, and symbol-source retrieval.
  - **Scenario:** Use for recurring outline or symbol-source queries not covered by the selected Codegraph route, after its package and operations are verified.
  - **Boundary:** Codebase Memory handles only its assigned outline or symbol-source gaps; Codegraph retains cross-file relationship queries. Do not duplicate a query already covered by either service.
  - **Skip:** Do not send a query to Codebase Memory when the selected Codegraph route already covers it.
  - **Fallback:** Complete truncated source before interpreting it; empty or uncovered results do not prove a path or symbol is absent.
```

## Project Workflows with Coding Guidance

Use a scenario only when the author selected its capabilities. Keep them as separate peer entries,
with task-specific calls conditional; they do not all have to run on every task.

### Relationship-Aware Project Workflow

```md
- **Codegraph** — Cross-file relationships and impact evidence.
  - **Scenario**: Unresolved call paths, dependencies, or impact questions covered by the project index.
  - **Boundary**: Codegraph supplies relationship evidence. Coding policy remains with project instructions and the selected engineering guidance.

- **Scrapider Guidelines** — Engineering rules for implementation and review.
  - **Scenario**: Implementing, refactoring, debugging, or reviewing code under the selected Scrapider rules.
  - **Boundary**: Retrieval establishes current code facts; Scrapider guides changes and review without overriding explicit project contracts or user instructions.
```

### Precise-Symbol Project Workflow

Use this scenario only for an explicit Serena selection; it is not a default recommendation.

```md
- **Codegraph** — Relationship evidence for the selected code paths.
  - **Scenario**: Call, dependency, or impact questions remain unresolved and are covered by the graph.
  - **Boundary**: Codegraph owns relationships; Serena handles only remaining precise-symbol gaps. Do not repeat a query already answered by either selected service.

- **Serena (LSP)** — Supplementary symbol evidence.
  - **Scenario**: The author explicitly selected Serena, recurring symbol questions remain after the selected graph route, and language support and resource capacity are verified.
  - **Boundary**: Serena fills only the unresolved binding or reference gap; it does not repeat answered graph queries or replace the selected coding rules.

- **Scrapider Guidelines** — Engineering rules for the resulting changes.
  - **Scenario**: Implementing or reviewing code under the project's selected Scrapider rules.
  - **Boundary**: Retrieval services supply evidence; project contracts and Scrapider guidance govern implementation and review.
```

## Support Layers

### A1. Repomap

```markdown
- **Repomap** — Initial codebase orientation.
  - **Scenario:** Use for an initial outline only when module or symbol paths are unknown and existing outlines do not locate them.
  - **Boundary:** Repomap supplies orientation only; it does not establish source bodies or relationship evidence.
```

### A2. Repomap with Codegraph

```markdown
- **Repomap** — Initial codebase orientation.
  - **Scenario:** Use when Codegraph is the selected service and a recurring relationship workload needs an initial module or symbol outline that existing outlines do not provide.
  - **Boundary:** Repomap supplies only the missing orientation; Codegraph handles relationships. Do not use either service as a substitute for complete source bodies.
  - **Skip:** Do not use repomap when an existing outline already locates the target or as a substitute for relationship queries.
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file relationship questions when Codegraph is the selected service.
  - **Boundary:** Codegraph owns relationship queries; repomap supplies only initial orientation. Verify graph findings against complete current source.
  - **Skip:** Skip graph exploration when no relationship question remains.
```

### A3. Semantic search

```markdown
- **@teknologika/mcp-codebase-search** — Conceptual code candidates.
  - **Scenario:** Use when a conceptual task is not located by identifiers or structural queries and package identity, language coverage, index freshness, and host support are verified.
  - **Boundary:** Semantic search supplies candidate code chunks only; verify each candidate against complete current project source.
  - **Skip:** Do not add a second semantic-search service when an existing one covers the same conceptual queries.
```

### A4. Codegraph with semantic search

```markdown
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file relationship queries when Codegraph is the selected service.
  - **Boundary:** Codegraph handles relationships; semantic search supplies candidate code chunks. Verify each candidate against complete current project source.
  - **Skip:** Skip graph queries when no relationship question remains.
- **@teknologika/mcp-codebase-search** — Conceptual code candidates.
  - **Scenario:** Use when conceptual searches remain unresolved by identifiers or Codegraph's structural results and package identity, language coverage, index freshness, and host support are verified.
  - **Boundary:** Semantic search supplies candidate code chunks only; Codegraph retains relationship queries. Do not repeat a query already answered by either service.
  - **Skip:** Do not add semantic search when an existing semantic service covers the same queries or structural retrieval already locates the code.
```

### A5. AST symbol extractor

```markdown
- **syphon1c/code-scale-mcp** — AST symbol-source extraction.
  - **Scenario:** Use for symbol-body queries in a supported language after its package identity and extraction support are verified.
  - **Boundary:** The AST extractor supplies symbol-level source only; verify that the returned body is complete before interpreting it.
  - **Skip:** Do not add an extractor when the required body is already available, or use AST extraction as a substitute for semantic search.
```

### A6. Codegraph with AST symbol extraction

```markdown
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file relationship queries when Codegraph is the selected service.
  - **Boundary:** Codegraph handles relationship evidence; the AST extractor handles only the symbol-body gap. Verify the returned body is complete before interpreting it.
  - **Skip:** Skip graph queries when no relationship question remains.
- **syphon1c/code-scale-mcp** — AST symbol-source extraction.
  - **Scenario:** Use for a supported symbol-body query that remains unresolved after the selected graph route.
  - **Boundary:** The extractor supplies symbol-level source only; Codegraph retains relationship queries. Verify that the returned body is complete before interpreting it.
  - **Skip:** Do not add AST extraction when the required body is already available.
```

### A7. RTK

```markdown
- **RTK** — Supported command-output filtering.
  - **Scenario:** Use command filters when supported Git, build, test, or log output is truncated or repeatedly exceeds the host output limit.
  - **Boundary:** RTK filters shell-command output only; it does not filter MCP schemas. Keep raw output available when filtering obscures evidence.
```

### A8. Codegraph with RTK

```markdown
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file relationship questions when Codegraph is the selected service.
  - **Boundary:** Codegraph handles relationships; RTK filters supported Git/build/test/log command output only.
  - **Skip:** Skip graph exploration when no relationship question remains.
- **RTK** — Supported command-output filtering.
  - **Scenario:** Use filters for supported Git, build, test, or log output that is truncated or repeatedly exceeds the host output limit.
  - **Boundary:** RTK filters shell-command output only; Codegraph retains relationship questions, and RTK does not filter MCP schemas.
```

### A9. MCP Slim schema gateway with selected MCP services

```markdown
- **MCP Slim gateway** — Tool discovery and proxy routing for selected MCP services.
  - **Scenario:** Use `dopatools/mcp-slim` when the host eagerly loads the definitions of MCP services already selected for the project and lacks native deferred discovery; verify package identity and proxy support for those services.
  - **Boundary:** MCP Slim handles schema discovery and proxy routing only; downstream MCP services retain their assigned retrieval responsibilities.
  - **Skip:** If host-native deferred discovery is already selected for these services, retain it instead of adding a gateway for the same purpose.
  - **Fallback:** Retain direct MCP connections for services the gateway cannot proxy; do not route a service through an unverified proxy.
```

### A10. Codegraph with MCP Slim schema gateway

```markdown
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file relationship queries when Codegraph is the selected service.
  - **Boundary:** Codegraph handles relationships; MCP Slim handles verified schema discovery and proxy routing.
  - **Fallback:** Retain a direct Codegraph MCP connection if the gateway cannot proxy it.
- **MCP Slim gateway** — Tool discovery and proxy routing for selected MCP services.
  - **Scenario:** Use with the selected Codegraph service when the host eagerly loads its tool definitions, native deferred discovery is unavailable, and proxy support is verified.
  - **Boundary:** MCP Slim handles schema discovery and proxy routing only; Codegraph retains relationship queries.
  - **Skip:** If native deferred discovery is already selected for Codegraph, retain that route instead of adding the gateway.
  - **Fallback:** Keep a direct Codegraph connection if MCP Slim cannot proxy it.
```

### A11. Codegraph with RTK and MCP Slim

```markdown
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file relationship queries when Codegraph is the selected service.
  - **Boundary:** Codegraph handles relationships; MCP Slim handles schema discovery, and RTK filters supported shell output.
- **RTK** — Supported command-output filtering.
  - **Scenario:** Use filters when supported Git, build, test, or log output is truncated or repeatedly exceeds the host output limit.
  - **Boundary:** RTK filters shell-command output only; MCP Slim handles tool-schema discovery, and RTK does not filter MCP schemas.
- **MCP Slim gateway** — Tool discovery and proxy routing for selected MCP services.
  - **Scenario:** Use with the selected Codegraph service when the host eagerly loads its definitions and lacks native deferred discovery, with proxy support verified.
  - **Boundary:** MCP Slim handles schema discovery/proxy routing only; Codegraph retains relationship queries, and RTK handles shell output.
  - **Skip:** If native deferred discovery is already selected for Codegraph, retain that route instead of adding the gateway.
  - **Fallback:** Keep a direct Codegraph connection if the gateway cannot proxy it.
```
