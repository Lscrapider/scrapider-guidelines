# IDE and Library Documentation Scenarios

Use the scenario matching the selected capabilities. Each sample keeps distinct capabilities as
peer entries and preserves their separate responsibilities. Apply the
[description rules](../agents-md-authoring.md#capability-entry-format) and project constraints.

## JetBrains MCP

```md
- **JetBrains IDE MCP** — IDE semantic operations and checks.
  - **Scenario**: In the explicitly identified IDE-open project:
    - Rename variables, fields, methods, classes, or other code symbols with reference-aware refactoring; review string and comment changes in the diff, and verify reflective uses separately.
    - For changed files, run supported IDE diagnostics and single-file incremental compilation, followed by best-effort formatting according to project style.
    - When dependency signatures or versions are unclear, inspect third-party declarations and the resolved version on the current classpath.
    - Debug a reproduced runtime failure when static evidence is insufficient.
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

## Codegraph and JetBrains

```md
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario**: Unresolved cross-file call, dependency, or impact questions covered by the project index.
  - **Boundary**: Codegraph supplies relationship evidence; JetBrains owns IDE semantic operations and checks.

- **JetBrains IDE MCP** — IDE semantic operations and checks.
  - **Scenario**: In the explicitly identified IDE-open project:
    - Rename variables, fields, methods, classes, or other code symbols with reference-aware refactoring; review string and comment changes in the diff, and verify reflective uses separately.
    - For changed files, run supported IDE diagnostics and single-file incremental compilation, followed by best-effort formatting according to project style.
    - When dependency signatures or versions are unclear, inspect third-party declarations and the resolved version on the current classpath.
    - Debug a reproduced runtime failure when static evidence is insufficient.
  - **Boundary**: Codegraph owns relationship investigation; JetBrains owns IDE semantic operations and checks.
```

## Codegraph and Context7

```md
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario**: Unresolved cross-file call, dependency, or impact questions covered by the project index.
  - **Boundary**: Codegraph supplies relationship evidence only.

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
    - For changed files, run supported IDE diagnostics and single-file incremental compilation, followed by best-effort formatting according to project style.
    - When dependency signatures or versions are unclear, inspect third-party declarations and the resolved version on the current classpath.
    - Debug a reproduced runtime failure when static evidence is insufficient.
  - **Boundary**: IDE declarations establish installed dependency facts; Context7 handles unresolved upstream version and compatibility facts.

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

## Codegraph, JetBrains, and Context7

```md
- **Codegraph** — Code relationships and impact evidence.
  - **Scenario**: Unresolved cross-file call, dependency, or impact questions covered by the project index.
  - **Boundary**: Codegraph supplies relationship evidence; JetBrains owns IDE semantic operations and checks.

- **JetBrains IDE MCP** — IDE semantic operations and checks.
  - **Scenario**: In the explicitly identified IDE-open project:
    - Rename variables, fields, methods, classes, or other code symbols with reference-aware refactoring; review string and comment changes in the diff, and verify reflective uses separately.
    - For changed files, run supported IDE diagnostics and single-file incremental compilation, followed by best-effort formatting according to project style.
    - When dependency signatures or versions are unclear, inspect third-party declarations and the resolved version on the current classpath.
    - Debug a reproduced runtime failure when static evidence is insufficient.
  - **Boundary**: Codegraph owns relationship investigation; IDE declarations establish installed dependency facts; Context7 handles unresolved upstream version and compatibility facts.

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
    - For changed files, run supported IDE diagnostics and single-file incremental compilation, followed by best-effort formatting according to project style.
    - When dependency signatures or versions are unclear, inspect third-party declarations and the resolved version on the current classpath.
    - Debug a reproduced runtime failure when static evidence is insufficient.
  - **Boundary**: The IDE owns semantic edits and checks; Serena supplies only symbol evidence the IDE cannot resolve.

- **Serena (LSP)** — Supplementary symbol evidence.
  - **Scenario**: The author explicitly selected Serena, recurring symbol queries remain unresolved after IDE inspection, and its support and resource requirements have been verified.
  - **Boundary**: Serena fills the identified query gap; JetBrains retains semantic edits and checks. Do not repeat answered queries or run competing semantic edits.
```

## Standards Review Collaboration

Declare the standards-reviewer role under project collaboration; see [Planning and Collaboration](planning-and-collaboration.md#collaboration).

## Selection Boundaries

| Responsibility overlap | Together | Separate choices |
| --- | --- | --- |
| Graph retrieval and IDE source operations | [Codegraph and JetBrains](#codegraph-and-jetbrains) keep relationship evidence and semantic operations with different owners. | Select either the retrieval profile or the [IDE entry](#jetbrains-mcp) when only that responsibility applies. |
| IDE declarations and external docs | [JetBrains and Context7](#jetbrains-and-context7) separates installed project facts from upstream version evidence. | Use [Context7](#context7) for unresolved external facts; rely on local artifacts directly when they already answer the question. |
| IDE and LSP symbol support | [JetBrains and Serena](#jetbrains-and-serena) requires a verified symbol-query gap. | Select the IDE alone or the standalone Serena profile in the [retrieval scenarios](retrieval.md). |
| Scripted candidates and standards findings | [Standards review collaboration](#standards-review-collaboration) is declared under collaboration, not as a tool capability. | See the [collaboration scenarios](planning-and-collaboration.md#collaboration). |
