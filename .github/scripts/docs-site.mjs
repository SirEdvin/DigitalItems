#!/usr/bin/env node

import { appendFile, lstat, mkdir, readFile, readdir, realpath, rm, writeFile } from "node:fs/promises";
import { spawnSync } from "node:child_process";
import { dirname, isAbsolute, relative, resolve, sep } from "node:path";
import { fileURLToPath } from "node:url";

export const COMMON_DOC_FILES = [
  "projects/typed-peripheral-digitalitems/build.gradle.kts",
  "projects/typed-peripheral-digitalitems/package.json",
  "projects/typed-peripheral-digitalitems/package-lock.json",
  "projects/typed-peripheral-digitalitems/shared.ts",
  "projects/typed-peripheral-digitalitems/digitizer.ts",
  "projects/typed-peripheral-digitalitems/advanced_digitizer.ts",
  "projects/typed-peripheral-digitalitems/documentation/index.md",
  "projects/typed-peripheral-digitalitems/documentation/theme/digitalitems.css",
  "projects/typed-peripheral-digitalitems/documentation/theme/digitalitems.js",
];
const DOC_GENERATORS = [
  ["projects/typed-peripheral-digitalitems/typedoc.json", "projects/typed-peripheral-digitalitems/documentation/theme/plugin.mjs"],
  ["projects/typed-peripheral-digitalitems/mkdocs.yml", "projects/typed-peripheral-digitalitems/requirements-docs.txt", "projects/typed-peripheral-digitalitems/documentation/generate-docs.mjs"],
];

const BRANCHES = ["1.20", "1.21"];
const TAG_RE = /^v\d+(?:\.\d+)+(?:-[0-9A-Za-z]+(?:[.-][0-9A-Za-z]+)*)?$/;
const SHA_RE = /^[0-9a-f]{40}$/;
const HELP = `Usage: node .github/scripts/docs-site.mjs <command> [options]

Commands:
  discover [--repository DIR] [--github-output FILE] [--plan FILE]
  assemble --input DIR --output DIR (--plan FILE | --plan-json JSON)
  validate --site DIR
`;

function fail(message) {
  throw new Error(message);
}

function git(repository, args, allowFailure = false) {
  const result = spawnSync("git", ["-C", repository, ...args], { encoding: "utf8" });
  if (result.status !== 0 && !allowFailure) {
    fail(`git ${args.join(" ")} failed: ${(result.stderr || result.stdout).trim()}`);
  }
  return result;
}

export function safeTagName(ref) {
  const name = ref.startsWith("refs/tags/") ? ref.slice(10) : ref;
  return TAG_RE.test(name) ? name : null;
}

function versionParts(value) {
  return value.replace(/^v/, "").split(/[.-]/).map((part) => /^\d+$/.test(part) ? Number(part) : part.toLowerCase());
}

function compareVersionsDescending(left, right) {
  const a = versionParts(left);
  const b = versionParts(right);
  const count = Math.max(a.length, b.length);
  for (let index = 0; index < count; index += 1) {
    if (a[index] === b[index]) continue;
    if (a[index] === undefined) return typeof b[index] === "string" ? -1 : 1;
    if (b[index] === undefined) return typeof a[index] === "string" ? 1 : -1;
    if (typeof a[index] === "number" && typeof b[index] !== "number") return 1;
    if (typeof a[index] !== "number" && typeof b[index] === "number") return -1;
    return a[index] > b[index] ? -1 : 1;
  }
  return left.localeCompare(right);
}

export function sortEntries(entries) {
  return [...entries].sort((a, b) => {
    if (a.kind !== b.kind) return a.kind === "branch" ? -1 : 1;
    return compareVersionsDescending(a.name, b.name);
  });
}

function safeSegment(value, field) {
  if (typeof value !== "string" || !/^[0-9A-Za-z]+(?:[.-][0-9A-Za-z]+)*$/.test(value)) {
    fail(`Unsafe ${field}: ${JSON.stringify(value)}`);
  }
  return value;
}

