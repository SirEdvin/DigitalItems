import { asserts } from "@siredvin/soteria";
import { AdvancedDigitizer } from "@siredvin/typed-peripheral-digitalitems/advanced_digitizer";
import { Digitizer } from "@siredvin/typed-peripheral-digitalitems/digitizer";
import { advancedDigitizerMethods, digitizerMethods } from "../build/generated/contracts/peripheral_methods";

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

export function assertAdvancedDigitizerMethods(digitizer: AdvancedDigitizer): void {
    assertMethods(digitizer, advancedDigitizerMethods);
}

export function assertDigitizerMethods(digitizer: Digitizer): void {
    assertMethods(digitizer, digitizerMethods);
}
