# Capability Setup and Initialization

Install, initialize, and verify the capabilities a project adopted from
[Capability Recommendations](capability-recommendations.md). This is the initialization pass that
runs once, when the author asks to set up the adopted capabilities — normally after they have
confirmed the authored `AGENTS.md`. Authoring only points here: do not install anything during
authoring or immediately after writing the file, and do not treat adoption as authorization to
install.

- [General procedure](#general-procedure)
- [Codegraph (MCP and CLI)](#codegraph-mcp-and-cli)
- [JetBrains IDE tool surface](#jetbrains-ide-tool-surface)
- [External library docs service (context7)](#external-library-docs-service-context7)
- [Serena with language servers](#serena-with-language-servers)
- [CLI tools](#cli-tools)
- [Skills, plugins, and bundled agents](#skills-plugins-and-bundled-agents)

## General Procedure

For a confirmed setup request, perform the steps directly and report the evidence. Check what is
already installed and verified first, run only the missing steps, and do not reinstall or re-verify
a tool that already works in this environment.

Every selected tool needs three steps before its entry may be treated as usable:

1. **Install** — obtain the tool and register it with the host where registration applies.
2. **Initialize** — perform the per-project setup the tool requires. Registration alone does not
   make a tool usable: a registered Codegraph without a built index serves no queries, and a
   configured IDE surface whose IDE-side enablement is missing serves no calls.
3. **Verify** — confirm with one real call or command that the host exposes the tool and that it
   returns current project evidence. State what was verified and what was not.

Do not pin or record tool versions. Install the current stable release and confirm the exact
command shapes from the tool's own help or official documentation at run time; the commands in this
reference are verified shapes, and a mismatch means re-checking the tool, not forcing the recorded
text. A step that cannot be completed or verified is an environment blocker to report; do not write
the entry into `AGENTS.md` as usable or hide the gap behind a fallback policy.

Check visibility where the work will happen. A host may need a restart before newly added MCP tools
appear, and a subagent session started earlier may not see tools added afterward; verify delegated
runs can call the tools their role relies on. Setup commands and installation history stay in this
exchange; the project file records capability entries only.

## Codegraph (MCP and CLI)

The `@colbymchenry/codegraph` npm package provides a CLI and an MCP server whose main tool is
`codegraph_explore`.

- Install: `npm install -g @colbymchenry/codegraph`; confirm with `codegraph --version`. Register
  the MCP server with `codegraph install` on a supported agent, or add it to the host's MCP
  configuration manually.
- Initialize per project: run `codegraph init [path]` once in the project root; it creates the
  `.codegraph/` index directory and builds the initial index. Refresh changed files with
  `codegraph sync`, rebuild a stale or damaged index with `codegraph index`, and clear a stale lock
  that blocks indexing with `codegraph unlock`.
- Verify: `codegraph status [path]` reports index statistics, and one `codegraph_explore` call
  confirms both exposure and index discovery. The tool uses the nearest `.codegraph/` index at or
  above the given `projectPath`; a copy or sibling checkout must point at a path whose index
  exists, or build its own.
- Boundary: the index is per project root. Handle stale or missing coverage per the
  [query-scope rules](retrieval-operations.md#codegraph-query-scope); a coverage gap is not a
  reason to re-index on every task.

## JetBrains IDE Tool Surface

The IDE exposes an MCP server for the projects it has open. Enabling it is a manual IDE step the
agent cannot perform, and it belongs to the author.

- Install: use the IDE's MCP Server plugin, bundled and enabled by default in recent IntelliJ
  IDEA; re-enable it under the Plugins settings if disabled.
- Initialize: open the target project in the IDE; under Settings | Tools | MCP Server, select
  "Enable MCP Server", confirm the access dialog, and apply the settings. Copy the SSE, Stdio, or
  HTTP connection config into the host's MCP configuration and restart the client. The server is
  available only while that project is open, and every call must target it with `projectPath`.
- Exposed tools: the server exposes its tools individually, and the surface is heavyweight. Under
  Settings | Tools | MCP Server | Exposed Tools, enable only the tool groups the project's selected
  capabilities use; leave the rest off, especially the database tools. When a group the project
  does not use is enabled, prompt the author to disable it rather than leaving it on.
- Verify: call a read-only tool such as `get_project_modules` with that `projectPath`. An IDE
  without the project open, or a call against a different path, is an explicit blocker to surface,
  not a silent fallback.
- Operation boundaries follow the
  [IDE tool surface conditions](capability-recommendations.md#ide-tool-surface-on-an-open-project).

## External Library Docs Service (context7)

- Install: register the docs service as an MCP server in the host, directly or through a hub.
- Initialize: none beyond registration; there is no project-level state.
- Verify: resolve one known library identifier, and confirm the exposed tool names and parameters
  on that host, since hubs and hosts may rename them.

## Serena with Language Servers

Serena is not a default recommendation; adopt it only under its
[selection conditions](capability-recommendations.md#serena-with-language-servers).

- Install and register per Serena's current official documentation; verify the command on this host
  instead of copying it from memory.
- Initialize per project: confirm the project root, required language toolchains, and host memory
  capacity before first use, and run its project activation or indexing step where its setup
  defines one.
- Verify: one definition or reference query on a known symbol returns current source.

## CLI Tools

Install RTK, OpenSpec, and similar CLI capabilities from their official documentation, and verify
with a version or help call before routing assigns them work. OpenSpec also carries a per-project
initialization that creates its specification structure; run it once per adopted project, and do
not run it during [authoring](capability-recommendations.md#planning-and-execution).

## Skills, Plugins, and Bundled Agents

Skills and plugins install through the host's discovery mechanisms — skill directories or the
plugin marketplace — and have no per-project initialization. Verify by listing the host's
available skills or by loading the skill once. Install Scrapider Guidelines itself at the user or
project scope the host defines.

The bundled [collaboration roles](agents-md-authoring.md#bundled-collaboration-roles) need their
definitions supplied to the host as custom agents, preserving each file's `name` and `description`;
their presence in this skill does not register them. Verify the host actually offers a role before
a workflow depends on delegation; when it does not, keep the responsibilities with the primary
agent per the authoring rules and leave the registration step with the author.

Role definitions carry no tools: the tools available to a delegated role come from the host
session. Set up the capabilities the project adopted with the sections above, then confirm the
delegated run can call them.
