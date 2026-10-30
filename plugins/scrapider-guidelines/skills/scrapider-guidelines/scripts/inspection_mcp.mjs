import { execFile } from 'node:child_process';
import { existsSync, readFileSync } from 'node:fs';
import { homedir } from 'node:os';
import { resolve } from 'node:path';
import { promisify } from 'node:util';

const execute = promisify(execFile);
const protocolVersion = '2025-03-26';

function jsonFile(path) {
  if (!existsSync(path)) return {};
  try {
    return JSON.parse(readFileSync(path, 'utf8'));
  } catch (error) {
    throw new Error(`Cannot read MCP configuration ${path}: ${error.message}`);
  }
}

function zcodeServers(project) {
  const scope = (native, shared) => {
    const servers = jsonFile(native).mcp?.servers || {};
    return Object.keys(servers).length ? servers : jsonFile(shared).mcpServers || {};
  };
  return {
    ...scope(resolve(project, '.zcode/config.json'), resolve(project, '.agents/mcp.json')),
    ...scope(resolve(homedir(), '.zcode/cli/config.json'), resolve(homedir(), '.agents/mcp.json')),
  };
}

function claudeServers(project) {
  const user = jsonFile(resolve(homedir(), '.claude.json'));
  const local = user.projects?.[project] || {};
  const disabled = new Set(local.disabledMcpServers || []);
  const userSettings = jsonFile(resolve(homedir(), '.claude/settings.json'));
  const projectSettings = jsonFile(resolve(project, '.claude/settings.json'));
  const localSettings = jsonFile(resolve(project, '.claude/settings.local.json'));
  const settings = [userSettings, projectSettings, localSettings, local];
  const rejected = new Set(settings.flatMap((item) => item.disabledMcpjsonServers || []));
  const approvals = [userSettings, ...(local.hasTrustDialogAccepted ? [projectSettings, localSettings, local] : [])];
  const approved = new Set(approvals.flatMap((item) => item.enabledMcpjsonServers || []));
  const approveAll = Object.assign({}, ...approvals).enableAllProjectMcpServers === true;
  const projectServers = jsonFile(resolve(project, '.mcp.json')).mcpServers || {};
  return Object.fromEntries(Object.entries({
    ...user.mcpServers,
    ...projectServers,
    ...local.mcpServers,
  }).map(([name, config]) => {
    if (disabled.has(name)) return [name, { ...config, enabled: false }];
    if (Object.hasOwn(projectServers, name) && !Object.hasOwn(local.mcpServers || {}, name)) {
      const error = rejected.has(name) ? `Claude Code rejected project MCP ${name} through disabledMcpjsonServers.`
        : !approveAll && !approved.has(name) ? `Claude Code project MCP ${name} is pending approval; approve it in Claude Code first.` : null;
      if (error) return [name, { ...config, inspectionConnectionError: error }];
    }
    return [name, config];
  }));
}

function selectedServer(servers, host) {
  const name = process.env.JETBRAINS_MCP_SERVER;
  const configured = Object.entries(servers).filter(([key]) => name ? key === name : /jetbrains?/i.test(key));
  const matches = configured.filter(([, config]) => config.enable !== false && config.enabled !== false);
  if (configured.length && !matches.length) throw new Error(`JetBrains MCP is disabled in ${host}: ${configured.map(([key]) => key).join(', ')}.`);
  if (!matches.length) return null;
  if (matches.length !== 1) {
    throw new Error(`Multiple JetBrains MCP servers in ${host}: ${matches.map(([key]) => key).join(', ')}. Select JETBRAINS_MCP_SERVER.`);
  }
  const [server, config] = matches[0];
  if (config.inspectionConnectionError) throw new Error(config.inspectionConnectionError);
  return { server, config };
}

function environmentValue(name) {
  const value = process.env[name];
  if (!value) throw new Error(`Required environment variable is missing: ${name}`);
  return value;
}

function configuredString(value) {
  return value.replace(/\$\{([^}:]+)(?::-([^}]*))?\}/g, (_, name, fallback) =>
    process.env[name] ?? fallback ?? environmentValue(name));
}

