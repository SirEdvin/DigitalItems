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
        const [stored] = digitizer.digitize("energy", targetName, null, 10000);
        asserts.assertNotNull(stored, "Conservation fixture needs a stored energy ID");
        creativeFillerProvider.findOrThrow().put("energy", targetName, unit, 99000);
        const [invalid, invalidError] = digitizer.digitize("energy", targetName, null, 1000, "invalid-id");
        asserts.assertNull(invalid, "Invalid destination ID must reject");
        asserts.assertEqual(invalidError, "Destination doesn't exists anymore", "Invalid energy ID error changed");
        asserts.assertEqual(target.getEnergy(), 99000, "Rejected extraction drained physical energy");
        asserts.assertEqual(digitizer.get("energy", stored).energy.amount, 10000, "Rejected extraction changed digital energy");
        const [partial, partialError] = digitizer.rematerialize("energy", stored, null, targetName);
        asserts.assertNull(partialError, "Partial energy insertion failed");
        asserts.assertEqual(partial, 1000, "Partial energy transfer report changed");
        asserts.assertEqual(target.getEnergy(), 100000, "Partial energy insertion physical amount");
        asserts.assertEqual(digitizer.get("energy", stored).energy.amount, 9000, "Partial energy insertion digital remainder");
        const [full, fullError] = digitizer.rematerialize("energy", stored, null, targetName);
        asserts.assertNull(full, "Full energy destination must reject");
        asserts.assertEqual(fullError, "Unable to place rematerialized object", "Full energy error changed");
        asserts.assertEqual(target.getEnergy(), 100000, "Full rejection changed physical energy");
        asserts.assertEqual(digitizer.get("energy", stored).energy.amount, 9000, "Full rejection consumed digital energy");
        digitizer.digitize("energy", targetName);
        const [recovered] = digitizer.rematerialize("energy", stored, null, targetName);
        asserts.assertEqual(recovered, 9000, "Rejected energy must remain redeemable");
        asserts.assertEqual(target.getEnergy(), 9000, "Recovered energy physical amount");
        asserts.assertNull(digitizer.get("energy", stored), "Exhausted energy ID must be removed");
        digitizer.digitize("energy", targetName);
    }
}

export function runEnergyTest(): void { runTest("digitizer", new EnergyTest("energy_digitize")); }
