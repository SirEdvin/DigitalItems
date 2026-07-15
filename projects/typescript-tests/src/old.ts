import { BasicTest, asserts } from "@siredvin/soteria";
import { digitizerProvider } from "@siredvin/typed-peripheral-digitalitems/digitizer";
import { creativeFillerProvider } from "@siredvin/typed-peripheral-tweakium/creative_filler";
import { assertDigitizerMethods } from "./peripheral_contracts";
import { runTest } from "./run";

class OldDigitizerTest extends BasicTest {
    execute(): void {
        const digitizer = digitizerProvider.findOrThrow();
        assertDigitizerMethods(digitizer);
        asserts.assertEmptyInventory(digitizer);
        creativeFillerProvider.findOrThrow().put("item", peripheral.getName(digitizer), "minecraft:oak_log", 64);
        const id = digitizer.digitize();
        const info = digitizer.getIDInfo(id);
        asserts.assertNotNull(info, "Digitized item info is missing");
        asserts.assertEqual(info.item.name, "minecraft:oak_log", "Digitized item does not match");
        asserts.assertEqual(info.item.count, 64, "Digitized count does not match");
        digitizer.rematerialize(id);
        asserts.assertEqual(digitizer.getItemDetail(1).name, "minecraft:oak_log", "Rematerialized item does not match");
        asserts.assertEqual(digitizer.getItemDetail(1).count, 64, "Rematerialized count does not match");
        digitizer.digitize();
    }
}

export function runOldTest(): void { runTest("old_digitizer", new OldDigitizerTest("base_digitize")); }