function connection(selected, host) {
  const { server, config } = selected;
  const tool = 'run_inspection_kts';
  if (config.disabled_tools?.includes(tool) || (config.enabled_tools && !config.enabled_tools.includes(tool))) {
    throw new Error(`${tool} is excluded by ${host}'s configured tool allowlist.`);
  }
  const transport = config.transport || config;
  const type = transport.type || (transport.url ? 'http' : 'stdio');
  if (!['http', 'streamable_http', 'streamable-http', 'sse'].includes(type)) {
    throw new Error(`JetBrains MCP ${server} uses ${type} in ${host}; an HTTP or SSE connection is required.`);
  }
  if (!transport.url) throw new Error(`JetBrains MCP ${server} has no URL in ${host}.`);
  const headers = Object.fromEntries(Object.entries({ ...transport.headers, ...transport.http_headers })
    .map(([name, value]) => [name, configuredString(value)]));
  for (const [name, variable] of Object.entries(transport.env_http_headers || {})) headers[name] = environmentValue(variable);
  if (transport.bearer_token_env_var) headers.Authorization = `Bearer ${environmentValue(transport.bearer_token_env_var)}`;
  return { url: configuredString(transport.url), headers, type: type === 'sse' ? 'sse' : 'http',
    timeout: config.timeoutMs || (config.tool_timeout_sec || 120) * 1000, host, server };
}

async function codexConnection() {
  const { stdout } = await execute('codex', ['mcp', 'list', '--json'], { timeout: 10_000 });
  const selected = selectedServer(Object.fromEntries(JSON.parse(stdout).map((config) => [config.name, config])), 'Codex');
  if (!selected) throw new Error('No JetBrains MCP server is registered in Codex.');
  const detail = await execute('codex', ['mcp', 'get', selected.server, '--json'], { timeout: 10_000 });
  return connection({ server: selected.server, config: JSON.parse(detail.stdout) }, 'Codex');
}

async function connectionOptions(project) {
  if (process.env.JETBRAINS_MCP_URL) {
    const url = process.env.JETBRAINS_MCP_URL;
    return { url, headers: JSON.parse(process.env.JETBRAINS_MCP_HEADERS || '{}'),
      type: new URL(url).pathname.replace(/\/$/, '').endsWith('/sse') ? 'sse' : 'http',
      timeout: 120_000, host: 'environment', server: process.env.JETBRAINS_MCP_SERVER || 'JetBrains' };
  }
  const zcode = process.env.ZCODE_PROJECT_DIR || process.env.ZCODE_WORKSPACE_ROOT || process.env.ZCODE_PLUGIN_ROOT
    || /[\\/]\.zcode[\\/]/.test(import.meta.url);
  const claude = process.env.CLAUDECODE || /[\\/]\.claude[\\/]/.test(import.meta.url);
  if (!zcode && !claude && process.env.CODEX_THREAD_ID) return codexConnection();
  if (!zcode && claude) {
    const selected = selectedServer(claudeServers(project), 'Claude Code');
    if (selected) return connection(selected, 'Claude Code');
    throw new Error('No JetBrains MCP server found in Claude Code .mcp.json / .claude.json configuration.');
  }
  const native = selectedServer(zcodeServers(project), 'ZCode');
  if (native) return connection(native, 'ZCode');
  if (zcode || existsSync(resolve(project, '.zcode/config.json')) || existsSync(resolve(homedir(), '.zcode/cli/config.json'))) {
    throw new Error('No JetBrains MCP server found in project .zcode/config.json or ~/.zcode/cli/config.json (or their .agents/mcp.json fallback).');
  }
  const shared = selectedServer(claudeServers(project), 'JSON MCP configuration');
  if (shared) return connection(shared, 'JSON MCP configuration');
  throw new Error('No JetBrains MCP connection found in ZCode, .agents, or Claude JSON configuration. Set JETBRAINS_MCP_URL for another host.');
}

async function* sseEvents(response) {
  const decoder = new TextDecoder();
  let buffer = '';
  for await (const chunk of response.body) {
    buffer += decoder.decode(chunk, { stream: true });
    let boundary;
    while ((boundary = /\r?\n\r?\n/.exec(buffer))) {
      const event = buffer.slice(0, boundary.index);
      buffer = buffer.slice(boundary.index + boundary[0].length);
      const lines = event.split(/\r?\n/);
      const data = lines.filter((line) => line.startsWith('data:')).map((line) => line.slice(5).replace(/^ /, '')).join('\n');
      if (data) yield { type: lines.find((line) => line.startsWith('event:'))?.slice(6).trim() || 'message', data };
    }
  }
}

async function readRpcResponse(response, id) {
  if (!response.headers.get('content-type')?.startsWith('text/event-stream')) {
    const message = await response.json();
    if (message.id !== id) throw new Error('MCP returned a response for another request.');
    return message;
  }
  for await (const { data } of sseEvents(response)) {
    const message = JSON.parse(data);
    if (message.id === id) return message;
  }
  throw new Error('MCP stream ended without the matching response.');
}

class McpClient {
  constructor({ url, headers, timeout, type, host, server }) {
    this.url = url;
    this.headers = { ...headers, 'Content-Type': 'application/json', Accept: 'application/json, text/event-stream' };
    this.timeout = timeout;
    this.type = type;
    this.connection = { host, server, transport: type };
    this.requestId = 0;
    this.pending = new Map();
  }