export function normalizePlan(value) {
  if (!Array.isArray(value) || value.length === 0) fail("Plan must be a non-empty array");
  const paths = new Set();
  const refs = new Set();
  const entries = value.map((raw, index) => {
    if (!raw || typeof raw !== "object" || Array.isArray(raw)) fail(`Invalid plan entry at index ${index}`);
    if (raw.kind !== "branch" && raw.kind !== "tag") fail(`Invalid kind at index ${index}`);
    const name = safeSegment(raw.name, "name");
    if (raw.kind === "branch" && !BRANCHES.includes(name)) fail(`Unsupported branch: ${name}`);
    if (raw.kind === "tag" && safeTagName(name) !== name) fail(`Unsafe tag: ${name}`);
    const ref = raw.kind === "branch" ? `origin/${name}` : `refs/tags/${name}`;
    if (raw.ref !== ref) fail(`Ref does not match ${raw.kind} ${name}`);
    if (!SHA_RE.test(raw.sha)) fail(`Invalid SHA for ${name}`);
    const path = `${raw.kind}/${name}`;
    if (raw.path !== path) fail(`Path must be ${path}`);
    if (paths.has(path) || refs.has(ref)) fail(`Duplicate plan entry: ${path}`);
    paths.add(path);
    refs.add(ref);
    return {
      kind: raw.kind,
      name,
      label: typeof raw.label === "string" && raw.label ? raw.label : name,
      path,
      ref,
      sha: raw.sha,
      minecraftVersion: typeof raw.minecraftVersion === "string" ? raw.minecraftVersion : null,
      packageVersion: typeof raw.packageVersion === "string" ? raw.packageVersion : null,
      javaVersion: raw.javaVersion === "21" ? "21" : "17",
    };
  });
  return sortEntries(entries);
}

function show(repository, ref, path) {
  return git(repository, ["show", `${ref}:${path}`]).stdout;
}

function refHasDocs(repository, ref) {
  const has = (path) => git(repository, ["cat-file", "-e", `${ref}:${path}`], true).status === 0;
  return COMMON_DOC_FILES.every(has)
    && DOC_GENERATORS.some((files) => files.every(has))
    && /\bgenerateDocs\b/.test(show(repository, ref, COMMON_DOC_FILES[0]));
}

function metadata(repository, ref) {
  const packageJson = JSON.parse(show(repository, ref, "projects/typed-peripheral-digitalitems/package.json"));
  const properties = show(repository, ref, "gradle.properties");
  const minecraftVersion = /^minecraftVersion\s*=\s*(.+)$/m.exec(properties)?.[1].trim() ?? null;
  return {
    packageVersion: packageJson.version ?? null,
    minecraftVersion,
    javaVersion: minecraftVersion?.startsWith("1.21") ? "21" : "17",
  };
}

export function discover(repository = ".") {
  const candidates = BRANCHES.map((name) => ({ kind: "branch", name, ref: `origin/${name}` }));
  const tags = git(repository, ["for-each-ref", "--format=%(refname)", "refs/tags"]).stdout
    .split("\n")
    .filter(Boolean)
    .map((ref) => ({ kind: "tag", name: safeTagName(ref), ref }))
    .filter(({ name }) => name !== null);
  const entries = [];
  for (const candidate of [...candidates, ...tags]) {
    if (git(repository, ["rev-parse", "--verify", `${candidate.ref}^{commit}`], true).status !== 0) continue;
    if (!refHasDocs(repository, candidate.ref)) continue;
    const sha = git(repository, ["rev-parse", `${candidate.ref}^{commit}`]).stdout.trim().toLowerCase();
    const details = metadata(repository, candidate.ref);
    entries.push({
      ...candidate,
      ...details,
      sha,
      path: `${candidate.kind}/${candidate.name}`,
      label: candidate.kind === "branch"
        ? `Minecraft ${details.minecraftVersion ?? candidate.name} (development)`
        : candidate.name,
    });
  }
  if (entries.length === 0) fail("No documentation-capable refs were discovered");
  const plan = normalizePlan(entries);
  return {
    plan,
    matrix: { include: plan.map((entry) => ({ ...entry, artifact: `docs-${entry.kind}-${entry.name}-${entry.sha}` })) },
  };
}

