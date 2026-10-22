// 用法：node reproduce-preflight.mjs <安装同版本依赖的 runtime 目录>
// runtime/node_modules 中须有 @ooples/token-optimizer-mcp@7.5.1。
// 此脚本不安装依赖，不装 Hook，不改全局配置；所有工具状态在临时目录。
import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';
import { pathToFileURL } from 'node:url';

const runtime = path.resolve(process.argv[2] || '');
if (!process.argv[2]) throw new Error('请传入隔离 runtime 目录');
const packageRoot = path.join(runtime, 'node_modules/@ooples/token-optimizer-mcp');
const pkg = JSON.parse(fs.readFileSync(path.join(packageRoot, 'package.json'), 'utf8'));
if (pkg.version !== '7.5.1') throw new Error(`本复现固定为 7.5.1，实际为 ${pkg.version}`);
const { Client } = await import(pathToFileURL(path.join(runtime, 'node_modules/@modelcontextprotocol/sdk/dist/esm/client/index.js')));
const { StdioClientTransport } = await import(pathToFileURL(path.join(runtime, 'node_modules/@modelcontextprotocol/sdk/dist/esm/client/stdio.js')));
const taskState = fs.mkdtempSync(path.join(os.tmpdir(), 'token-optimizer-repro-'));
const userDirectory = path.join(taskState, 'user');
fs.mkdirSync(userDirectory);
const transport = new StdioClientTransport({
  command: process.execPath,
  args: [path.join(packageRoot, 'dist/server/index.js')],
  env: {
    PATH: process.env.PATH,
    HOME: userDirectory,
    TMPDIR: taskState,
    TOKEN_OPTIMIZER_VERSION: '7.5.1',
    TOKEN_OPTIMIZER_TOOL_PROFILE: 'files',
    TOKEN_OPTIMIZER_CACHE_DIR: path.join(taskState, 'cache'),
    TOKEN_OPTIMIZER_ANALYTICS_DB: path.join(taskState, 'analytics.db'),
    TOKEN_OPTIMIZER_STATE_DIR: path.join(taskState, 'state'),
    TOKEN_OPTIMIZER_TELEMETRY: '0',
    TOKEN_OPTIMIZER_BEACON: '0',
  },
  stderr: 'pipe',
});
transport.stderr?.on('data', chunk => process.stderr.write(chunk));
const client = new Client({ name: 'semantic-change-reproducer', version: '1.0.0' });
try {
  await client.connect(transport);
  const target = path.join(taskState, 'policy.py');
  const before = 'def policy(enabled):\n    if enabled:\n        return "allow"\n    return "deny"\n';
  const after = 'def policy(enabled):\n    if enabled:\n        return "allow"\n        return "deny"\n';
  for (const [name, content, options] of [
    ['first', before, {}],
    ['indentation_change', after, {}],
    ['full_recovery', after, { enableCache: false, diffMode: false }],
  ]) {
    fs.writeFileSync(target, content);
    const response = await client.callTool({ name: 'smart_read', arguments: { path: target, ...options } });
    process.stdout.write(JSON.stringify({ name, response }) + '\n');
  }
} finally {
  await client.close();
  fs.rmSync(taskState, { recursive: true, force: true });
}
