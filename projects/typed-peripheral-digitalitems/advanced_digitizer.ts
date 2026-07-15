import { ConfigurationAPI } from "@siredvin/typed-peripheral-api/configuration";
import { InventoryAPI } from "@siredvin/typed-peripheral-api/inventory";
import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";
import { IDInfo, ItemIDInfo } from "./shared";

/** Metadata and current contents associated with a fluid identifier. */
export declare type FluidIDInfo = IDInfo & {
  /** Digitized fluid details. */
  fluid: {
    /** Remaining fluid amount in millibuckets (mB). */
    amount: number;
    /** Namespaced fluid identifier, such as `minecraft:water`. */
    name: string;
  };
};

/** Metadata and current contents associated with an energy identifier. */
export declare type EnergyIDInfo = IDInfo & {
  /** Digitized energy details. */
  energy: {
    /** Remaining energy amount, measured in the accompanying `unit`. */
    amount: number;
    /** Energy unit reported by the source storage, such as `FE`. */
    unit: string;
  };
};

/** Runtime limits and inherited API version exposed by the advanced digitizer. */
export declare interface AdvancedDigitizerConfiguration {
  /** Whether digital identifiers currently decay. Server default: `true`. */
  decayEnabled: boolean;
  /** Identifier lifetime after creation or refresh, in server game ticks. Server default: `120000`. */
  decayTicks: number;
  /** Maximum accumulated amount for one energy identifier. Server default: `Long.MAX_VALUE / 2`. */
  energyStackLimit: number;
  /** Maximum accumulated fluid amount for one identifier, in mB. Server default: `Long.MAX_VALUE / 2`. */
  fluidStackLimit: number;
  /** Maximum accumulated item count for one identifier. Server default: `16384`. */
  itemStackLimit: number;
  /** Major and minor version of the inherited inventory API. */
  inventoryAPIVersion: [number, number];
}

/**
 * The advanced item, fluid, and energy digitizer peripheral.
 *
 * @remarks
 * This peripheral also exposes the inherited `InventoryAPI` methods
 * (`size`, `list`, `getItemDetail`, `getItemLimit`, `pushItems`, and
 * `pullItems`) and `ConfigurationAPI.getConfiguration()`. Its internal
 * inventory can be addressed as `self` only for item operations.
 *
 * Digital identifiers are opaque 16-byte binary strings and are scoped to a
 * mode. Preserve them byte-for-byte and pass the same mode to `get`, `refresh`,
 * and `rematerialize`.
 *
 * @noSelf
 */
export interface AdvancedDigitizer
  extends IPeripheral,
    InventoryAPI,
    ConfigurationAPI<AdvancedDigitizerConfiguration> {
  /**
   * Digitizes items from the internal inventory or an attached item storage.
   *
   * @param mode - The `item` storage mode.
   * @param source - Peripheral name to extract from, or `self`. Defaults to `self`.
   * @param filter - One-based slot, item name, or item query table. Defaults to any item.
   * @param limit - Optional amount from 1 through half of `itemStackLimit`.
   * @param destination - Existing item identifier to merge into; omit to create a new identifier.
   * @returns On success, `[id, null]`; for an operational failure, `[null, message]`.
   * @throws A Lua error for an invalid mode, filter, limit, source, or incompatible storage.
   */
  digitize(
    mode: "item",
    source?: string | null,
    filter?: number | string | LuaTable | null,
    limit?: number | null,
    destination?: string | null
  ): LuaMultiReturn<[null, string] | [string, null]>;

  /**
   * Digitizes fluid or energy from an attached storage peripheral.
   *
   * @param mode - The `fluid` or `energy` storage mode.
   * @param source - Attached peripheral name. The default `self` is unsupported for these modes.
   * @param filter - Fluid name or energy unit; omit to accept the first available content.
   * @param limit - Optional mB or energy-unit amount from 1 through half of the mode's configured stack limit.
   * @param destination - Existing same-mode identifier to merge into; omit to create a new identifier.
   * @returns On success, `[id, null]`; for an operational failure, `[null, message]`.
   * @throws A Lua error for an invalid mode, limit, peripheral, storage type, or use of `self`.
   */
  digitize(
    mode: "fluid" | "energy",
    source?: string | null,
    filter?: string | null,
    limit?: number | null,
    destination?: string | null
  ): LuaMultiReturn<[null, string] | [string, null]>;
  /**
   * Rematerializes items, fluid, or energy into a destination storage.
   *
   * @param mode - Storage mode associated with `id`.
   * @param id - Opaque binary identifier.
   * @param limit - Optional amount from 1 through half of the mode's configured stack limit; defaults to all stored content.
   * @param destination - Peripheral name to insert into, or `self` for items. Defaults to `self`.
   * @returns On success, `[amount, null]`; for an operational failure, `[null, message]`.
   * @throws A Lua error for invalid arguments, inaccessible or incompatible storage, or fluid/energy use of `self`.
   */
  rematerialize(
    mode: "item" | "fluid" | "energy",
    id: string,
    limit?: number | null,
    destination?: string | null
  ): LuaMultiReturn<[null, string] | [number, null]>;
  /**
   * Refreshes an identifier's decay deadline.
   *
   * @param mode - Storage mode associated with `id`.
   * @param id - Opaque binary identifier.
   * @returns `true` when refreshed, or `false` when the identifier is missing or decayed.
   * @throws A Lua error if `mode` is unsupported.
   */
  refresh(mode: "item" | "fluid" | "energy", id: string): boolean;

  /**
   * Reads an item identifier.
   * @param mode - The `item` storage mode.
   * @param id - Opaque binary item identifier.
   * @returns Current metadata, or `null` when the identifier is missing, exhausted, or decayed.
   * @throws A Lua error if `mode` is unsupported.
   */
  get(mode: "item", id: string): ItemIDInfo | null;

  /**
   * Reads a fluid identifier.
   * @param mode - The `fluid` storage mode.
   * @param id - Opaque binary fluid identifier.
   * @returns Current metadata, or `null` when the identifier is missing, exhausted, or decayed.
   * @throws A Lua error if `mode` is unsupported.
   */
  get(mode: "fluid", id: string): FluidIDInfo | null;

  /**
   * Reads an energy identifier.
   * @param mode - The `energy` storage mode.
   * @param id - Opaque binary energy identifier.
   * @returns Current metadata, or `null` when the identifier is missing, exhausted, or decayed.
   * @throws A Lua error if `mode` is unsupported.
   */
  get(mode: "energy", id: string): EnergyIDInfo | null;
}

/**
 * Discovers attached peripherals whose type is `advanced_digitizer`.
 *
 * @example
 * `const digitizer = advancedDigitizerProvider.findOrThrow();`
 */
export const advancedDigitizerProvider =
  new IPeripheralProvider<AdvancedDigitizer>(
    "advanced_digitizer",
    () => null
  );
