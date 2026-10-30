#!/usr/bin/env node
import { execFile } from 'node:child_process';
import { existsSync, readFileSync, readdirSync } from 'node:fs';
import { dirname, isAbsolute, relative, resolve, sep } from 'node:path';
import { fileURLToPath } from 'node:url';
import { promisify } from 'node:util';
import { createInspectionClient } from './inspection_mcp.mjs';

const execute = promisify(execFile);
const inspectionsDirectory = resolve(dirname(fileURLToPath(import.meta.url)), 'inspections');
const batchSize = 15;
const usage = 'node run_inspection.mjs <projectPath> [filesJson] [inspectionsJson]';

async function gitJavaFiles(project) {
  const commands = [
    ['diff', '--relative', '--name-only', '-z', '--diff-filter=d', '--', '*.java'],
    ['diff', '--cached', '--relative', '--name-only', '-z', '--diff-filter=d', '--', '*.java'],
    ['ls-files', '--others', '--exclude-standard', '-z', '--', '*.java'],
  ];
  const outputs = await Promise.all(commands.map(async (args) => {
    try {
      return (await execute('git', ['-C', project, ...args], { encoding: 'utf8' })).stdout;
    } catch (error) {
      throw new Error(`Git file discovery failed: ${error.stderr?.trim() || error.message}`);
    }
  }));
  return [...new Set(outputs.flatMap((output) => output.split('\0')).filter(
    (path) => path.endsWith('.java') && existsSync(resolve(project, path)),
  ))];
}

function stringList(value, name) {
  if (!Array.isArray(value) || !value.every((item) => typeof item === 'string')) {
    throw new Error(`${name} must be a JSON array of strings.`);
  }
  return [...new Set(value)];
}

function projectPaths(project, files) {
  return files.map((file) => {
    const path = relative(project, resolve(project, file));
    if (path === '..' || path.startsWith('../') || path.startsWith('..\\') || isAbsolute(path)) {
      throw new Error(`File is outside the selected project: ${file}`);
    }
    return path.split(sep).join('/');
  });
}

function inspectionJobs(files, inspections) {
  if (inspections === null) {
    const jobs = [];
    for (let offset = 0; offset < files.length; offset += batchSize) {
      jobs.push({ inspection: 'BatchAllRules', script: 'BatchAllRules.inspection.kts', files: files.slice(offset, offset + batchSize) });
    }
    return jobs;
  }
  const library = new Map(readdirSync(inspectionsDirectory).flatMap((file) => {
    const match = /^((?:J|PY)\d+)[A-Z].*\.inspection\.kts$/.exec(file);
    return match ? [[match[1], file]] : [];
  }));
  return stringList(inspections, 'inspectionsJson').flatMap((inspection) => {
    if (!library.has(inspection)) throw new Error(`Unknown inspection: ${inspection}`);
    return files.map((file) => ({ inspection, script: library.get(inspection), files: [file] }));
  });
}

function kotlinSetting(value, original) {
  if (typeof value === 'boolean' || Number.isInteger(value)) return String(value);
  if (typeof value === 'string') return JSON.stringify(value).replaceAll('$', '\\$');
  if (Array.isArray(value) && value.every((item) => typeof item === 'string')) {
    const constructor = original.trimStart().startsWith('setOf(') ? 'setOf' : 'listOf';
    return `${constructor}<String>(${value.map((item) => kotlinSetting(item, '')).join(', ')})`;
  }
  if (value && typeof value === 'object' && !Array.isArray(value)
      && Object.values(value).every((item) => typeof item === 'string')) {
    return `mapOf<String, String>(${Object.entries(value).map(([key, item]) => `${kotlinSetting(key, '')} to ${kotlinSetting(item, '')}`).join(', ')})`;
  }
  throw new Error('Settings accept booleans, integers, strings, string lists, or string maps.');
}

function configureScript(source, settings) {
  for (const [name, value] of Object.entries(settings)) {
    const escaped = name.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    const pattern = new RegExp(`^(val ${escaped}(?:[ \\t]*:[^=\\n]+)?[ \\t]*=[ \\t]*)([^\\n]*)$`, 'm');
    const match = pattern.exec(source);
    if (!match || match[2].trimEnd().endsWith('(')) throw new Error(`Not a single-line script setting: ${name}`);
    source = source.replace(pattern, (_, prefix) => prefix + kotlinSetting(value, match[2]));
  }
  return source;
}