function within(root, target) {
  const value = relative(root, target);
  return value === "" || (value !== ".." && !value.startsWith(`..${sep}`) && !isAbsolute(value));
}

async function rejectSymlinkComponents(path) {
  let current = resolve(path);
  while (true) {
    const info = await lstat(current).catch(() => null);
    if (info?.isSymbolicLink()) fail(`Symlinked path component is not allowed: ${current}`);
    const parent = dirname(current);
    if (parent === current) return;
    current = parent;
  }
}

async function copyTree(source, destination) {
  const info = await lstat(source).catch(() => null);
  if (!info?.isDirectory() || info.isSymbolicLink()) fail(`Missing or unsafe docs fragment: ${source}`);
  await mkdir(destination, { recursive: true });
  for (const name of (await readdir(source)).sort()) {
    const from = resolve(source, name);
    const to = resolve(destination, name);
    const child = await lstat(from);
    if (child.isSymbolicLink()) fail(`Symlinks are not allowed in docs: ${from}`);
    if (child.isDirectory()) await copyTree(from, to);
    else if (child.isFile()) await writeFile(to, await readFile(from));
    else fail(`Unsupported file in docs: ${from}`);
  }
}

function escapeHtml(value) {
  return String(value).replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll('"', "&quot;");
}

function rootHtml(manifest) {
  const cards = manifest.versions.map((entry) => `
        <a class="version" href="${escapeHtml(entry.path)}">
          <span>${entry.kind === "branch" ? "LIVE BRANCH" : "RELEASE"}</span>
          <strong>${escapeHtml(entry.label)}</strong>
          <small>${escapeHtml(entry.packageVersion ? `Types ${entry.packageVersion}` : entry.name)}</small>
        </a>`).join("");
  return `<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Digital Items Peripheral Archive</title><style>
:root{color-scheme:dark;font-family:"Trebuchet MS",sans-serif;background:#071116;color:#e4f2f2}*{box-sizing:border-box}body{margin:0;min-height:100vh;background:linear-gradient(rgba(75,225,210,.07) 1px,transparent 1px),linear-gradient(90deg,rgba(75,225,210,.07) 1px,transparent 1px),radial-gradient(circle at 80% 15%,#123942,#071116 42%);background-size:48px 48px,48px 48px,auto}main{width:min(920px,calc(100% - 2rem));margin:auto;padding:12vh 0}p.kicker{color:#4be1d2;font:700 .72rem "Lucida Console",monospace;letter-spacing:.18em}h1{max-width:760px;margin:.7rem 0 1rem;font:700 clamp(2.7rem,8vw,6.8rem)/.91 "Lucida Console",monospace;letter-spacing:-.08em}p.lead{max-width:620px;color:#9fb4ba;font-size:1.14rem;line-height:1.65}.versions{display:grid;grid-template-columns:repeat(auto-fit,minmax(240px,1fr));gap:1rem;margin-top:3.5rem}.version{position:relative;display:grid;gap:.55rem;padding:1.35rem;color:inherit;text-decoration:none;background:#0c1c23;border:1px solid #286a6b;clip-path:polygon(0 0,calc(100% - 18px) 0,100% 18px,100% 100%,0 100%);transition:transform .18s,border-color .18s}.version:hover{transform:translateY(-4px);border-color:#ffbd59}.version span{color:#ffbd59;font:700 .62rem "Lucida Console",monospace;letter-spacing:.16em}.version strong{font:700 1.1rem "Lucida Console",monospace}.version small{color:#9fb4ba}</style></head>
<body><main><p class="kicker">DIGITAL ITEMS // PERIPHERAL ARCHIVE</p><h1>Code becomes matter.</h1><p class="lead">Versioned TypeScript and Lua reference for Digital Items peripherals. Choose the Minecraft branch or immutable release matching your installation.</p><section class="versions" aria-label="Documentation versions">${cards}</section></main></body></html>\n`;
}

