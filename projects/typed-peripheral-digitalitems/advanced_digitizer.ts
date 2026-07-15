import { ConfigurationAPI } from "@siredvin/typed-peripheral-api/configuration";
import { InventoryAPI } from "@siredvin/typed-peripheral-api/inventory";
import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";
import { IDInfo, ItemIDInfo } from "./shared";

export declare type FluidIDInfo = IDInfo & {
  fluid: {
    amount: number;
    name: string;
  };
};

export declare type EnergyIDInfo = IDInfo & {
  energy: {
    amount: number;
    unit: string;
  };
};

export declare interface AdvancedDigitizerConfiguration {
  decayEnabled: boolean;
  decayTicks: number;
  energyStackLimit: number;
  fluidStackLimit: number;
  itemStackLimit: number;
  inventoryAPIVersion: [number, number];
}

/** @noSelf **/
export interface AdvancedDigitizer
  extends IPeripheral,
    InventoryAPI,
    ConfigurationAPI<AdvancedDigitizerConfiguration> {
  digitize(
    mode: "item",
    source?: string,
    filter?: number | LuaTable,
    limit?: number,
    destination?: string
  ): LuaMultiReturn<[null, string] | [string, null]>;
  digitize(
    mode: "fluid" | "energy",
    source?: string,
    filter?: string,
    limit?: number,
    destination?: string
  ): LuaMultiReturn<[null, string] | [string, null]>;
  rematerialize(
    mode: "item" | "fluid" | "energy",
    id: string,
    limit?: number,
    destination?: string
  ): LuaMultiReturn<[null, string] | [number, null]>;
  refresh(mode: "item" | "fluid" | "energy", id: string): boolean;
  get(mode: "item", id: string): ItemIDInfo | null;
  get(mode: "fluid", id: string): FluidIDInfo | null;
  get(mode: "energy", id: string): EnergyIDInfo | null;
}

export const advancedDigitizerProvider =
  new IPeripheralProvider<AdvancedDigitizer>(
    "advanced_digitizer",
    () => null
  );
