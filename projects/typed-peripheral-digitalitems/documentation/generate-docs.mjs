import { cp, mkdir, readFile, rm, writeFile } from "node:fs/promises";
import { spawnSync } from "node:child_process";
import { dirname, relative, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import ts from "typescript";

const projectRoot = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const peripherals = [
  { interfaceName: "Digitizer", page: "guides/basic-digitizer.md" },
  { interfaceName: "AdvancedDigitizer", page: "guides/advanced-items.md" },
];

function text(parts) {
  return ts.displayPartsToString(parts).trim();
}

function tagsFor(signature, checker) {
  const result = { params: new Map(), returns: "", throws: [] };
  for (const tag of signature.getJsDocTags(checker)) {
    const value = text(tag.text);
    if (tag.name === "param") {
      const match = /^(\S+)\s*-?\s*(.*)$/s.exec(value);
      if (match) result.params.set(match[1], match[2]);
    } else if (tag.name === "returns" || tag.name === "return") result.returns = value;
    else if (tag.name === "throws") result.throws.push(value);
  }
  return result;
}

function sourceInterface(symbol) {
  const declaration = symbol.valueDeclaration ?? symbol.declarations?.[0];
  let current = declaration;
  while (current && !ts.isInterfaceDeclaration(current)) current = current.parent;
  return current?.name.text;
}

export function extractPeripheral(program, interfaceName) {
  const checker = program.getTypeChecker();
  const matches = [];
  for (const sourceFile of program.getSourceFiles()) {
    if (sourceFile.isDeclarationFile && sourceFile.fileName.includes("typescript/lib/")) continue;
    ts.forEachChild(sourceFile, (node) => {
      if (ts.isInterfaceDeclaration(node) && node.name.text === interfaceName) matches.push(node);
    });
  }
  if (matches.length !== 1) throw new Error(`Expected one ${interfaceName} interface, found ${matches.length}`);
  const declaration = matches[0];
  let peripheralType = null;
  for (const sourceFile of program.getSourceFiles()) {
    const visit = (node) => {
      if (ts.isNewExpression(node)
        && node.typeArguments?.[0]?.getText() === interfaceName
        && node.arguments?.[0]
        && ts.isStringLiteral(node.arguments[0])) {
        peripheralType = node.arguments[0].text;
      }
      ts.forEachChild(node, visit);
    };
    ts.forEachChild(sourceFile, visit);
  }
  if (!peripheralType) throw new Error(`No peripheral provider found for ${interfaceName}`);
  const type = checker.getTypeAtLocation(declaration);
  const methods = checker.getPropertiesOfType(type).flatMap((symbol) => {
    const methodType = checker.getTypeOfSymbolAtLocation(symbol, declaration);
    const signatures = checker.getSignaturesOfType(methodType, ts.SignatureKind.Call);
    if (!signatures.length) return [];
    return [{
      name: symbol.name,
      inheritedFrom: sourceInterface(symbol) === interfaceName ? null : sourceInterface(symbol),
      signatures: signatures.map((signature) => {
        const tags = tagsFor(signature, checker);
        const parameters = signature.getParameters().map((parameter) => {
          const parameterDeclaration = parameter.valueDeclaration ?? parameter.declarations?.[0] ?? declaration;
          return {
            name: parameter.name,
            type: checker.typeToString(checker.getTypeOfSymbolAtLocation(parameter, parameterDeclaration), declaration, ts.TypeFormatFlags.NoTruncation),
            optional: Boolean(parameter.flags & ts.SymbolFlags.Optional) || Boolean(parameterDeclaration.questionToken),
            description: tags.params.get(parameter.name) ?? "",
          };
        });
        return {
          signature: `${symbol.name}${checker.signatureToString(signature, declaration, ts.TypeFormatFlags.NoTruncation | ts.TypeFormatFlags.UseAliasDefinedOutsideCurrentScope)}`,
          summary: text(signature.getDocumentationComment(checker)),
          parameters,
          returns: tags.returns,
          throws: tags.throws,
        };
      }),
    }];
  });
  if (!methods.length) throw new Error(`${interfaceName} has no methods`);
  return { interfaceName, peripheralType, methods };
}

function escapeCell(value) {
  return value.replaceAll("|", "\\|").replaceAll("\n", " ");
}

async function renderBlock(textureRoot, block) {
  const texture = async (face) => (await readFile(resolve(textureRoot, `${block}_${face}.png`))).toString("base64");
  const [side, front, top] = await Promise.all([texture("side"), texture("front_on"), texture("top")]);
  return `<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 128 128">
  <polygon points="12,83 64,113 116,83 64,53" fill="#000" opacity=".28"/>
  <image href="data:image/png;base64,${side}" width="16" height="16" image-rendering="pixelated" transform="matrix(3 1.5 0 3 16 32)"/>
  <image href="data:image/png;base64,${front}" width="16" height="16" image-rendering="pixelated" transform="matrix(3 -1.5 0 3 64 56)"/>
  <image href="data:image/png;base64,${top}" width="16" height="16" image-rendering="pixelated" transform="matrix(3 1.5 -3 1.5 64 8)"/>
</svg>\n`;
}

export function renderPeripheral(peripheral, revision = "") {
  const lines = [
    "## Peripheral methods",
    "",
    "The reference below is generated from the published TypeScript interface.",
    "",
  ];
  for (const method of peripheral.methods) {
    for (const signature of method.signatures) {
      lines.push(`### \`${signature.signature}\``, "");
      if (method.inheritedFrom) lines.push(`*Inherited from \`${method.inheritedFrom}\`.*`, "");
      if (signature.summary) lines.push(signature.summary, "");
      if (signature.parameters.length) {
        lines.push("| Parameter | Type | Description |", "| --- | --- | --- |");
        for (const parameter of signature.parameters) {
          const name = `\`${parameter.name}${parameter.optional ? "?" : ""}\``;
          lines.push(`| ${name} | \`${escapeCell(parameter.type)}\` | ${escapeCell(parameter.description)} |`);
        }
        lines.push("");
      }
      if (signature.returns) lines.push(`**Returns:** ${signature.returns}`, "");
      for (const error of signature.throws) lines.push(`**Throws:** ${error}`, "");
    }
  }
  if (revision) lines.push(`[View TypeScript source](https://github.com/SirEdvin/DigitalItems/tree/${revision}/projects/typed-peripheral-digitalitems)`, "");
  return lines.join("\n");
}

function options(args) {
  const result = { out: resolve(projectRoot, "docs"), revision: "", siteUrl: "" };
  for (let index = 0; index < args.length; index += 2) {
    const value = args[index + 1];
    if (!value) throw new Error(`Missing value for ${args[index]}`);
    if (args[index] === "--out") result.out = resolve(value);
    else if (args[index] === "--gitRevision") result.revision = value;
    else if (args[index] === "--hostedBaseUrl") result.siteUrl = value;
    else throw new Error(`Unknown option: ${args[index]}`);
  }
  return result;
}

export async function buildDocs(args = []) {
  const config = options(args);
  const buildRoot = resolve(projectRoot, ".mkdocs-build");
  await rm(buildRoot, { recursive: true, force: true });
  await cp(resolve(projectRoot, "documentation"), buildRoot, {
    recursive: true,
    filter: (path) => !path.endsWith("generate-docs.mjs") && !path.endsWith("generate-docs.test.mjs") && !path.includes("/theme/"),
  });
  await mkdir(resolve(buildRoot, "assets/peripherals"), { recursive: true });
  const textureRoot = resolve(projectRoot, "../core/src/main/resources/assets/digitalitems/textures/block");
  for (const block of ["digitizer", "advanced_digitizer"]) {
    await writeFile(resolve(buildRoot, "assets/peripherals", `${block}.svg`), await renderBlock(textureRoot, block));
  }
  await mkdir(resolve(buildRoot, "assets/stylesheets"), { recursive: true });
  await mkdir(resolve(buildRoot, "assets/javascripts"), { recursive: true });
  await cp(resolve(projectRoot, "documentation/theme/digitalitems.css"), resolve(buildRoot, "assets/stylesheets/digitalitems.css"));
  await cp(resolve(projectRoot, "documentation/theme/digitalitems.js"), resolve(buildRoot, "assets/javascripts/digitalitems.js"));
  await cp(resolve(projectRoot, "documentation/theme/favicon.svg"), resolve(buildRoot, "assets/favicon.svg"));

  const parsed = ts.parseJsonConfigFileContent(
    ts.readConfigFile(resolve(projectRoot, "tsconfig.json"), ts.sys.readFile).config,
    ts.sys,
    projectRoot,
    { noEmit: true, strictNullChecks: true },
  );
  const program = ts.createProgram(parsed.fileNames, parsed.options);
  const errors = ts.getPreEmitDiagnostics(program);
  if (errors.length) throw new Error(ts.formatDiagnosticsWithColorAndContext(errors, {
    getCanonicalFileName: (name) => name,
    getCurrentDirectory: () => projectRoot,
    getNewLine: () => "\n",
  }));
  for (const peripheral of peripherals) {
    const path = resolve(buildRoot, peripheral.page);
    const markdown = await readFile(path, "utf8");
    const extracted = extractPeripheral(program, peripheral.interfaceName);
    await writeFile(path, `${markdown.replaceAll("{{ peripheralType }}", extracted.peripheralType).trim()}\n\n${renderPeripheral(extracted, config.revision)}\n`);
  }

  await rm(config.out, { recursive: true, force: true });
  const result = spawnSync("python3", ["-m", "mkdocs", "build", "--strict", "--clean", "--site-dir", config.out], {
    cwd: projectRoot,
    env: config.siteUrl ? { ...process.env, DOCS_SITE_URL: config.siteUrl } : process.env,
    encoding: "utf8",
  });
  if (result.status !== 0) throw new Error((result.stderr || result.stdout).trim());
  await rm(buildRoot, { recursive: true, force: true });
  return relative(projectRoot, config.out);
}

if (resolve(process.argv[1] ?? "") === fileURLToPath(import.meta.url)) {
  buildDocs(process.argv.slice(2)).catch((error) => {
    process.stderr.write(`generate-docs: ${error.message}\n`);
    process.exitCode = 1;
  });
}
