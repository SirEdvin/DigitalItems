import assert from "node:assert/strict";
import { resolve } from "node:path";
import test from "node:test";
import ts from "typescript";
import { extractPeripheral, renderPeripheral } from "./generate-docs.mjs";

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
  assert.match(renderPeripheral(peripheral), /\*\*Throws:\*\* A Lua error if the slot is empty/);
});

test("preserves advanced digitizer overloads and nullable tuple unions", () => {
  const peripheral = extractPeripheral(program, "AdvancedDigitizer");
  assert.equal(peripheral.peripheralType, "advanced_digitizer");
  assert.equal(peripheral.methods.length, 11);
  assert.equal(peripheral.methods.find(({ name }) => name === "digitize").signatures.length, 2);
  assert.equal(peripheral.methods.find(({ name }) => name === "get").signatures.length, 3);
  const markdown = renderPeripheral(peripheral);
  assert.match(markdown, /filter\?: number \| string \| LuaTable \| null/);
  assert.match(markdown, /LuaMultiReturn<\[null, string\] \| \[number, null\]>/);
});