function loadScripts(jobs, settings) {
  const scripts = new Map(jobs.map(({ script }) => [script, null]));
  const supported = new Set();
  for (const script of scripts.keys()) {
    const source = readFileSync(resolve(inspectionsDirectory, script), 'utf8');
    const names = new Set([...source.matchAll(/^val ([A-Z][A-Z0-9_]*)[ \t]*(?::[^=\n]+)?=/gm)].map((match) => match[1]));
    const applicable = Object.fromEntries(Object.entries(settings).filter(([name]) => names.has(name)));
    Object.keys(applicable).forEach((name) => supported.add(name));
    scripts.set(script, configureScript(source, applicable));
  }
  for (const name of Object.keys(settings)) {
    if (!supported.has(name)) throw new Error(`Setting does not apply to the selected inspections: ${name}`);
  }
  return scripts;
}

function inspectionResult(result, batch) {
  if (result.isError) throw new Error(result.content.filter((item) => item.type === 'text').map((item) => item.text).join('\n'));
  const inspection = result.structuredContent || JSON.parse(result.content.find((item) => item.type === 'text').text);
  if (batch && inspection.foundProblems) {
    inspection.foundProblems = inspection.foundProblems.map(({ message, highlightType }) => ({ message, highlightType }));
  }
  return inspection;
}

function resultStatus(result) {
  if (!result.compilationSuccess) return 'error';
  if (!result.inspectionResultMessage?.startsWith('Inspection found ')) return 'incomplete';
  return result.foundProblems?.some(({ message }) => message.startsWith('[BATCH]') && /not found|truncated/.test(message))
    ? 'incomplete' : 'ok';
}

async function runInspection(projectPath, files = null, inspections = null) {
  const report = { status: 'ok', results: [] };
  let client;
  try {
    const project = resolve(projectPath);
    const selected = files === null ? await gitJavaFiles(project) : stringList(files, 'filesJson');
    const paths = projectPaths(project, selected);
    const jobs = inspectionJobs(paths, inspections);
    Object.assign(report, { projectPath: project, files: paths });
    if (!jobs.length) {
      return { ...report, status: 'skipped', reason: paths.length ? 'No inspections selected.'
        : files === null ? 'No changed Java files in the Git working tree.' : 'No files selected.' };
    }
    const settings = JSON.parse(process.env.SCRAPIDER_INSPECTION_SETTINGS || '{}');
    if (!settings || Array.isArray(settings) || typeof settings !== 'object' || 'FILES' in settings) {
      throw new Error('SCRAPIDER_INSPECTION_SETTINGS must be an object; select files through the second argument.');
    }
    const scripts = loadScripts(jobs, settings);
    client = await createInspectionClient(project);
    report.connection = client.connection;
    await client.initialize();
    for (const job of jobs) {
      const batch = job.inspection === 'BatchAllRules';
      const code = batch ? configureScript(scripts.get(job.script), { FILES: job.files }) : scripts.get(job.script);
      const result = inspectionResult(await client.request('tools/call', {
        name: 'run_inspection_kts',
        arguments: { projectPath: project, contextPath: job.files[0], inspectionKtsCode: code },
      }), batch);
      report.results.push({ inspection: job.inspection, contextPaths: job.files, ...result });
      const status = resultStatus(result);
      if (status !== 'ok') report.status = status;
      if (status === 'error') break;
    }
  } catch (error) {
    const cause = error.cause?.code || error.cause?.message;
    Object.assign(report, { status: 'error', error: error.message + (cause ? ` (${cause})` : '') });
  } finally {
    await client?.close();
  }
  return report;
}

const args = process.argv.slice(2);
if (!args.length || args[0] === '--help') {
  console.log(`${usage}\nOnly projectPath: inspect changed .java files from Git.\nOptional lists: '["src/A.java"]' '["J7","J13"]'. Use null to keep Git selection.`);
  process.exitCode = args.length ? 0 : 1;
} else {
  let report;
  try {
    if (args.length > 3) throw new Error(usage);
    report = await runInspection(args[0], args[1] === undefined ? null : JSON.parse(args[1]), args[2] === undefined ? null : JSON.parse(args[2]));
  } catch (error) {
    report = { status: 'error', results: [], error: error.message };
  }
  console.log(JSON.stringify(report, null, 2));
  process.exitCode = ['ok', 'skipped'].includes(report.status) ? 0 : 1;
}
