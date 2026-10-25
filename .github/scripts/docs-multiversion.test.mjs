import assert from "node:assert/strict";
import { spawnSync } from "node:child_process";
import { mkdtemp, mkdir, writeFile, rm } from "node:fs/promises";
import { tmpdir } from "node:os";
import { dirname, join } from "node:path";
import test from "node:test";
import { COMMON_DOC_FILES, discover, normalizePlan } from "./docs-site.mjs";

test("discover reads all maintained versions from one commit with historical URLs", async () => {
  const root = await mkdtemp(join(tmpdir(), "docs-discover-"));
  try {
    for (const path of [...COMMON_DOC_FILES, "projects/typed-peripheral-digitalitems/mkdocs.yml",
      "projects/typed-peripheral-digitalitems/requirements-docs.txt", "projects/typed-peripheral-digitalitems/documentation/generate-docs.mjs"]) {
      await mkdir(dirname(join(root, path)), { recursive: true });
      await writeFile(join(root, path), path.endsWith("package.json") ? '{"version":"0.2.1"}' : "generateDocs");
    }
    await writeFile(join(root, "gradle.properties"), "minecraftVersion=1.20.1\nmaintainedMinecraftVersions=1.20.1,1.21.1,26.1.2\n");
    const git = (...args) => {
      const result = spawnSync("git", ["-C", root, ...args], { encoding: "utf8" });
      assert.equal(result.status, 0, result.stderr);
      return result.stdout.trim();
    };
    git("init");
    git("add", ".");
    git("-c", "user.name=Fixture", "-c", "user.email=fixture@example.invalid", "commit", "-m", "Fixture");
    const sha = git("rev-parse", "HEAD");
    const { plan } = discover(root, "HEAD");
    assert.deepEqual(plan.map(({ path, minecraftVersion, javaVersion, ref, sha }) => ({ path, minecraftVersion, javaVersion, ref, sha })), [
      { path: "branch/26.1", minecraftVersion: "26.1.2", javaVersion: "25", ref: "origin/multiversion", sha },
      { path: "branch/1.21", minecraftVersion: "1.21.1", javaVersion: "21", ref: "origin/multiversion", sha },
      { path: "branch/1.20", minecraftVersion: "1.20.1", javaVersion: "17", ref: "origin/multiversion", sha },
    ]);
    assert.throws(() => normalizePlan([plan[0], plan[0]]), /Duplicate/);
  } finally {
    await rm(root, { recursive: true, force: true });
  }
});