export async function assembleSite({ input, output, plan: rawPlan }) {
  const plan = normalizePlan(rawPlan);
  const inputRoot = resolve(input);
  const outputRoot = resolve(output);
  await rejectSymlinkComponents(inputRoot);
  await rejectSymlinkComponents(outputRoot);
  if (outputRoot === resolve("/") || outputRoot === resolve(".") || within(outputRoot, inputRoot) || within(inputRoot, outputRoot)) {
    fail(`Unsafe output directory: ${output}`);
  }
  await rm(outputRoot, { recursive: true, force: true });
  await mkdir(outputRoot, { recursive: true });
  for (const entry of plan) await copyTree(resolve(inputRoot, entry.path), resolve(outputRoot, entry.path));
  const versions = plan.map((entry) => ({ ...entry, path: `${entry.path}/` }));
  const manifest = { schemaVersion: 1, defaultPath: versions[0].path, versions };
  await writeFile(resolve(outputRoot, "versions.json"), `${JSON.stringify(manifest, null, 2)}\n`);
  await writeFile(resolve(outputRoot, "index.html"), rootHtml(manifest));
  await writeFile(resolve(outputRoot, ".nojekyll"), "");
  return manifest;
}

async function regularFile(path, description) {
  const info = await lstat(path).catch(() => null);
  if (!info?.isFile() || info.isSymbolicLink()) fail(`Missing ${description}: ${path}`);
}

async function htmlFiles(root) {
  const files = [];
  async function walk(directory) {
    for (const name of (await readdir(directory)).sort()) {
      const path = resolve(directory, name);
      const info = await lstat(path);
      if (info.isSymbolicLink()) fail(`Symlinks are not allowed in site: ${path}`);
      if (info.isDirectory()) await walk(path);
      else if (info.isFile() && name.endsWith(".html")) files.push(path);
    }
  }
  await walk(root);
  return files;
}

