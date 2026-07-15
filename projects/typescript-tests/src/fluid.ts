import { BasicTest, asserts } from "@siredvin/soteria";
import { advancedDigitizerProvider } from "@siredvin/typed-peripheral-digitalitems/advanced_digitizer";
import { creativeFillerProvider } from "@siredvin/typed-peripheral-tweakium/creative_filler";
import { fluidStoragePeripheralProvider } from "@siredvin/typed-peripheral-api/fluid_storage";
import { runTest } from "./run";

class FluidTest extends BasicTest {
    execute(): void {
        const digitizer = advancedDigitizerProvider.findOrThrow();
        const target = fluidStoragePeripheralProvider.findOrThrow();
        const targetName = peripheral.getName(target);
        asserts.assertEmptyFluidStorage(target);
        creativeFillerProvider.findOrThrow().put("fluid", targetName, "minecraft:lava", 8000);
        const [id, error1] = digitizer.digitize("fluid", targetName, null, 4000);
        asserts.assertNull(error1, "First fluid digitization failed");
        asserts.assertNotNull(id, "Fluid ID is missing");
        asserts.assertEqual(digitizer.get("fluid", id).fluid.name, "minecraft:lava", "Stored fluid does not match");
        asserts.assertEqual(digitizer.get("fluid", id).fluid.amount, 4000, "First fluid amount does not match");
        const [id2, error2] = digitizer.digitize("fluid", targetName, null, 4000, id);
        asserts.assertNull(error2, "Second fluid digitization failed");
        asserts.assertEqual(id2, id, "Fluid accumulation changed ID");
        asserts.assertEqual(digitizer.get("fluid", id).fluid.amount, 8000, "Accumulated fluid amount does not match");
        const [amount, error3] = digitizer.rematerialize("fluid", id, null, targetName);
        asserts.assertNull(error3, "Fluid rematerialization failed");
        asserts.assertEqual(amount, 8000, "Fluid rematerialized amount does not match");
        asserts.assertEqual(target.tanks()[1].amount, 8000, "Physical fluid amount does not match");
        digitizer.digitize("fluid", targetName);
    }
}

export function runFluidTest(): void { runTest("digitizer", new FluidTest("fluid_digitize")); }
