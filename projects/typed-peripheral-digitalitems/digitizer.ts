import { ConfigurationAPI } from "@siredvin/typed-peripheral-api/configuration";
import { InventoryAPI } from "@siredvin/typed-peripheral-api/inventory";
import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";
import { ItemIDInfo } from "./shared";

/** @noSelf **/
export interface Digitizer
  extends IPeripheral,
    InventoryAPI,
    ConfigurationAPI<object> {
  getDecayEnabled(): boolean;
  getDecayTicks(): number;
  digitize(): string;
  rematerialize(id: string);
  digitizeAmount(amount: number): string;
  rematerializeAmount(id: string, amount: number);
  refresh(id: string);
  getIDInfo(id): ItemIDInfo;
}

export const digitizerProvider = new IPeripheralProvider<Digitizer>(
  "digitizer",
  () => null
);