function localTarget(root, html, rawLink) {
  const link = rawLink.replaceAll("&amp;", "&").trim();
  if (!link || link.startsWith("#") || link.startsWith("//") || /^[A-Za-z][A-Za-z0-9+.-]*:/.test(link)) return null;
  let pathname;
  try {
    pathname = decodeURIComponent(link.split(/[?#]/, 1)[0]);
  } catch {
    fail(`Malformed local link in ${html}: ${rawLink}`);
  }
  if (pathname.includes("\\") || pathname.includes("\0")) fail(`Unsafe local link in ${html}: ${rawLink}`);
  const versionedPath = pathname.match(/^\/.*?\/((?:branch|tag)\/.*)$/)?.[1];
  const target = pathname.startsWith("/")
    ? resolve(root, versionedPath ?? `.${pathname}`)
    : resolve(dirname(html), pathname);
  if (!within(root, target)) fail(`Local link escapes site in ${html}: ${rawLink}`);
  return target;
}

export async function validateSite(site) {
  const root = resolve(site);
  await rejectSymlinkComponents(root);
  const rootInfo = await lstat(root).catch(() => null);
  if (!rootInfo?.isDirectory() || rootInfo.isSymbolicLink()) fail(`Missing site directory: ${site}`);
  await regularFile(resolve(root, "index.html"), "root index.html");
  await regularFile(resolve(root, "versions.json"), "versions.json");
  await regularFile(resolve(root, ".nojekyll"), ".nojekyll");
  let manifest;
  try {
    manifest = JSON.parse(await readFile(resolve(root, "versions.json"), "utf8"));
  } catch (error) {
    fail(`Invalid versions.json: ${error.message}`);
  }
  if (manifest?.schemaVersion !== 1 || !Array.isArray(manifest.versions)) fail("Unsupported versions.json schema");
  const plan = normalizePlan(manifest.versions.map((entry) => ({ ...entry, path: entry.path?.replace(/\/$/, "") })));
  if (!plan.some((entry) => `${entry.path}/` === manifest.defaultPath)) fail("Invalid defaultPath in versions.json");
  for (const entry of plan) {
    const versionRoot = resolve(root, entry.path);
    await regularFile(resolve(versionRoot, "index.html"), `${entry.path} index.html`);
  }
  const canonicalRoot = await realpath(root);
  for (const html of await htmlFiles(root)) {
    const body = await readFile(html, "utf8");
    for (const match of body.matchAll(/\b(?:href|src)\s*=\s*["']([^"']+)["']/gi)) {
      const target = localTarget(root, html, match[1]);
      if (target === null) continue;
      let resolved = target;
      let info = await lstat(resolved).catch(() => null);
      if (info?.isDirectory()) {
        resolved = resolve(resolved, "index.html");
        info = await lstat(resolved).catch(() => null);
      }
      if (!info?.isFile() || info.isSymbolicLink()) fail(`Broken local link in ${relative(root, html)}: ${match[1]}`);
      if (!within(canonicalRoot, await realpath(resolved))) fail(`Local link leaves site in ${relative(root, html)}: ${match[1]}`);
    }
  }
  return plan;
}

function parseOptions(args) {
  const options = {};
  for (let index = 0; index < args.length; index += 2) {
    const name = args[index];
    const value = args[index + 1];
    if (!name?.startsWith("--") || value === undefined || value.startsWith("--") || name in options) fail(`Invalid option: ${name}`);
    options[name] = value;
  }
  return options;
}

function requireOptions(options, required, optional = []) {
  for (const name of required) if (!(name in options)) fail(`Missing required option: ${name}`);
  for (const name of Object.keys(options)) if (![...required, ...optional].includes(name)) fail(`Unknown option: ${name}`);
}

async function main(args) {
  if (!args.length || args.includes("--help") || args[0] === "help") return process.stdout.write(HELP);
  const command = args[0];
  const options = parseOptions(args.slice(1));
  if (command === "discover") {
    requireOptions(options, [], ["--repository", "--github-output", "--plan"]);
    const result = discover(options["--repository"] ?? ".");
    const matrix = JSON.stringify(result.matrix);
    const plan = JSON.stringify(result.plan);
    if (options["--github-output"]) await appendFile(options["--github-output"], `matrix=${matrix}\nplan=${plan}\n`);
    if (options["--plan"]) await writeFile(options["--plan"], `${JSON.stringify(result.plan, null, 2)}\n`);
    return process.stdout.write(`${JSON.stringify(result)}\n`);
  }
  if (command === "assemble") {
    requireOptions(options, ["--input", "--output"], ["--plan", "--plan-json"]);
    if (Boolean(options["--plan"]) === Boolean(options["--plan-json"])) fail("Use exactly one of --plan or --plan-json");
    const source = options["--plan"] ? await readFile(options["--plan"], "utf8") : options["--plan-json"];
    return assembleSite({ input: options["--input"], output: options["--output"], plan: JSON.parse(source) });
  }
  if (command === "validate") {
    requireOptions(options, ["--site"]);
    return validateSite(options["--site"]);
  }
  fail(`Unknown command: ${command}`);
}

if (resolve(process.argv[1] ?? "") === fileURLToPath(import.meta.url)) {
  main(process.argv.slice(2)).catch((error) => {
    process.stderr.write(`docs-site: ${error.message}\n`);
    process.exitCode = 1;
  });
}
