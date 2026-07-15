import { calculateLength } from "cc-corelib/utils";
import { InventoryAPI } from "@siredvin/typed-peripheral-api/inventory";
import { FluidStorageAPI } from "@siredvin/typed-peripheral-api/fluid_storage";
import { EnergyStorageAPI } from "@siredvin/typed-peripheral-api/energy_storage";

export function assert(value: boolean, message: string): void { if (!value) throw message; }
export function assertNotNull(value: any | null, message: string): void { if (value == null) throw message; }
export function assertNull(value: any | null, message: string): void { if (value != null) throw message + " " + value; }
export function assertEqual(value: any, expected: any, message: string): void {
    if (value != expected) throw message + " real value " + value + " but " + expected + " expected";
}
export function assertLessOrEq(value: number, expected: number, message: string): void {
    if (value > expected) throw message + " real value " + value + " is bigger than " + expected;
}
export function assertEmptyInventory(inventory: InventoryAPI): void {
    assert(calculateLength(inventory.list()) == 0, "Inventory " + peripheral.getName(inventory) + " is not empty");
}
export function assertEmptyFluidStorage(storage: FluidStorageAPI): void {
    for (const [_slot, tank] of storage.tanks()) assertEqual(tank.amount, 0, "Fluid storage tank should be empty");
}
export function assertEmptyEnergyStorage(storage: EnergyStorageAPI): void {
    assertEqual(storage.getEnergy(), 0, "Energy storage should be empty");
}
