# IDE and Library Documentation Scenarios

Use the scenario matching the selected capabilities. Each sample keeps distinct capabilities as
peer entries; local search and ordinary file operations appear only as boundaries or fallback paths.
Apply the [description rules](../agents-md-authoring.md#capability-entry-format) and project constraints.

## JetBrains MCP

```md
- **JetBrains IDE MCP** — IDE semantic operations and checks.
  - **Scenario**: In the explicitly identified IDE-open project:
    - Rename variables, fields, methods, classes, or other code symbols with reference-aware refactoring; review string and comment changes in the diff, and verify reflective uses separately.
    - After native code edits, diagnose edited files, run supported single-file incremental compilation, and finish with IDE formatting that respects project style.
    - When dependency signatures or versions are unclear, inspect third-party declarations and the resolved version on the current classpath.
    - Debug a reproduced runtime failure when static evidence is insufficient.
  - **Boundary**: Local search owns ordinary source investigation; ordinary file creation and editing stay with local tools.
  - **Skip**: An unambiguous rename may use local edits with a reference sweep when the project’s tool-routing contract permits it.
  - **Fallback**: If the project is not open, the operation is unavailable, or a call times out, use local tools and project checks and report verification gaps. On formatter failure, finish locally.
```

## Context7

```md
- **Context7** — External library versions, compatibility, and API facts.
  - **Scenario**:
    - For a new dependency, fork, renamed library, or version upgrade, verify the current stable release and compatibility with the project's framework line.
    - For a version-sensitive API fact, query only after repository examples and local dependency artifacts cannot answer it.
  - **Boundary**:
    - Match the documentation to the target library coordinates; reject a different-library result.
    - Local dependency artifacts establish installed signatures; declared project versions take precedence over external examples.
    - Verify language-level correctness by compilation; documentation does not replace this check.
  - **Skip**: Do not query for routine implementation already demonstrated by the repository.
  - **Fallback**: Consult official library documentation when the service is unavailable or lacks matching evidence; state unresolved facts.
```

## Codegraph, Native Tools, and JetBrains

```md
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario**: Unresolved cross-file call, dependency, or impact questions covered by the project index.
  - **Boundary**: Codegraph supplies relationship evidence; local search handles literals and uncovered source. JetBrains owns semantic edits and IDE checks.
  - **Fallback**: Use local search and targeted reads for unavailable, stale, or incomplete graph evidence.

- **JetBrains IDE MCP** — IDE semantic operations and checks.
  - **Scenario**: In the explicitly identified IDE-open project:
    - Rename variables, fields, methods, classes, or other code symbols with reference-aware refactoring; review string and comment changes in the diff, and verify reflective uses separately.
    - After native code edits, diagnose edited files, run supported single-file incremental compilation, and finish with IDE formatting that respects project style.
    - When dependency signatures or versions are unclear, inspect third-party declarations and the resolved version on the current classpath.
    - Debug a reproduced runtime failure when static evidence is insufficient.
  - **Boundary**: Codegraph owns relationship investigation; ordinary file creation and editing stay with local tools.
  - **Skip**: An unambiguous rename may use local edits with a reference sweep when the project’s tool-routing contract permits it.
  - **Fallback**: If the project is not open, the operation is unavailable, or a call times out, use local tools and project checks and report verification gaps. On formatter failure, finish locally.
```

## Codegraph, Native Tools, and Context7

```md
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario**: Unresolved cross-file call, dependency, or impact questions covered by the project index.
  - **Boundary**: Codegraph supplies relationship evidence; local search handles literals and uncovered source. Ordinary file edits remain with local file tools.
  - **Fallback**: Use local search and targeted reads for unavailable, stale, or incomplete graph evidence.

- **Context7** — External library versions, compatibility, and API facts.
  - **Scenario**:
    - For a new dependency, fork, renamed library, or version upgrade, verify the current stable release and compatibility with the project's framework line.
    - For a version-sensitive API fact, query only after repository examples and local dependency artifacts cannot answer it.
  - **Boundary**:
    - Match the documentation to the target library coordinates; reject a different-library result.
    - Local dependency artifacts establish installed signatures; declared project versions take precedence over external examples.
    - Verify language-level correctness by compilation; documentation does not replace this check.
  - **Skip**: Do not query for routine implementation already demonstrated by the repository.
  - **Fallback**: Consult official library documentation when the service is unavailable or lacks matching evidence; state unresolved facts.
```

## JetBrains and Context7

```md
- **JetBrains IDE MCP** — IDE semantic operations and checks.
  - **Scenario**: In the explicitly identified IDE-open project:
    - Rename variables, fields, methods, classes, or other code symbols with reference-aware refactoring; review string and comment changes in the diff, and verify reflective uses separately.
    - After native code edits, diagnose edited files, run supported single-file incremental compilation, and finish with IDE formatting that respects project style.
    - When dependency signatures or versions are unclear, inspect third-party declarations and the resolved version on the current classpath.
    - Debug a reproduced runtime failure when static evidence is insufficient.
  - **Boundary**: Local search owns ordinary source investigation; ordinary file creation and editing stay with local tools. IDE declarations establish installed dependency facts; Context7 handles unresolved upstream version and compatibility facts.
  - **Skip**: An unambiguous rename may use local edits with a reference sweep when the project’s tool-routing contract permits it.
  - **Fallback**: If the project is not open, the operation is unavailable, or a call times out, use local tools and project checks and report verification gaps. On formatter failure, finish locally.

- **Context7** — External library versions, compatibility, and API facts.
  - **Scenario**:
    - For a new dependency, fork, renamed library, or version upgrade, verify the current stable release and compatibility with the project's framework line.
    - For a version-sensitive API fact, query only after repository examples and local dependency artifacts cannot answer it.
  - **Boundary**:
    - Match the documentation to the target library coordinates; reject a different-library result.
    - IDE declarations and local artifacts establish installed signatures; declared project versions take precedence over external examples.
    - Verify language-level correctness by compilation; documentation does not replace this check.
  - **Skip**: Do not query for routine implementation already demonstrated by the repository.
  - **Fallback**: Consult official library documentation when the service is unavailable or lacks matching evidence; state unresolved facts.
```

## Codegraph, Native Tools, JetBrains, and Context7

```md
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario**: Unresolved cross-file call, dependency, or impact questions covered by the project index.
  - **Boundary**: Codegraph supplies relationship evidence; local search handles literals and uncovered source. JetBrains owns semantic edits and IDE checks.
  - **Fallback**: Use local search and targeted reads for unavailable, stale, or incomplete graph evidence.

- **JetBrains IDE MCP** — IDE semantic operations and checks.
  - **Scenario**: In the explicitly identified IDE-open project:
    - Rename variables, fields, methods, classes, or other code symbols with reference-aware refactoring; review string and comment changes in the diff, and verify reflective uses separately.
    - After native code edits, diagnose edited files, run supported single-file incremental compilation, and finish with IDE formatting that respects project style.
    - When dependency signatures or versions are unclear, inspect third-party declarations and the resolved version on the current classpath.
    - Debug a reproduced runtime failure when static evidence is insufficient.
  - **Boundary**: Codegraph owns relationship investigation; ordinary file creation and editing stay with local tools. IDE declarations establish installed dependency facts; Context7 handles unresolved upstream version and compatibility facts.
  - **Skip**: An unambiguous rename may use local edits with a reference sweep when the project’s tool-routing contract permits it.
  - **Fallback**: If the project is not open, the operation is unavailable, or a call times out, use local tools and project checks and report verification gaps. On formatter failure, finish locally.

- **Context7** — External library versions, compatibility, and API facts.
  - **Scenario**:
    - For a new dependency, fork, renamed library, or version upgrade, verify the current stable release and compatibility with the project's framework line.
    - For a version-sensitive API fact, query only after repository examples and local dependency artifacts cannot answer it.
  - **Boundary**:
    - Match the documentation to the target library coordinates; reject a different-library result.
    - IDE declarations and local artifacts establish installed signatures; declared project versions take precedence over external examples.
    - Verify language-level correctness by compilation; documentation does not replace this check.
  - **Skip**: Do not query for routine implementation already demonstrated by the repository.
  - **Fallback**: Consult official library documentation when the service is unavailable or lacks matching evidence; state unresolved facts.
```

## JetBrains and Serena

```md
- **JetBrains IDE MCP** — Semantic operations and checks in the open project.
  - **Scenario**: In the explicitly identified IDE-open project:
    - Rename variables, fields, methods, classes, or other code symbols with reference-aware refactoring; review string and comment changes in the diff, and verify reflective uses separately.
    - After native code edits, diagnose edited files, run supported single-file incremental compilation, and finish with IDE formatting that respects project style.
    - When dependency signatures or versions are unclear, inspect third-party declarations and the resolved version on the current classpath.
    - Debug a reproduced runtime failure when static evidence is insufficient.
  - **Boundary**: The IDE owns semantic edits and checks; Serena supplies only symbol evidence the IDE and local source cannot resolve. Ordinary file edits remain local.
  - **Skip**: An unambiguous rename may use local edits with a reference sweep when the project’s tool-routing contract permits it.
  - **Fallback**: If the project is not open, the operation is unavailable, or a call times out, use local tools and project checks and report verification gaps. On formatter failure, finish locally.

- **Serena (LSP)** — Supplementary symbol evidence.
  - **Scenario**: The author explicitly selected Serena, recurring symbol queries remain unresolved by the IDE and local evidence, and its support and resource requirements have been verified.
  - **Boundary**: Serena fills the identified query gap; JetBrains retains semantic edits and checks. Do not repeat answered queries or run competing semantic edits.
  - **Fallback**: Use available IDE or local evidence for unsupported queries and report any unresolved binding.
```

## IDE Inspections and Standards Review

The standards-reviewer definition owns inspection-library loading and execution. The project entry
states the review responsibility and handoff, not the reviewer's internal tool sequence.

```md
- **scrapider-standards-reviewer** — Review against applicable engineering rules.
  - **Scenario**: Standards review is requested or required by project policy.
  - **Boundary**: The reviewer owns mechanical and semantic standards checks while remaining read-only; the primary agent owns fixes, integration, and acceptance. Functional correctness stays with the functional-review route.
  - **Fallback**: Follow the existing standards-review dispatch policy if the role is unavailable, retaining the same rules and coverage reporting.
```

## Selection Boundaries

| Responsibility overlap | Together | Separate choices |
| --- | --- | --- |
| Graph retrieval and IDE source operations | [Codegraph and JetBrains](#codegraph-native-tools-and-jetbrains) keep relationship evidence and semantic operations with different owners. | Select either the retrieval profile or the [IDE entry](#jetbrains-mcp) when only that responsibility applies. |
| IDE declarations and external docs | [JetBrains and Context7](#jetbrains-and-context7) separates installed project facts from upstream version evidence. | Use [Context7](#context7) for unresolved external facts; rely on local artifacts directly when they already answer the question. |
| IDE and LSP symbol support | [JetBrains and Serena](#jetbrains-and-serena) requires a verified symbol-query gap. | Select the IDE alone or the standalone Serena profile in the [retrieval scenarios](retrieval.md). |
| Scripted candidates and standards findings | [The standards-review role](#ide-inspections-and-standards-review) owns its internal inspections and rule judgment; the primary agent receives the findings. | Use the standalone reviewer in the [collaboration scenarios](planning-and-collaboration.md#collaboration) when inspections cannot run. |
