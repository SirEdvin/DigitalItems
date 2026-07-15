import { cpSync, mkdirSync, rmSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { spawnSync } from "node:child_process";

const entries = [
  "base_digitize",
  "remote_digitize",
  "base_more_digitize",
  "remote_more_digitize",
  "base_limit_digitize",
  "remote_limit_digitize",
  "fluid_digitize",
  "energy_digitize",
  "old_digitize"
];
const output = resolve("build/generated/test-lua");
const soteriaSource = resolve("build/soteria-source");
rmSync(output, { recursive: true, force: true });
rmSync(soteriaSource, { recursive: true, force: true });
mkdirSync(output, { recursive: true });
mkdirSync(soteriaSource, { recursive: true });
for (const file of ["index.ts", "base.ts", "asserts.ts", "reports.ts"]) {
  cpSync(resolve("node_modules/@siredvin/soteria", file), resolve(soteriaSource, file));
}

const contractsResult = spawnSync(process.execPath, ["generate-peripheral-contracts.mjs"], { stdio: "inherit" });
if (contractsResult.status !== 0) process.exit(contractsResult.status ?? 1);

for (const entry of entries) {
  const bundle = resolve(output, `digitalitemsgametests.${entry}.lua`);
  const result = spawnSync(
    resolve("node_modules/.bin/tstl"),
    ["-p", "tsconfig.json", "--luaBundle", bundle, "--luaBundleEntry", `src/entries/${entry}.ts`],
    { stdio: "inherit" }
  );
  if (result.status !== 0) process.exit(result.status ?? 1);
}
