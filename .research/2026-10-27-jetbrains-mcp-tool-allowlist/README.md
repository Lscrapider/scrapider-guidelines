# JetBrains MCP explicit tool allowlist research

Date: 2026-10-27 (Asia/Shanghai). Status: approved and applied with exactly ten tools; debugging is disabled.

The final decision and live-session verification are in [approved/RESULT.md](approved/RESULT.md).
Files under `proposed/` and `raw/` preserve the original 22-tool proposal and research observations.

- REPORT.md: background, method, results, limitations, and proposed next documentation change.
- tool-review.csv: all 45 original tools; `approved_enabled` is the final decision, while the
  proposal columns preserve the initial review context.
- proposed/codex-allowlist.proposed.toml: superseded original proposal with 22 exported names.
- proposed/enabled-tools.json: the same candidate list for reproducible probes.
- proposed/capability-setup-addition.proposed.md: draft of the future documentation addition.
- raw/local-environment.json: selected local client/IDE settings; endpoint and home paths redacted.
- raw/session-tool-catalog.json: current-session tool definitions, not a new client startup.
- raw/http-allowlist-probes.json: JSON-RPC tool-catalog responses for 10 temporary HTTP sessions.
- raw/codex-transient-config-check.json: CLI acceptance of a non-persistent enabled_tools override.
- raw/local-filter-bytecode.txt: relevant installed-plugin bytecode output supporting name parsing.
- raw/source-index.json: official sources used, source kinds and evidence boundaries.
- probes/tool_allowlist_probe.py: read-only initialize/tools/list probe, with session cleanup.

The `.proposed.toml` file is a fragment to merge into an existing server table; it intentionally
omits a connection URL and must not replace the user's whole configuration.

Reproduce directory probes only against the user's authorized local IDE endpoint:

```sh
python3 probes/tool_allowlist_probe.py \
  --url "$JETBRAINS_MCP_URL" \
  --proposal-json proposed/enabled-tools.json \
  --output /tmp/jetbrains-tool-allowlist-probes.json
```

Set JETBRAINS_MCP_URL to the active IDE's localhost HTTP Stream endpoint. The probe never calls
tools/call, never enables tools persistently, and never reads project source or runtime values.
The stored endpoint is replaced by a placeholder; session identifiers are not archived.
