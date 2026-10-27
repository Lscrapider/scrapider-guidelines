#!/usr/bin/env python3
"""Read-only MCP tools/list probes; never invokes a tool or edits client/IDE settings."""

import argparse
import json
import urllib.parse
import urllib.request
import urllib.error
from pathlib import Path


def decode_rpc(text):
    try:
        return json.loads(text)
    except json.JSONDecodeError:
        messages = []
        for line in text.splitlines():
            if line.startswith("data:"):
                try:
                    messages.append(json.loads(line[5:].strip()))
                except json.JSONDecodeError:
                    pass
        if not messages:
            raise ValueError("No JSON-RPC response in body")
        return messages[-1]


def request_rpc(url, headers, method, params=None, rpc_id=None):
    body = {"jsonrpc": "2.0", "method": method}
    if params is not None:
        body["params"] = params
    if rpc_id is not None:
        body["id"] = rpc_id
    req = urllib.request.Request(
        url, data=json.dumps(body).encode(), headers=headers, method="POST"
    )
    # Do not forward a local request to a configured external proxy.
    opener = urllib.request.build_opener(urllib.request.ProxyHandler({}))
    with opener.open(req, timeout=7) as response:
        raw = response.read().decode()
        return response.status, dict(response.headers), raw


def run_case(url, label, allowlist):
    headers = {"Content-Type": "application/json",
               "Accept": "application/json, text/event-stream"}
    if allowlist is not None:
        headers["IJ_MCP_ALLOWED_TOOLS"] = allowlist
    result = {"case": label, "request_allowlist": allowlist,
              "endpoint": "<LOCAL_IDE_MCP_URL>"}
    initialized = False
    try:
        status, response_headers, raw = request_rpc(
            url, headers, "initialize",
            {"protocolVersion": "2025-03-26", "capabilities": {},
             "clientInfo": {"name": "scrapider-allowlist-probe", "version": "1.0"}},
            1
        )
        result["initialize_http_status"] = status
        result["initialize_response"] = decode_rpc(raw)
        session = next((v for k, v in response_headers.items()
                        if k.lower() == "mcp-session-id"), None)
        result["session_id_returned"] = bool(session)
        if session:
            headers["Mcp-Session-Id"] = session
        initialized = True
        status, _, _ = request_rpc(url, headers, "notifications/initialized")
        result["initialized_http_status"] = status
        status, _, raw = request_rpc(url, headers, "tools/list", {}, 2)
        message = decode_rpc(raw)
        result["tools_list_http_status"] = status
        result["tools_list_response"] = message
        tools = message.get("result", {}).get("tools", [])
        result["tool_names"] = sorted(tool["name"] for tool in tools)
        result["tool_count"] = len(tools)
        if message.get("result", {}).get("nextCursor"):
            result["pagination_incomplete"] = True
    except Exception as error:
        # Store error type only; exception URLs can contain local endpoint details.
        result["error_type"] = type(error).__name__
    finally:
        if initialized:
            req = urllib.request.Request(url, headers=headers, method="DELETE")
            opener = urllib.request.build_opener(urllib.request.ProxyHandler({}))
            try:
                with opener.open(req, timeout=7) as response:
                    result["session_close_http_status"] = response.status
            except urllib.error.HTTPError as error:
                result["session_close_http_status"] = error.code
            except Exception as error:
                result["session_close_error_type"] = type(error).__name__
    return result


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--url", required=True)
    parser.add_argument("--proposal-json", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    parsed = urllib.parse.urlparse(args.url)
    if (parsed.scheme != "http" or parsed.hostname != "localhost"
            or parsed.username or parsed.password or parsed.query or parsed.fragment):
        parser.error("Use an unauthenticated http://localhost:<port>/stream endpoint")
    proposal = json.loads(args.proposal_json.read_text())
    cases = [
        ("baseline_no_header", None),
        ("one_tool", "get_project_modules"),
        ("two_tools_with_spaces", " get_project_modules , get_symbol_info "),
        ("unknown_tool", "__scrapider_nonexistent_probe_tool__"),
        ("empty_header", ""),
        ("disabled_database_tool", "execute_sql_query"),
        ("proposed_full", ",".join(proposal)),
        ("debugger_exported_name", "xdebug_get_debugger_status"),
        ("debugger_internal_name", "get_debugger_status"),
        ("proposed_internal_names", ",".join(
            name[len("xdebug_"):] if name.startswith("xdebug_") else name
            for name in proposal
        )),
    ]
    results = [run_case(args.url, label, value) for label, value in cases]
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(results, indent=2, ensure_ascii=False) + "\n")
    print(json.dumps([{
        "case": r["case"], "tool_count": r.get("tool_count"),
        "error_type": r.get("error_type"),
        "session_close_http_status": r.get("session_close_http_status")
    } for r in results], ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
