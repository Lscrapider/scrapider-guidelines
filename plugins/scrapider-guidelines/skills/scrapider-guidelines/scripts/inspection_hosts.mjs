import { execFile } from 'node:child_process';
import { existsSync, readFileSync } from 'node:fs';
import { homedir } from 'node:os';
import { dirname, isAbsolute, relative, resolve, sep } from 'node:path';
import { isDeepStrictEqual, promisify } from 'node:util';

const execute = promisify(execFile);

function jsoncText(text) {
  let clean = '';
  let quoted = false;
  for (let index = 0; index < text.length; index++) {
    const char = text[index];
    if (quoted) {
      clean += char;
      if (char === '\\') clean += text[++index] || '';
      else if (char === '"') quoted = false;
    } else if (char === '"') {
      quoted = true;
      clean += char;
    } else if (char === '/' && text[index + 1] === '/') {
      while (index + 1 < text.length && !'\r\n'.includes(text[index + 1])) index++;
    } else if (char === '/' && text[index + 1] === '*') {
      const end = text.indexOf('*/', index + 2);
      if (end < 0) throw new Error('Unterminated JSONC comment.');
      clean += ' ';
      index = end + 1;
    } else clean += char;
  }
  let output = '';
  quoted = false;
  for (let index = 0; index < clean.length; index++) {
    const char = clean[index];
    if (quoted) {
      output += char;
      if (char === '\\') output += clean[++index] || '';
      else if (char === '"') quoted = false;
    } else if (char === '"') {
      quoted = true;
      output += char;
    } else if (char === ',') {
      let next = index + 1;
      while (/\s/.test(clean[next] || '') && next < clean.length) next++;
      if (!['}', ']'].includes(clean[next])) output += char;
    } else output += char;
  }
  return output;
}

function jsonFile(path, comments = false) {
  if (!existsSync(path)) return {};
  try {
    const text = readFileSync(path, 'utf8').replace(/^\uFEFF/, '');
    const value = JSON.parse(comments ? jsoncText(text) : text);
    if (!value || Array.isArray(value) || typeof value !== 'object') throw new Error();
    return value;
  } catch {
    throw new Error(`Cannot parse MCP configuration ${path}: invalid ${comments ? 'JSON/JSONC' : 'JSON'}.`);
  }
}

function wildcard(pattern, value) {
  const escaped = pattern.replace(/[.+^${}()|[\]\\]/g, '\\$&').replaceAll('*', '.*').replaceAll('?', '.');
  return new RegExp(`^${escaped}$`).test(value);
}

function inside(path, directory) {
  const value = relative(resolve(directory), resolve(path));
  return value === '' || (value !== '..' && !value.startsWith(`..${sep}`) && !isAbsolute(value));
}

function workbuddyServers(project) {
  const user = jsonFile(resolve(homedir(), '.workbuddy/mcp.json')).mcpServers || {};
  const local = jsonFile(resolve(project, '.workbuddy/mcp.json')).mcpServers || {};
  const servers = { ...user };
  for (const [name, config] of Object.entries(local)) {
    servers[name] = user[name] && !isDeepStrictEqual(user[name], config)
      ? { ...config, inspectionConnectionError: `WorkBuddy has conflicting user/project definitions for ${name}; pass --mcp-url for the current permitted connection.` }
      : config;
  }
  return Object.fromEntries(Object.entries(servers).map(([name, config]) => [name,
    { ...config, timeoutMs: config.timeoutMs || config.timeout }]));
}

function qoderDirectory() {
  if (process.env.QODER_CONFIG_DIR) return resolve(process.env.QODER_CONFIG_DIR);
  const standard = resolve(homedir(), '.qoder');
  return existsSync(resolve(standard, 'settings.json')) || !existsSync(resolve(homedir(), '.qoder-cn/settings.json'))
    ? standard : resolve(homedir(), '.qoder-cn');
}

