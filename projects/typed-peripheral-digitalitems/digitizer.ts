import { ConfigurationAPI } from "@siredvin/typed-peripheral-api/configuration";
import { InventoryAPI } from "@siredvin/typed-peripheral-api/inventory";
import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";
import { ItemIDInfo } from "./shared";

export declare interface DigitizerConfiguration {
  inventoryAPIVersion: [number, number];
}

/** @noSelf **/
export interface Digitizer
  extends IPeripheral,
    InventoryAPI,
    ConfigurationAPI<DigitizerConfiguration> {
  getDecayEnabled(): boolean;
  getDecayTicks(): number;
  digitize(slot?: number): string;
  rematerialize(id: string): number;
  digitizeAmount(amount: number, slot?: number): string;
  rematerializeAmount(id: string, amount: number): number;
  refresh(id: string): void;
  getIDInfo(id: string): ItemIDInfo;
}

export const digitizerProvider = new IPeripheralProvider<Digitizer>(
  "digitizer",
  () => null
);
