import { mkdirSync, writeFileSync } from "node:fs";
import { dirname, resolve } from "node:path";
import ts from "typescript";

const contracts = [
  {
    moduleName: "@siredvin/typed-peripheral-digitalitems/advanced_digitizer",
    typeName: "AdvancedDigitizer",
    exportName: "advancedDigitizerMethods",
  },
  {
    moduleName: "@siredvin/typed-peripheral-digitalitems/digitizer",
    typeName: "Digitizer",
    exportName: "digitizerMethods",
  },
];

const configPath = ts.findConfigFile(".", ts.sys.fileExists, "tsconfig.json");
if (configPath === undefined) throw new Error("Unable to find tsconfig.json");

const config = ts.readConfigFile(configPath, ts.sys.readFile);
if (config.error !== undefined) {
  throw new Error(ts.flattenDiagnosticMessageText(config.error.messageText, "\n"));
}

const parsedConfig = ts.parseJsonConfigFileContent(config.config, ts.sys, dirname(configPath));
const containingFile = resolve("src/peripheral-contracts.ts");
const resolvedContracts = contracts.map((contract) => {
  const resolved = ts.resolveModuleName(contract.moduleName, containingFile, parsedConfig.options, ts.sys).resolvedModule;
  if (resolved === undefined) throw new Error(`Unable to resolve ${contract.moduleName}`);
  return { ...contract, fileName: resolved.resolvedFileName };
});

const program = ts.createProgram(
  [...parsedConfig.fileNames, ...resolvedContracts.map((contract) => contract.fileName)],
  parsedConfig.options,
);
const checker = program.getTypeChecker();
const output = [];
for (const contract of resolvedContracts) {
  const sourceFile = program.getSourceFile(contract.fileName);
  const moduleSymbol = sourceFile && checker.getSymbolAtLocation(sourceFile);
  const typeSymbol = moduleSymbol && checker.getExportsOfModule(moduleSymbol)
    .find((symbol) => symbol.name === contract.typeName);
  if (typeSymbol === undefined) throw new Error(`Unable to find ${contract.typeName} in ${contract.moduleName}`);

  const peripheralType = checker.getDeclaredTypeOfSymbol(typeSymbol);
  const methods = checker.getPropertiesOfType(peripheralType)
    .filter((property) => {
      const declaration = property.valueDeclaration ?? property.declarations?.[0];
      return declaration !== undefined && checker.getTypeOfSymbolAtLocation(property, declaration).getCallSignatures().length > 0;
    })
    .map((property) => property.name)
    .sort();

  output.push(`export const ${contract.exportName}: string[] = ${JSON.stringify(methods)};`);
}

const outputPath = resolve("build/generated/contracts/peripheral_methods.ts");
mkdirSync(dirname(outputPath), { recursive: true });
writeFileSync(outputPath, `${output.join("\n")}\n`);
