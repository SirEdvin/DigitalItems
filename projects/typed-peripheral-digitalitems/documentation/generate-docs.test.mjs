import assert from "node:assert/strict";
import { resolve } from "node:path";
import test from "node:test";
import ts from "typescript";
import { extractPeripheral, renderBlock, renderPeripheral } from "./generate-docs.mjs";

const root = resolve(import.meta.dirname, "..");
const parsed = ts.parseJsonConfigFileContent(
  ts.readConfigFile(resolve(root, "tsconfig.json"), ts.sys.readFile).config,
  ts.sys,
  root,
  { noEmit: true, strictNullChecks: true },
);
const program = ts.createProgram(parsed.fileNames, parsed.options);

test("extracts direct, inherited, and generic digitizer methods", () => {
  const peripheral = extractPeripheral(program, "Digitizer");
  assert.equal(peripheral.peripheralType, "digitizer");
  assert.equal(peripheral.methods.length, 15);
  assert.match(peripheral.methods.find(({ name }) => name === "getConfiguration").signatures[0].signature, /DigitizerConfiguration/);
  assert.equal(peripheral.methods.find(({ name }) => name === "size").inheritedFrom, "InventoryViewAPI");
  const markdown = renderPeripheral(peripheral);
  assert.match(markdown, /\*\*Throws:\*\* A Lua error if the slot is empty/);
  assert.match(markdown, /### `getConfiguration\(\)`/);
  assert.match(markdown, /### `digitize\(slot\)`/);
  assert.doesNotMatch(markdown, /### `[^`]*: [^`]*`/);
  assert.doesNotMatch(markdown, /```typescript/);
});

test("preserves advanced digitizer overloads and nullable tuple unions", () => {
  const peripheral = extractPeripheral(program, "AdvancedDigitizer");
  assert.equal(peripheral.peripheralType, "advanced_digitizer");
  assert.equal(peripheral.methods.length, 11);
  const digitize = peripheral.methods.find(({ name }) => name === "digitize");
  assert.equal(digitize.signatures.length, 2);
  assert.equal(peripheral.methods.find(({ name }) => name === "get").signatures.length, 3);
  assert.match(digitize.signatures[0].signature, /filter\?: number \| string \| LuaTable \| null/);
  assert.match(digitize.signatures[0].signature, /LuaMultiReturn<\[null, string\] \| \[string, null\]>/);
  assert.match(peripheral.methods.find(({ name }) => name === "rematerialize").signatures[0].signature, /LuaMultiReturn<\[null, string\] \| \[number, null\]>/);
  const markdown = renderPeripheral(peripheral);
  assert.match(markdown, /### `digitize\("item", source, filter, limit, destination\)`/);
  assert.match(markdown, /### `digitize\("fluid" \| "energy", source, filter, limit, destination\)`/);
});

test("renders all 16x16 block texture pixels as crisp SVG faces", async () => {
  const textureRoot = resolve(root, "../core/src/main/resources/assets/digitalitems/textures/block");
  const svg = await renderBlock(textureRoot, "digitizer");
  assert.match(svg, /shape-rendering="crispEdges"/);
  assert.equal(svg.match(/<polygon /g).length, 769);
  assert.doesNotMatch(svg, /<image /);
});