function qoderServers(project) {
  const user = jsonFile(resolve(qoderDirectory(), 'settings.json'), true);
  const trusted = user.security?.folderTrust?.enabled === false
    || (user.permissions?.trustDirectories || []).some((directory) => inside(project, configuredString(directory)));
  const layers = [{ config: user, servers: user.mcpServers || {}, approval: false }];
  if (trusted) {
    const shared = jsonFile(resolve(project, '.qoder/settings.json'), true);
    const local = jsonFile(resolve(project, '.qoder/settings.local.json'), true);
    layers.push({ config: shared, servers: shared.mcpServers || {}, approval: true },
      { config: {}, servers: jsonFile(resolve(project, '.mcp.json'), true).mcpServers || {}, approval: true },
      { config: local, servers: local.mcpServers || {}, approval: false });
  }
  const policy = Object.assign({}, ...layers.map(({ config }) => config.mcp || {}));
  const excluded = new Set(layers.flatMap(({ config }) => config.mcp?.excluded || []));
  const servers = {};
  for (const layer of layers) {
    for (const [name, config] of Object.entries(layer.servers)) {
      let error;
      if (excluded.has(name) || (policy.allowed && !policy.allowed.includes(name))) error = `Qoder excludes MCP server ${name}.`;
      else if (layer.approval && !policy.enableAllProjectMcpServers && !policy.enabledProjectMcpServers?.includes(name)) {
        error = `Qoder project MCP ${name} is pending approval.`;
      }
      const tool = `mcp__${name}__run_inspection_kts`;
      const matches = (behavior) => layers.some(({ config }) => config.permissions?.[behavior]?.some(
        (pattern) => wildcard(pattern, tool) || wildcard(pattern, `mcp__${name}`)));
      if (matches('deny')) error = `Qoder denies ${tool}.`;
      else if (matches('ask')) error = `Qoder requires approval for ${tool}; confirm permission in Qoder first.`;
      const normalized = { ...config, timeoutMs: config.timeoutMs || config.timeout };
      servers[name] = error ? { ...normalized, inspectionConnectionError: error } : normalized;
    }
  }
  return servers;
}

function openCodeDirectory() {
  return process.env.OPENCODE_CONFIG_DIR ? resolve(process.env.OPENCODE_CONFIG_DIR)
    : resolve(process.env.XDG_CONFIG_HOME || resolve(homedir(), '.config'), 'opencode');
}

function openCodeValue(value, directory) {
  if (typeof value === 'string') return value.replace(/\{(env|file):([^}]+)\}/g, (_, kind, name) => {
    if (kind === 'env') return process.env[name] || '';
    const path = name.startsWith('~/') ? resolve(homedir(), name.slice(2)) : resolve(directory, name);
    return existsSync(path) ? readFileSync(path, 'utf8').trim() : '';
  });
  if (Array.isArray(value)) return value.map((item) => openCodeValue(item, directory));
  if (value && typeof value === 'object') return Object.fromEntries(Object.entries(value)
    .map(([key, item]) => [key, openCodeValue(item, directory)]));
  return value;
}

function openCodePaths(project) {
  const ancestors = [];
  for (let directory = project; ; directory = dirname(directory)) {
    ancestors.unshift(directory);
    if (dirname(directory) === directory) break;
  }
  const paths = [];
  const addDirectory = (directory) => paths.push(resolve(directory, 'opencode.json'), resolve(directory, 'opencode.jsonc'));
  addDirectory(openCodeDirectory());
  if (process.env.OPENCODE_CONFIG) paths.push(resolve(process.env.OPENCODE_CONFIG));
  ancestors.forEach(addDirectory);
  ancestors.forEach((directory) => addDirectory(resolve(directory, '.opencode')));
  return paths;
}

