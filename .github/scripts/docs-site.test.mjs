import assert from "node:assert/strict";
import { mkdtemp, mkdir, readFile, symlink, unlink, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import { join } from "node:path";
import test from "node:test";

import { assembleSite, normalizePlan, safeTagName, sortEntries, validateSite } from "./docs-site.mjs";

const SHA_A = "a".repeat(40);
const SHA_B = "b".repeat(40);

function entry(kind, name, sha = SHA_A) {
  return {
    kind,
    name,
    label: name,
    path: `${kind}/${name}`,
    ref: kind === "branch" ? `origin/${name}` : `refs/tags/${name}`,
    sha,
    minecraftVersion: kind === "branch" ? `${name}.1` : "1.20.1",
    packageVersion: "0.2.1",
    javaVersion: kind === "branch" && name === "1.21" ? "21" : "17",
  };
}

async function fragment(root, path, html = '<a href="assets/main.js">asset</a>') {
  const directory = join(root, path);
  await mkdir(join(directory, "assets"), { recursive: true });
  await writeFile(join(directory, "index.html"), html);
  for (const asset of ["main.js", "search.js", "navigation.js", "custom.css", "custom.js"]) {
    await writeFile(join(directory, "assets", asset), "// docs\n");
  }
}

test("sortEntries groups branches before descending tags", () => {
  const sorted = sortEntries([
    entry("tag", "v1.20.1-0.3.0-pre.1"),
    entry("branch", "1.20"),
    entry("tag", "v1.20.1-0.3.0"),
    entry("branch", "1.21"),
  ]);
  assert.deepEqual(sorted.map(({ path }) => path), [
    "branch/1.21",
    "branch/1.20",
    "tag/v1.20.1-0.3.0",
    "tag/v1.20.1-0.3.0-pre.1",
  ]);
});

test("safeTagName accepts only path-safe release tags", () => {
  assert.equal(safeTagName("refs/tags/v1.20.1-0.5.8"), "v1.20.1-0.5.8");
  for (const ref of ["1.21", "v../1.21", "v1.21/evil", "v1.21_2", "refs/tags/v1.21%2f.."]) {
    assert.equal(safeTagName(ref), null);
  }
});

test("sortEntries follows SemVer prerelease precedence", () => {
  const sorted = sortEntries([
    entry("tag", "v1.20.1-0.5.8-1"),
    entry("tag", "v1.20.1-0.5.8-alpha"),
  ]);
  assert.deepEqual(sorted.map(({ name }) => name), ["v1.20.1-0.5.8-alpha", "v1.20.1-0.5.8-1"]);
});

test("normalizePlan rejects traversal, mismatched refs, and paths", () => {
  assert.throws(() => normalizePlan([{ ...entry("branch", "1.20"), name: "../escape" }]), /Unsafe/);
  assert.throws(() => normalizePlan([{ ...entry("branch", "1.20"), ref: "origin/feature" }]), /Ref does not match/);
  assert.throws(() => normalizePlan([{ ...entry("branch", "1.20"), path: "tag/1.20" }]), /Path must be/);
});

test("normalizePlan rejects duplicate entries", () => {
  assert.throws(() => normalizePlan([entry("branch", "1.20"), entry("branch", "1.20", SHA_B)]), /Duplicate/);
});

test("assemble writes a versioned manifest and validates the site", async () => {
  const root = await mkdtemp(join(tmpdir(), "docs-site-"));
  const input = join(root, "input");
  const output = join(root, "output");
  await fragment(input, "branch/1.20");
  await fragment(input, "tag/v1.20.1-0.5.8");
  await assembleSite({
    input,
    output,
    plan: [entry("branch", "1.20"), entry("tag", "v1.20.1-0.5.8", SHA_B)],
  });
  const manifest = JSON.parse(await readFile(join(output, "versions.json"), "utf8"));
  assert.equal(manifest.schemaVersion, 1);
  assert.equal(manifest.defaultPath, "branch/1.20/");
  assert.deepEqual(manifest.versions.map(({ path }) => path), ["branch/1.20/", "tag/v1.20.1-0.5.8/"]);
  await validateSite(output);
});

test("assemble rejects a missing fragment", async () => {
  const root = await mkdtemp(join(tmpdir(), "docs-site-missing-"));
  await assert.rejects(
    assembleSite({ input: join(root, "input"), output: join(root, "output"), plan: [entry("branch", "1.20")] }),
    /Missing or unsafe docs fragment/,
  );
});

test("validate rejects links escaping the site", async () => {
  const root = await mkdtemp(join(tmpdir(), "docs-site-links-"));
  const input = join(root, "input");
  const output = join(root, "output");
  await fragment(input, "branch/1.20", '<a href="../../../../outside.html">outside</a>');
  await assembleSite({ input, output, plan: [entry("branch", "1.20")] });
  await assert.rejects(validateSite(output), /escapes site/);
});

test("validate rejects missing TypeDoc assets", async () => {
  const root = await mkdtemp(join(tmpdir(), "docs-site-assets-"));
  const input = join(root, "input");
  const output = join(root, "output");
  await fragment(input, "branch/1.20");
  await assembleSite({ input, output, plan: [entry("branch", "1.20")] });
  await unlink(join(output, "branch", "1.20", "assets", "search.js"));
  await assert.rejects(validateSite(output), /Missing branch\/1.20 search.js/);
});

test("assemble rejects a symlinked output ancestor", async () => {
  const root = await mkdtemp(join(tmpdir(), "docs-site-symlink-"));
  const input = join(root, "input");
  const target = join(root, "target");
  const linked = join(root, "linked");
  await fragment(input, "branch/1.20");
  await mkdir(target);
  await symlink(target, linked, "dir");
  await assert.rejects(
    assembleSite({ input, output: join(linked, "site"), plan: [entry("branch", "1.20")] }),
    /Symlinked path component/,
  );
});
