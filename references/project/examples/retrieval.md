# Retrieval Capability Examples

Choose a primary indexed retrieval profile only when recurring relationship or symbol queries justify one. Use native search and reads directly for localized work and as fallback for gaps; they are default operations, not separate capabilities. Add support layers only when their scenarios apply.

Each fenced block is a complete scenario sample containing one or more peer capability entries. Include a whole sample only when its scenario is selected, and leave alternatives out of `AGENTS.md`.

Serena scenarios apply only when the author explicitly selected it; do not add Serena to the default
Codegraph and IDE workflow. Use available IDE and local dependency evidence for symbol gaps first.

## Index

### Primary profiles

- [R2. Codegraph with native retrieval](#r2-codegraph-with-native-retrieval)
- [R3. Codegraph with Serena LSP and native retrieval](#r3-codegraph-with-serena-lsp-and-native-retrieval)
- [R4. Serena LSP with native retrieval](#r4-serena-lsp-with-native-retrieval)
- [R5. DeusData Codebase Memory with native retrieval](#r5-deusdata-codebase-memory-with-native-retrieval)
- [R6. Explicitly partitioned Codegraph and Codebase Memory](#r6-explicitly-partitioned-codegraph-and-codebase-memory)

### Support layers and complete combinations

- [A1. Repomap with native retrieval](#a1-repomap-with-native-retrieval); [A2. Repomap with Codegraph and native retrieval](#a2-repomap-with-codegraph-and-native-retrieval)
- [A3. Semantic search with native retrieval](#a3-semantic-search-with-native-retrieval); [A4. Codegraph with semantic search and native retrieval](#a4-codegraph-with-semantic-search-and-native-retrieval)
- [A5. AST symbol extractor with native retrieval](#a5-ast-symbol-extractor-with-native-retrieval); [A6. Codegraph with AST symbol extraction and native retrieval](#a6-codegraph-with-ast-symbol-extraction-and-native-retrieval)
- [A7. RTK with native retrieval](#a7-rtk-with-native-retrieval); [A8. Codegraph with RTK and native retrieval](#a8-codegraph-with-rtk-and-native-retrieval)
- [A9. MCP Slim schema gateway with selected MCP services](#a9-mcp-slim-schema-gateway-with-selected-mcp-services); [A10. Codegraph with MCP Slim schema gateway and native retrieval](#a10-codegraph-with-mcp-slim-schema-gateway-and-native-retrieval); [A11. Codegraph with RTK, MCP Slim, and native retrieval](#a11-codegraph-with-rtk-mcp-slim-and-native-retrieval)

### Selection notes

- For a single primary graph service, compare [Codegraph + native retrieval](#r2-codegraph-with-native-retrieval) with [DeusData Codebase Memory + native retrieval](#r5-deusdata-codebase-memory-with-native-retrieval). Use [the partitioned combination](#r6-explicitly-partitioned-codegraph-and-codebase-memory) only when each service has a distinct query domain.
- When Serena is explicitly selected, use [Codegraph + Serena + native retrieval](#r3-codegraph-with-serena-lsp-and-native-retrieval) when relationship and precise-symbol queries both recur; use [Serena + native retrieval](#r4-serena-lsp-with-native-retrieval) when graph retrieval is not needed.
- Use [repomap with native retrieval](#a1-repomap-with-native-retrieval) when existing outlines do not locate unknown paths; use [repomap with Codegraph](#a2-repomap-with-codegraph-and-native-retrieval) only when that selected profile also needs the missing orientation.
- Use [semantic search with native retrieval](#a3-semantic-search-with-native-retrieval) for conceptual gaps; add it to [Codegraph](#a4-codegraph-with-semantic-search-and-native-retrieval) only when structural queries still do not locate candidates.
- Use [AST extraction with native retrieval](#a5-ast-symbol-extractor-with-native-retrieval) for a symbol-body gap; combine it with [Codegraph](#a6-codegraph-with-ast-symbol-extraction-and-native-retrieval) only when existing symbol tools do not supply that body.
- RTK filters supported shell output; MCP Slim handles schema discovery. Use [the RTK entry](#a7-rtk-with-native-retrieval) or [Codegraph + RTK](#a8-codegraph-with-rtk-and-native-retrieval) for command output, [the gateway entry](#a9-mcp-slim-schema-gateway-with-selected-mcp-services) or [Codegraph + MCP Slim](#a10-codegraph-with-mcp-slim-schema-gateway-and-native-retrieval) for deferred discovery, and [the combined entry](#a11-codegraph-with-rtk-mcp-slim-and-native-retrieval) only when both needs apply.

## Primary Retrieval Profiles

### R2. Codegraph with native retrieval

```markdown
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file flows, dependencies, or impact analysis when the Codegraph package, exposed operations, and suitable index are verified.
  - **Boundary:** Codegraph owns relationship questions. Use native search and reads for literals, configuration, documentation, current edits, and evidence outside the index. Preserve any host contract that requires Codegraph before code questions or edits; a known path does not override that order.
  - **Skip:** Skip broad graph exploration when no relationship question remains.
  - **Fallback:** Use native source evidence when the index is unavailable, stale, or incomplete.
```

### R3. Codegraph with Serena LSP and native retrieval

```markdown
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file flows, dependencies, or impact analysis when the Codegraph package, exposed operations, and suitable index are verified.
  - **Boundary:** Codegraph handles cross-file relationships; Serena handles only unresolved precise-symbol queries; native search and reads handle direct source, literals, configuration, documentation, edits, and uncovered evidence.
  - **Skip:** Skip graph exploration when no relationship question remains.
  - **Fallback:** Use native evidence for an unavailable, stale, or incomplete graph result; do not repeat a symbol query that Serena already resolved.
- **Serena (LSP)** — Precise symbol and reference evidence.
  - **Scenario:** Use for recurring definition, implementation, or reference queries that remain unresolved after Codegraph and native evidence.
  - **Boundary:** Serena handles precise-symbol queries only; Codegraph retains relationship queries, and native search and reads cover other evidence.
  - **Skip:** Do not use Serena for answered questions, ordinary text or configuration searches, or when the project root, required toolchains, language/query support, or host memory capacity are not verified.
  - **Fallback:** Use native source and available graph evidence; leave the symbol question unresolved when neither provides adequate evidence.
```

### R4. Serena LSP with native retrieval

```markdown
- **Serena (LSP)** — Precise symbol and reference evidence.
  - **Scenario:** Use as the selected primary service for recurring precise definition, implementation, or reference queries that native evidence cannot resolve when graph retrieval is not needed.
  - **Boundary:** Serena handles supported symbol queries; native search and reads handle text, configuration, documentation, direct source checks, and other evidence. If Codegraph is already selected, reserve it for recurring cross-file relationships rather than the same symbol query.
  - **Skip:** Do not use Serena for answered queries, ordinary text or configuration searches, or when the project root, required toolchains, language/query support, or host memory capacity are not verified.
  - **Fallback:** Use native evidence for remaining questions and leave the result unresolved when available source does not answer it.
```

### R5. DeusData Codebase Memory with native retrieval

```markdown
- **DeusData/codebase-memory-mcp** — Graph, outline, and symbol-source retrieval.
  - **Scenario:** Use as the selected primary service for recurring outline, graph-query, and symbol-source needs after its package identity and host operations are verified.
  - **Boundary:** Codebase Memory handles its selected outline, graph, and symbol-source queries; native search and reads handle literals, configuration, documentation, current edits, and uncovered content. Do not duplicate the same query with another selected graph service.
  - **Skip:** Do not add Codebase Memory when an existing graph service already covers the same recurring queries.
  - **Fallback:** Complete truncated source before interpreting it. Use native evidence for empty results, stale coverage, or content the service does not return.
```

### R6. Explicitly partitioned Codegraph and Codebase Memory

```markdown
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file flow, dependency, and impact queries when its package and operations are verified.
  - **Boundary:** Assign Codegraph to cross-file flow, dependency, and impact questions. Use Codebase Memory only for explicitly named outline or symbol-source gaps that its verified output covers and the selected Codegraph route does not. Keep native search and reads for literals, configuration, documentation, edits, and uncovered evidence; do not send the same query to both services.
  - **Skip:** If query domains overlap or cannot be named, do not route the same query through both services; ask the author which primary service to retain rather than replacing an existing selection.
  - **Fallback:** Use native evidence for uncovered or stale results and complete truncated source before drawing conclusions.
- **Codebase Memory** — Graph, outline, and symbol-source retrieval.
  - **Scenario:** Use for recurring outline or symbol-source queries not covered by the selected Codegraph route, after its package and operations are verified.
  - **Boundary:** Codebase Memory handles only its assigned outline or symbol-source gaps; Codegraph retains cross-file relationship queries, and native search and reads handle direct source and other evidence.
  - **Skip:** Do not send a query to Codebase Memory when the selected Codegraph route already covers it.
  - **Fallback:** Complete truncated source before interpreting it; use native evidence for empty results and uncovered content.
```

## Support Layers

### A1. Repomap with native retrieval

```markdown
- **Repomap** — Initial codebase orientation.
  - **Scenario:** Use for an initial outline only when module or symbol paths are unknown and existing outlines do not locate them.
  - **Boundary:** Repomap supplies orientation only; native search and reads locate and verify source bodies, literals, configuration, documentation, and edits.
  - **Skip:** Do not use repomap as a substitute for source bodies or relationship evidence.
  - **Fallback:** Use scoped native search to locate the target; do not impose a separate repomap sequence when native evidence is sufficient.
```

### A2. Repomap with Codegraph and native retrieval

```markdown
- **Repomap** — Initial codebase orientation.
  - **Scenario:** Use when Codegraph is the selected service and a recurring relationship workload needs an initial module or symbol outline that existing outlines do not provide.
  - **Boundary:** Repomap supplies only the missing orientation; Codegraph handles relationships, and native search and reads verify source bodies and cover direct evidence.
  - **Skip:** Do not use repomap when an existing outline already locates the target or as a substitute for relationship queries.
  - **Fallback:** Locate the target with scoped native search without adding an outline route when native evidence suffices.
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file relationship questions when Codegraph is the selected service.
  - **Boundary:** Codegraph owns relationship queries; repomap supplies only initial orientation, and native search and reads verify source and cover direct evidence.
  - **Skip:** Skip graph exploration when no relationship question remains.
  - **Fallback:** Use native evidence for unavailable or uncovered graph results.
```

### A3. Semantic search with native retrieval

```markdown
- **@teknologika/mcp-codebase-search** — Conceptual code candidates.
  - **Scenario:** Use when a conceptual task is not located by identifiers or structural queries and package identity, language coverage, index freshness, and host support are verified.
  - **Boundary:** Semantic search supplies candidate code chunks; native search and reads verify them against current source and handle exact text, configuration, documentation, edits, and uncovered evidence.
  - **Skip:** Do not add a second semantic-search service when an existing one covers the same conceptual queries.
  - **Fallback:** Use structural and native searches for evidence the semantic index does not return; trace relationships only when a flow question remains.
```

### A4. Codegraph with semantic search and native retrieval

```markdown
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file relationship queries when Codegraph is the selected service.
  - **Boundary:** Codegraph handles relationships; semantic search supplies candidate code chunks, and native search and reads verify candidates against current source.
  - **Skip:** Skip graph queries when no relationship question remains.
  - **Fallback:** Use native evidence for uncovered or stale graph results.
- **@teknologika/mcp-codebase-search** — Conceptual code candidates.
  - **Scenario:** Use when conceptual searches remain unresolved by identifiers or Codegraph's structural results and package identity, language coverage, index freshness, and host support are verified.
  - **Boundary:** Semantic search supplies candidate code chunks only; Codegraph retains relationship queries, and native search and reads verify candidates and cover direct evidence.
  - **Skip:** Do not add semantic search when an existing semantic service covers the same queries or structural retrieval already locates the code.
  - **Fallback:** Use structural and native searches for missing source; trace relationships only when a flow question remains.
```

### A5. AST symbol extractor with native retrieval

```markdown
- **syphon1c/code-scale-mcp** — AST symbol-source extraction.
  - **Scenario:** Use for symbol-body queries in a supported language after its package identity and extraction support are verified.
  - **Boundary:** The AST extractor supplies symbol-level source; native search and reads verify current code and handle literals, configuration, documentation, edits, and relationship evidence.
  - **Skip:** Do not add an extractor when an existing symbol tool or bounded native reads already provide the required body, or use AST extraction as a substitute for semantic search.
  - **Fallback:** Use existing symbol tools or bounded native reads when extraction is unavailable or does not locate the body.
```

### A6. Codegraph with AST symbol extraction and native retrieval

```markdown
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file relationship queries when Codegraph is the selected service.
  - **Boundary:** Codegraph handles relationship evidence; the AST extractor handles only the symbol-body gap, while native search and reads verify current source and cover direct evidence.
  - **Skip:** Skip graph queries when no relationship question remains.
  - **Fallback:** Use native relationship evidence when graph results are unavailable or incomplete.
- **syphon1c/code-scale-mcp** — AST symbol-source extraction.
  - **Scenario:** Use for a supported symbol-body query that remains unlocated after the selected graph and native evidence.
  - **Boundary:** The extractor supplies symbol-level source only; Codegraph retains relationship queries, and native search and reads verify the returned body.
  - **Skip:** Do not add AST extraction when Codegraph, Serena, another existing symbol tool, or bounded native reads already provide the body.
  - **Fallback:** Use bounded native reads or an existing symbol tool when extraction is unavailable or incomplete.
```

### A7. RTK with native retrieval

```markdown
- **RTK** — Supported command-output filtering.
  - **Scenario:** Use command filters when supported Git, build, test, or log output is truncated or repeatedly exceeds the host output limit.
  - **Boundary:** RTK filters shell-command output only; it does not filter MCP schemas. Keep raw output available when filtering obscures evidence.
  - **Fallback:** Use bounded native commands when RTK integration is unavailable or filtered output omits needed evidence.
```

### A8. Codegraph with RTK and native retrieval

```markdown
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file relationship questions when Codegraph is the selected service.
  - **Boundary:** Codegraph handles relationships; RTK filters supported Git/build/test/log command output only, while native search and reads cover direct source and uncovered evidence.
  - **Skip:** Skip graph exploration when no relationship question remains.
  - **Fallback:** Use native evidence for graph coverage gaps.
- **RTK** — Supported command-output filtering.
  - **Scenario:** Use filters for supported Git, build, test, or log output that is truncated or repeatedly exceeds the host output limit.
  - **Boundary:** RTK filters shell-command output only; Codegraph retains relationship questions, and RTK does not filter MCP schemas.
  - **Fallback:** Use bounded native commands and retain raw output when RTK is unavailable or filtering obscures evidence.
```

### A9. MCP Slim schema gateway with selected MCP services

```markdown
- **MCP Slim gateway** — Tool discovery and proxy routing for selected MCP services.
  - **Scenario:** Use `dopatools/mcp-slim` when the host eagerly loads the definitions of MCP services already selected for the project and lacks native deferred discovery; verify package identity and proxy support for those services.
  - **Boundary:** MCP Slim handles schema discovery and proxy routing only; downstream MCP services retain their retrieval responsibilities, and native search and reads cover direct source and uncovered evidence.
  - **Skip:** If host-native deferred discovery is already selected for these services, retain it instead of adding a gateway for the same purpose.
  - **Fallback:** Retain direct MCP connections for services the gateway cannot proxy; do not route a service through an unverified proxy.
```

### A10. Codegraph with MCP Slim schema gateway and native retrieval

```markdown
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file relationship queries when Codegraph is the selected service.
  - **Boundary:** Codegraph handles relationships; MCP Slim handles verified schema discovery/proxy routing, while native search and reads cover direct source and other evidence.
  - **Fallback:** Retain a direct Codegraph MCP connection if the gateway cannot proxy it; use native evidence for graph coverage gaps.
- **MCP Slim gateway** — Tool discovery and proxy routing for selected MCP services.
  - **Scenario:** Use with the selected Codegraph service when the host eagerly loads its tool definitions, native deferred discovery is unavailable, and proxy support is verified.
  - **Boundary:** MCP Slim handles schema discovery and proxy routing only; Codegraph retains its relationship-query role, and native search and reads handle direct source and uncovered evidence.
  - **Skip:** If native deferred discovery is already selected for Codegraph, retain that route instead of adding the gateway.
  - **Fallback:** Keep a direct Codegraph connection if MCP Slim cannot proxy it.
```

### A11. Codegraph with RTK, MCP Slim, and native retrieval

```markdown
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario:** Use for recurring cross-file relationship queries when Codegraph is the selected service.
  - **Boundary:** Codegraph handles relationships; MCP Slim handles schema discovery, RTK filters supported shell output, and native search and reads cover direct source and other evidence.
  - **Fallback:** Use native evidence when graph results are unavailable or incomplete.
- **RTK** — Supported command-output filtering.
  - **Scenario:** Use filters when supported Git, build, test, or log output is truncated or repeatedly exceeds the host output limit.
  - **Boundary:** RTK filters shell-command output only; MCP Slim handles tool-schema discovery, and RTK does not filter MCP schemas.
  - **Fallback:** Use bounded native commands and retain raw output when RTK is unavailable or filtering obscures evidence.
- **MCP Slim gateway** — Tool discovery and proxy routing for selected MCP services.
  - **Scenario:** Use with the selected Codegraph service when the host eagerly loads its definitions and lacks native deferred discovery, with proxy support verified.
  - **Boundary:** MCP Slim handles schema discovery/proxy routing only; Codegraph retains relationship queries, RTK handles shell output, and native search and reads cover direct source and other evidence.
  - **Skip:** If native deferred discovery is already selected for Codegraph, retain that route instead of adding the gateway.
  - **Fallback:** Keep a direct Codegraph connection if the gateway cannot proxy it.
```
