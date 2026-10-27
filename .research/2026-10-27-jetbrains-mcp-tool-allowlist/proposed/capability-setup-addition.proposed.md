# Proposed Capability Setup addition — pending user review

Target: plugins/scrapider-guidelines/skills/scrapider-guidelines/references/project/capability-setup.md,
JetBrains IDE Tool Surface.

Insert a required client-configuration step between copying the connection config and restarting
the client. Retain the existing IDE-side enablement and tool-selection instructions.

> **Explicit tool allowlist** — after registering the connection, configure the target host's
> supported MCP tool allowlist before treating setup as complete. Copying a connection URL or
> enabling IDE tool groups alone does not complete this step. Select the tools needed by the
> project's adopted IDE operations; include the adopted inspection and debugger workflows rather
> than silently dropping them. Use the tool names actually exposed in that environment.
>
> For Codex, merge `enabled_tools = [...]` into the existing `[mcp_servers.<server-id>]` table.
> Use server-exported names such as `get_file_problems` and `xdebug_get_stack`, without the
> host-added `mcp__...__` invocation prefix. Preserve connection, authentication, timeout, and
> unrelated settings. Check for an existing `disabled_tools` entry, which takes precedence for
> overlapping names.
>
> Keep IDE-side Exposed Tools aligned with the selected operations. Do not enable database tools
> as part of IDE setup. Enabling a tool does not grant permission to run arbitrary commands,
> inspection scripts, or debug launches.
>
> Reconnect or restart the client, then verify the effective tool catalog includes every selected
> tool and excludes unselected tools. Make one read-only call with the intended `projectPath`.
> A missing required tool, unexpected tool exposure, or an unavailable IDE-open project is a setup
> gap to report. An already-running session's old tool catalog is not verification of the new
> allowlist.
>
> Other hosts must use their own verified filter mechanism. JetBrains also implements
> `IJ_MCP_ALLOWED_TOOLS` (Stdio environment variable; SSE/HTTP request header), but its internal
> naming and filter behavior must be verified for that installation. Do not assume Codex names,
> empty-list behavior, or a simple intersection with all IDE filters.

The candidate Codex list is in `proposed/codex-allowlist.proposed.toml`. It has 22 names, grouped as
9 core IDE tools, 1 existing inspection-script runner, and 12 debugger tools. A project that
explicitly omits the debugger workflow can select the 10 core/inspection names. No list has been
applied to the user's client or IDE.