function openCodeServers(project) {
  const documents = openCodePaths(project).filter(existsSync)
    .map((path) => ({ data: jsonFile(path, true), directory: dirname(path) }));
  if (process.env.OPENCODE_CONFIG_CONTENT) {
    try { documents.push({ data: JSON.parse(jsoncText(process.env.OPENCODE_CONFIG_CONTENT)), directory: project }); }
    catch { throw new Error('OPENCODE_CONFIG_CONTENT contains invalid JSON/JSONC.'); }
  }
  const servers = {};
  const permissions = [];
  for (const { data, directory } of documents) {
    Object.assign(servers, openCodeValue(data.mcp?.servers || data.mcp || {}, directory));
    permissions.push(...Object.entries(data.tools || {}).map(([action, enabled]) =>
      ({ action, resource: '*', effect: enabled ? 'allow' : 'deny' })));
    const legacy = typeof data.permission === 'string' ? { '*': data.permission } : data.permission || {};
    permissions.push(...Object.entries(legacy).map(([action, effect]) =>
      ({ action, resource: '*', effect: typeof effect === 'string' ? effect : effect['*'] })));
    if (Array.isArray(data.permissions)) permissions.push(...data.permissions);
  }
  // V2 reverses document precedence for policies, preserving statement order within each document.
  const policies = [...documents].reverse().flatMap(({ data }) =>
    [...(data.experimental?.policies || []), ...(data.policies || [])]);
  return Object.fromEntries(Object.entries(servers).map(([name, config]) => {
    const action = `${name.replace(/[^a-zA-Z0-9_-]/g, '_')}_run_inspection_kts`;
    let effect = 'allow';
    for (const rule of permissions) if (rule.resource === '*' && wildcard(rule.action, action)) effect = rule.effect;
    const decisions = {};
    for (const rule of policies) {
      const resource = rule.action === 'mcp.use' ? name : rule.action === 'tool.use' ? `${action}:*` : null;
      if (resource !== null && wildcard(rule.resource, resource)) decisions[rule.action] = rule.effect;
    }
    const allowed = effect === 'allow' && !Object.values(decisions).includes('deny');
    const normalized = { ...config, timeoutMs: config.timeoutMs || config.timeout };
    return [name, allowed ? normalized : { ...normalized,
      inspectionConnectionError: `OpenCode denies MCP tool ${action} or requires approval; confirm permission in OpenCode first.` }];
  }));
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
  const matches = configured.filter(([, config]) => config.enable !== false && config.enabled !== false && config.disabled !== true);
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
  if (config.disabled_tools?.includes(tool) || config.disabledTools?.includes(tool)
      || (config.enabled_tools && !config.enabled_tools.includes(tool))
      || config.excludeTools?.includes(tool) || (config.includeTools && !config.includeTools.includes(tool))
      || config.tools?.some?.((entry) => ['*', tool].includes(entry.name) && entry.permission_policy === 'always_deny')) {
    throw new Error(`${tool} is excluded by ${host}'s configured tool allowlist.`);
  }
  const transport = config.transport || config;
  const type = transport.type || (transport.url ? 'auto' : 'stdio');
  const types = { http: 'http', streamable_http: 'http', 'streamable-http': 'http', streamableHttp: 'http', remote: 'http', sse: 'sse', auto: 'auto' };
  if (!types[type]) {
    throw new Error(`JetBrains MCP ${server} uses ${type} in ${host}; an HTTP or SSE connection is required.`);
  }
  if (!transport.url) throw new Error(`JetBrains MCP ${server} has no URL in ${host}.`);
  const headers = Object.fromEntries(Object.entries({ ...transport.headers, ...transport.http_headers })
    .map(([name, value]) => [name, configuredString(value)]));
  for (const [name, variable] of Object.entries(transport.env_http_headers || {})) headers[name] = environmentValue(variable);
  if (transport.bearer_token_env_var) headers.Authorization = `Bearer ${environmentValue(transport.bearer_token_env_var)}`;
  return { url: configuredString(transport.url), headers, type: types[type],
    timeout: config.timeoutMs || (config.tool_timeout_sec || 120) * 1000, host, server };
}

async function codexServers() {
  const { stdout } = await execute('codex', ['mcp', 'list', '--json'], { timeout: 10_000 });
  return Object.fromEntries(JSON.parse(stdout).map((config) => [config.name, config]));
}

async function codexServerDetail(server) {
  const { stdout } = await execute('codex', ['mcp', 'get', server, '--json'], { timeout: 10_000 });
  return JSON.parse(stdout);
}

function explicitConnection(explicit) {
  const url = explicit.url || process.env.JETBRAINS_MCP_URL;
  if (!url) return null;
  try {
    if (!['http:', 'https:'].includes(new URL(url).protocol)) throw new Error();
  } catch { throw new Error('The explicit MCP URL must be an absolute HTTP(S) endpoint.'); }
  const type = explicit.type || process.env.JETBRAINS_MCP_TRANSPORT || 'auto';
  if (!['auto', 'http', 'sse'].includes(type)) throw new Error('MCP transport must be auto, http, or sse.');
  let headers;
  try {
    headers = JSON.parse(process.env.JETBRAINS_MCP_HEADERS || '{}');
    if (!headers || Array.isArray(headers) || typeof headers !== 'object') throw new Error();
    new Headers(headers);
  } catch { throw new Error('JETBRAINS_MCP_HEADERS must be a valid JSON object of HTTP headers.'); }
  return { url, headers, type, timeout: 120_000, host: 'explicit', server: 'JetBrains' };
}

