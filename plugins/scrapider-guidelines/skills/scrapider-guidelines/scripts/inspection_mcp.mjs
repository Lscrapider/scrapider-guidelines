import { resolveInspectionConnection } from './inspection_hosts.mjs';

const protocolVersion = '2025-03-26';

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
      if (!response.ok) {
        await response.body?.cancel();
        throw Object.assign(new Error(`MCP ${method} returned HTTP ${response.status}.`), { httpStatus: response.status });
      }
      const sessionId = response.headers.get('mcp-session-id');
      if (sessionId) this.headers['Mcp-Session-Id'] = sessionId;
      if (notification) { await response.body?.cancel(); return; }
      reply = await readRpcResponse(response, message.id);
    }
    if (notification) return;
    if (reply.error) throw new Error(`MCP ${method}: ${reply.error.code} ${reply.error.message}`);
    return reply.result;
  }

  async initializeProtocol() {
    const result = await this.request('initialize', { protocolVersion, capabilities: {},
      clientInfo: { name: 'scrapider-inspection-runner', version: '1.0.0' } });
    if (result.protocolVersion !== protocolVersion) throw new Error(`Unsupported MCP protocol: ${result.protocolVersion}`);
    this.headers['MCP-Protocol-Version'] = protocolVersion;
    await this.request('notifications/initialized', undefined, true);
  }

  async initialize() {
    if (this.type === 'auto') {
      this.type = 'http';
      this.connection.transport = this.type;
      try { await this.initializeProtocol(); }
      catch (error) {
        if (![404, 405].includes(error.httpStatus)) throw error;
        this.type = 'sse';
        this.connection.transport = this.type;
        await this.openSse();
        await this.initializeProtocol();
      }
    } else {
      if (this.type === 'sse') await this.openSse();
      await this.initializeProtocol();
    }
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

export async function createInspectionClient(project, explicit) {
  return new McpClient(await resolveInspectionConnection(project, explicit));
}