  async openSse() {
    this.streamController = new AbortController();
    let resolveEndpoint;
    let rejectEndpoint;
    const endpoint = new Promise((resolve, reject) => { resolveEndpoint = resolve; rejectEndpoint = reject; });
    const timer = setTimeout(() => rejectEndpoint(new Error('Timed out waiting for the MCP SSE endpoint.')), this.timeout);
    this.streamTask = (async () => {
      const response = await fetch(this.url, { headers: { ...this.headers, Accept: 'text/event-stream' },
        redirect: 'error', signal: this.streamController.signal });
      if (!response.ok) throw new Error(`MCP SSE connection returned HTTP ${response.status}.`);
      for await (const event of sseEvents(response)) {
        if (event.type === 'endpoint') {
          const url = new URL(event.data, this.url);
          if (url.origin !== new URL(this.url).origin) throw new Error('MCP SSE message endpoint changed origin.');
          resolveEndpoint(url.href);
        } else {
          const message = JSON.parse(event.data);
          this.pending.get(message.id)?.resolve(message);
        }
      }
      if (!this.closing) throw new Error('MCP SSE stream closed before the session completed.');
    })().catch((error) => {
      if (!this.closing) {
        this.streamError = error;
        rejectEndpoint(error);
        for (const pending of this.pending.values()) pending.reject(error);
      }
    });
    try { this.postUrl = await endpoint; } finally { clearTimeout(timer); }
  }

  async sseRequest(message, notification) {
    if (this.streamError) throw this.streamError;
    const post = async () => {
      const response = await fetch(this.postUrl, { method: 'POST', headers: this.headers,
        body: JSON.stringify(message), redirect: 'error', signal: AbortSignal.timeout(this.timeout) });
      await response.body?.cancel();
      if (!response.ok) throw new Error(`MCP ${message.method} returned HTTP ${response.status}.`);
    };
    if (notification) return post();
    let timer;
    const reply = new Promise((resolve, reject) => {
      this.pending.set(message.id, { resolve, reject });
      timer = setTimeout(() => reject(new Error(`MCP ${message.method} timed out waiting for its SSE response.`)), this.timeout);
    });
    try { return (await Promise.all([post(), reply]))[1]; }
    finally { clearTimeout(timer); this.pending.delete(message.id); }
  }

  async request(method, params, notification = false) {
    const message = { jsonrpc: '2.0', method };
    if (params !== undefined) message.params = params;
    if (!notification) message.id = ++this.requestId;
    let reply;
    if (this.type === 'sse') {
      reply = await this.sseRequest(message, notification);
    } else {
      const response = await fetch(this.url, { method: 'POST', headers: this.headers,
        body: JSON.stringify(message), redirect: 'error', signal: AbortSignal.timeout(this.timeout) });
      if (!response.ok) throw new Error(`MCP ${method} returned HTTP ${response.status}.`);
      const sessionId = response.headers.get('mcp-session-id');
      if (sessionId) this.headers['Mcp-Session-Id'] = sessionId;
      if (notification) { await response.body?.cancel(); return; }
      reply = await readRpcResponse(response, message.id);
    }
    if (notification) return;
    if (reply.error) throw new Error(`MCP ${method}: ${reply.error.code} ${reply.error.message}`);
    return reply.result;
  }

  async initialize() {
    if (this.type === 'sse') await this.openSse();
    const result = await this.request('initialize', { protocolVersion, capabilities: {},
      clientInfo: { name: 'scrapider-inspection-runner', version: '1.0.0' } });
    if (result.protocolVersion !== protocolVersion) throw new Error(`Unsupported MCP protocol: ${result.protocolVersion}`);
    this.headers['MCP-Protocol-Version'] = protocolVersion;
    await this.request('notifications/initialized', undefined, true);
  }

  async close() {
    if (this.type === 'sse') {
      this.closing = true;
      this.streamController?.abort();
      await this.streamTask;
      return;
    }
    if (!this.headers['Mcp-Session-Id']) return;
    try {
      const response = await fetch(this.url, { method: 'DELETE', headers: this.headers,
        redirect: 'error', signal: AbortSignal.timeout(5000) });
      await response.body?.cancel();
      if (!response.ok && ![404, 405].includes(response.status)) console.error(`MCP session cleanup returned HTTP ${response.status}.`);
    } catch (error) {
      console.error(`MCP session cleanup failed: ${error.message}`);
    }
  }
}

export async function createInspectionClient(project) {
  return new McpClient(await connectionOptions(project));
}
