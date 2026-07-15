import { BasicTest, asserts } from "@siredvin/soteria";
import { advancedDigitizerProvider } from "@siredvin/typed-peripheral-digitalitems/advanced_digitizer";
import { digitizerProvider } from "@siredvin/typed-peripheral-digitalitems/digitizer";
import { advancedDigitizerMethods, digitizerMethods } from "../build/generated/contracts/peripheral_methods";
import { runTest } from "./run";

function assertMethods(peripheralObject: IPeripheral, expectedMethods: string[]): void {
    const name = peripheral.getName(peripheralObject);
    const actualMethods = peripheral.getMethods(name);
    asserts.assertNotNull(actualMethods, `Unable to inspect peripheral ${name}`);

    actualMethods.sort();
    asserts.assertEqual(
        actualMethods.join(","),
        expectedMethods.join(","),
        `TypeScript definition does not match in-game peripheral ${name}`,
    );
}

class PeripheralContractsTest extends BasicTest {
    execute(): void {
        assertMethods(advancedDigitizerProvider.findOrThrow(), advancedDigitizerMethods);
        assertMethods(digitizerProvider.findOrThrow(), digitizerMethods);
    }
}

export function runPeripheralContractsTest(): void {
    runTest("peripheral_contracts", new PeripheralContractsTest("typescript_definitions_match"));
}