function adapters(project) {
  const file = (path) => existsSync(path);
  const home = homedir();
  return [
    { host: 'ZCode', path: /[\\/]\.zcode[\\/]/, hint: process.env.ZCODE_PROJECT_DIR || process.env.ZCODE_WORKSPACE_ROOT || process.env.ZCODE_PLUGIN_ROOT,
      available: () => file(resolve(home, '.zcode/cli/config.json')) || file(resolve(project, '.zcode/config.json')),
      load: () => zcodeServers(project) },
    { host: 'Claude Code', path: /[\\/]\.claude[\\/]/, hint: process.env.CLAUDECODE,
      available: () => file(resolve(home, '.claude.json')) || file(resolve(project, '.claude')),
      load: () => claudeServers(project) },
    { host: 'WorkBuddy', path: /[\\/]\.workbuddy[\\/]/,
      available: () => file(resolve(home, '.workbuddy/mcp.json')) || file(resolve(project, '.workbuddy/mcp.json')),
      load: () => workbuddyServers(project) },
    { host: 'Qoder', path: /[\\/]\.qoder(?:-cn)?[\\/]/, hint: process.env.QODER_SESSION_ID || process.env.QODER_WORKING_DIR,
      available: () => file(resolve(qoderDirectory(), 'settings.json')) || file(resolve(project, '.qoder/settings.json')) || file(resolve(project, '.qoder/settings.local.json')),
      load: () => qoderServers(project) },
    { host: 'OpenCode', path: /[\\/](?:\.opencode|\.config[\\/]opencode)[\\/]/,
      hint: process.env.OPENCODE_CLIENT || process.env.OPENCODE_PID || process.env.OPENCODE === '1',
      available: () => process.env.OPENCODE_CONFIG || process.env.OPENCODE_CONFIG_CONTENT
        || openCodePaths(project).some(file),
      load: () => openCodeServers(project) },
    { host: 'Codex', path: /[\\/]\.codex[\\/]/, hint: process.env.CODEX_THREAD_ID || process.env.CODEX_SESSION_ID,
      available: () => file(resolve(process.env.CODEX_HOME || resolve(home, '.codex'), 'config.toml')),
      load: codexServers, detail: codexServerDetail },
  ];
}

async function adaptedConnection(adapter, servers) {
  const selected = selectedServer(servers || await adapter.load(), adapter.host);
  if (!selected) throw new Error(`No eligible JetBrains MCP connection found in ${adapter.host}; pass --mcp-url for an explicitly permitted direct connection.`);
  if (adapter.detail) selected.config = await adapter.detail(selected.server);
  return connection(selected, adapter.host);
}

export async function resolveInspectionConnection(project, explicit = {}) {
  const supplied = explicitConnection(explicit);
  if (supplied) return supplied;
  const profiles = adapters(project);
  const fromPath = profiles.filter((adapter) => adapter.path.test(import.meta.url));
  const hinted = profiles.filter((adapter) => fromPath.includes(adapter) || adapter.hint);
  if (hinted.length > 1) throw new Error(`Conflicting host signals: ${hinted.map(({ host }) => host).join(', ')}. Pass --mcp-url.`);
  if (hinted.length) return adaptedConnection(hinted[0]);
  const name = process.env.JETBRAINS_MCP_SERVER;
  const candidates = [];
  for (const adapter of profiles.filter((item) => item.available())) {
    const servers = await adapter.load();
    if (Object.keys(servers).some((key) => name ? key === name : /jetbrains?/i.test(key))) candidates.push({ adapter, servers });
  }
  if (candidates.length > 1) throw new Error(`MCP host is ambiguous: ${candidates.map(({ adapter }) => adapter.host).join(', ')}. Pass --mcp-url.`);
  if (candidates.length) return adaptedConnection(candidates[0].adapter, candidates[0].servers);
  throw new Error('Automatic MCP discovery found no eligible connection. Use --mcp-url for the current permitted MCP endpoint.');
}
