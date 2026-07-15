import { minecraft } from "@siredvin/cc-names";
import { calculateLength } from "@siredvin/cc-utils";
import { BasicTest, asserts } from "@siredvin/soteria";
import { InventoryAPI, inventoryPeripheralProvider } from "@siredvin/typed-peripheral-api/inventory";
import { AdvancedDigitizer, advancedDigitizerProvider } from "@siredvin/typed-peripheral-digitalitems/advanced_digitizer";
import { creativeFillerProvider } from "@siredvin/typed-peripheral-tweakium/creative_filler";
import { runTest } from "./run";

const filler = creativeFillerProvider.findOrThrow();

abstract class ItemTest extends BasicTest {
    constructor(name: string, readonly digitizer: AdvancedDigitizer, readonly target: InventoryAPI, readonly targetName: string, timeout = 10) {
        super(name, timeout);
    }
    bootstrap(): void { asserts.assertEmptyInventory(this.target); }
    fill(item: string, amount = 64): void { filler.put("item", peripheral.getName(this.target), item, amount); }
    teardown(): void {
        for (const [slot] of this.target.list()) this.digitizer.digitize("item", this.targetName, slot);
    }
}

class BaseDigitizeTest extends ItemTest {
    execute(): void {
        this.fill("minecraft:acacia_log", 64);
        this.fill("minecraft:jungle_log", 34);
        asserts.assertEqual(calculateLength(this.target.list()), 2, "Expected two item stacks");
        const [acaciaID, acaciaError] = this.digitizer.digitize("item", this.targetName, 1, 34);
        const [jungleID, jungleError] = this.digitizer.digitize("item", this.targetName, 2);
        asserts.assertNull(acaciaError, "Acacia digitization failed");
        asserts.assertNull(jungleError, "Jungle digitization failed");
        asserts.assertNotNull(acaciaID, "Acacia ID is missing");
        asserts.assertNotNull(jungleID, "Jungle ID is missing");
        asserts.assertEqual(this.target.getItemDetail(1).count, 30, "Acacia remainder does not match");
        asserts.assertNull(this.target.getItemDetail(2), "Jungle stack should be empty");
        asserts.assertEqual(this.digitizer.get("item", acaciaID).item.count, 34, "Digitized acacia count does not match");
        asserts.assertEqual(this.digitizer.get("item", jungleID).item.count, 34, "Digitized jungle count does not match");
        const [acaciaBack, acaciaBackError] = this.digitizer.rematerialize("item", acaciaID, null, this.targetName);
        asserts.assertNull(acaciaBackError, "Acacia rematerialization failed");
        asserts.assertEqual(acaciaBack, 34, "Acacia rematerialized amount does not match");
        asserts.assertEqual(this.target.getItemDetail(1).count, 64, "Acacia did not merge into slot 1");
        asserts.assertNull(this.digitizer.get("item", acaciaID), "Exhausted acacia ID still exists");
        const [jungleBack, jungleBackError] = this.digitizer.rematerialize("item", jungleID, 16, this.targetName);
        asserts.assertNull(jungleBackError, "Jungle rematerialization failed");
        asserts.assertEqual(jungleBack, 16, "Jungle rematerialized amount does not match");
        asserts.assertEqual(this.target.getItemDetail(2).count, 16, "Jungle output does not match");
        asserts.assertEqual(this.digitizer.get("item", jungleID).item.count, 18, "Jungle remainder does not match");
    }
}

class MoreDigitizeTest extends ItemTest {
    execute(): void {
        this.fill("minecraft:oak_log");
        this.fill("minecraft:oak_log");
        this.fill("minecraft:oak_log");
        asserts.assertEqual(calculateLength(this.target.list()), 3, "Expected three item stacks");
        const [id, error1] = this.digitizer.digitize("item", this.targetName, 1);
        asserts.assertNull(error1, "First digitization failed");
        asserts.assertNotNull(id, "First digitization returned no ID");
        const [id2, error2] = this.digitizer.digitize("item", this.targetName, 2, null, id);
        asserts.assertNull(error2, "Second digitization failed");
        asserts.assertEqual(id2, id, "Second digitization changed ID");
        asserts.assertEqual(this.digitizer.get("item", id).item.count, 128, "Second accumulated count does not match");
        const [id3, error3] = this.digitizer.digitize("item", this.targetName, 3, 34, id);
        asserts.assertNull(error3, "Third digitization failed");
        asserts.assertEqual(id3, id, "Third digitization changed ID");
        asserts.assertEqual(this.digitizer.get("item", id).item.count, 162, "Third accumulated count does not match");
        asserts.assertEqual(this.target.getItemDetail(3).count, 30, "Third stack remainder does not match");
        const [amount, error4] = this.digitizer.rematerialize("item", id, 64, this.targetName);
        asserts.assertNull(error4, "Rematerialization failed");
        asserts.assertEqual(amount, 64, "Rematerialized amount does not match");
        asserts.assertEqual(this.digitizer.get("item", id).item.count, 98, "Digital remainder does not match");
        asserts.assertEqual(this.target.getItemDetail(1).count, 64, "Physical output does not match");
    }
}

class LimitTest extends ItemTest {
    constructor(name: string, digitizer: AdvancedDigitizer, target: InventoryAPI, targetName: string) {
        // GameTest advances ticks much faster than wall time while CC's worker is asynchronous.
        super(name, digitizer, target, targetName, 3600);
    }
    execute(): void {
        const limit = this.digitizer.getConfiguration()["itemStackLimit"] as number;
        let stored = 0;
        let id: string | null = null;
        while (stored < limit) {
            const amount = math.min(64, limit - stored);
            this.fill("minecraft:chest", amount);
            const [nextID, error] = this.digitizer.digitize("item", this.targetName, 1, null, id);
            asserts.assertNull(error, "Digitization before configured limit failed");
            asserts.assertNotNull(nextID, "Digitization before configured limit returned no ID");
            if (id != null) asserts.assertEqual(nextID, id, "Accumulation changed ID");
            id = nextID;
            stored += amount;
            asserts.assertLessOrEq(this.digitizer.get("item", id).item.count, limit, "Stored amount exceeded limit");
        }
        this.fill("minecraft:chest", 1);
        const [overflowID, overflowError] = this.digitizer.digitize("item", this.targetName, 1, null, id);
        asserts.assertNotNull(overflowError, "Overflow digitization should fail");
        asserts.assertNull(overflowID, "Overflow digitization unexpectedly returned an ID");
    }
}

export function runItemCase(kind: "base" | "more" | "limit", remote: boolean): void {
    const digitizer = advancedDigitizerProvider.findOrThrow();
    const target = remote ? inventoryPeripheralProvider.findOrThrow(minecraft.chest()) : digitizer;
    const targetName = remote ? peripheral.getName(target) : "self";
    const name = (remote ? "remote_" : "base_") + (kind == "base" ? "digitize" : kind + "_digitize");
    const test = kind == "base"
        ? new BaseDigitizeTest(name, digitizer, target, targetName)
        : kind == "more"
            ? new MoreDigitizeTest(name, digitizer, target, targetName)
            : new LimitTest(name, digitizer, target, targetName);
    runTest("digitizer", test);
}
