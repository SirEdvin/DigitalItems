import { BasicTest, asserts } from "@siredvin/soteria";
import { advancedDigitizerProvider } from "@siredvin/typed-peripheral-digitalitems/advanced_digitizer";
import { creativeFillerProvider } from "@siredvin/typed-peripheral-tweakium/creative_filler";
import { energyStoragePeripheralProvider } from "@siredvin/typed-peripheral-api/energy_storage";
import { runTest } from "./run";

class EnergyTest extends BasicTest {
    execute(): void {
        const digitizer = advancedDigitizerProvider.findOrThrow();
        const target = energyStoragePeripheralProvider.findOrThrow();
        const targetName = peripheral.getName(target);
        asserts.assertEmptyEnergyStorage(target);
        const unit = target.getEnergyUnit();
        creativeFillerProvider.findOrThrow().put("energy", targetName, unit, 10000);
        const [id, error1] = digitizer.digitize("energy", targetName, null, 2000);
        asserts.assertNull(error1, "First energy digitization failed");
        asserts.assertNotNull(id, "Energy ID is missing");
        asserts.assertEqual(digitizer.get("energy", id).energy.unit, unit, "Stored energy unit does not match");
        asserts.assertEqual(digitizer.get("energy", id).energy.amount, 2000, "First energy amount does not match");
        const [id2, error2] = digitizer.digitize("energy", targetName, null, 4000, id);
        asserts.assertNull(error2, "Second energy digitization failed");
        asserts.assertEqual(id2, id, "Energy accumulation changed ID");
        asserts.assertEqual(digitizer.get("energy", id).energy.amount, 6000, "Accumulated energy amount does not match");
        const [amount, error3] = digitizer.rematerialize("energy", id, null, targetName);
        asserts.assertNull(error3, "Energy rematerialization failed");
        asserts.assertEqual(amount, 6000, "Energy rematerialized amount does not match");
        asserts.assertEqual(target.getEnergy(), 10000, "Physical energy amount does not match");
        digitizer.digitize("energy", targetName, null, 10000);
    }
}

export function runEnergyTest(): void { runTest("digitizer", new EnergyTest("energy_digitize")); }
