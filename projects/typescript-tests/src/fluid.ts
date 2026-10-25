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
        const [stored] = digitizer.digitize("fluid", targetName);
        asserts.assertNotNull(stored, "Conservation fixture needs a stored fluid ID");
        const filler = creativeFillerProvider.findOrThrow();
        filler.put("fluid", targetName, "minecraft:water", 1000);
        const [merged, mergeError] = digitizer.digitize("fluid", targetName, null, 1000, stored);
        asserts.assertNull(merged, "Different fluids must not merge");
        asserts.assertEqual(mergeError, "Can't merge found item into destination, they are different", "Fluid rejection error changed");
        asserts.assertEqual(target.tanks()[1].amount, 1000, "Rejected simulated extraction drained physical fluid");
        asserts.assertEqual(digitizer.get("fluid", stored).fluid.amount, 8000, "Rejected extraction changed digital fluid");
        const [rejected, rejectedError] = digitizer.rematerialize("fluid", stored, null, targetName);
        asserts.assertNull(rejected, "Incompatible fluid destination must reject");
        asserts.assertEqual(rejectedError, "Unable to place rematerialized object", "Fluid storage error changed");
        asserts.assertEqual(target.tanks()[1].amount, 1000, "Rejected insertion changed physical fluid");
        asserts.assertEqual(digitizer.get("fluid", stored).fluid.amount, 8000, "Rejected insertion consumed digital fluid");
        digitizer.digitize("fluid", targetName);
        filler.put("fluid", targetName, "minecraft:lava", 99000);
        const [partial, partialError] = digitizer.rematerialize("fluid", stored, null, targetName);
        asserts.assertNull(partialError, "Partial fluid insertion failed");
        asserts.assertEqual(partial, 1000, "Partial fluid transfer report changed");
        asserts.assertEqual(target.tanks()[1].amount, 100000, "Partial fluid insertion physical amount");
        asserts.assertEqual(digitizer.get("fluid", stored).fluid.amount, 7000, "Partial fluid insertion digital remainder");
        const [full, fullError] = digitizer.rematerialize("fluid", stored, null, targetName);
        asserts.assertNull(full, "Full fluid destination must reject");
        asserts.assertEqual(fullError, "Unable to place rematerialized object", "Full fluid error changed");
        asserts.assertEqual(target.tanks()[1].amount, 100000, "Full rejection changed physical fluid");
        asserts.assertEqual(digitizer.get("fluid", stored).fluid.amount, 7000, "Full rejection consumed digital fluid");
        digitizer.digitize("fluid", targetName);
        const [recovered] = digitizer.rematerialize("fluid", stored, null, targetName);
        asserts.assertEqual(recovered, 7000, "Rejected fluid must remain redeemable");
        asserts.assertEqual(target.tanks()[1].amount, 7000, "Recovered fluid physical amount");
        asserts.assertNull(digitizer.get("fluid", stored), "Exhausted fluid ID must be removed");
        digitizer.digitize("fluid", targetName);
    }
}

export function runFluidTest(): void { runTest("digitizer", new FluidTest("fluid_digitize")); }
