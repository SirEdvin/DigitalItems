import { ConfigurationAPI } from "@siredvin/typed-peripheral-api/configuration";
import { InventoryAPI } from "@siredvin/typed-peripheral-api/inventory";
import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";
import { ItemIDInfo } from "./shared";

/** Configuration fields exposed by the basic digitizer. */
export declare interface DigitizerConfiguration {
  /** Major and minor version of the inherited inventory API. */
  inventoryAPIVersion: [number, number];
}

/**
 * The basic item-only digitizer peripheral.
 *
 * @remarks
 * This peripheral also exposes the inherited `InventoryAPI` methods
 * (`size`, `list`, `getItemDetail`, `getItemLimit`, `pushItems`, and
 * `pullItems`) and `ConfigurationAPI.getConfiguration()`.
 *
 * Digital identifiers are opaque 16-byte binary strings. Preserve them
 * byte-for-byte; they are not text, UUIDs, or safe to pass through a text-only
 * encoding without an explicit binary encoding.
 *
 * Unlike the advanced digitizer, failures from this API throw Lua errors.
 *
 * @noSelf
 */
export interface Digitizer
  extends IPeripheral,
    InventoryAPI,
    ConfigurationAPI<DigitizerConfiguration> {
  /** @returns Whether identifier decay is currently enabled by server configuration. */
  getDecayEnabled(): boolean;

  /** @returns The configured lifetime after creation or refresh, in server ticks. */
  getDecayTicks(): number;

  /**
   * Removes the complete stack from an internal inventory slot and digitizes it.
   *
   * @param slot - One-based inventory slot. Defaults to slot 1.
   * @returns A new opaque 16-byte binary identifier.
   * @throws A Lua error if the slot is empty or the slot is invalid.
   */
  digitize(slot?: number): string;

  /**
   * Rematerializes as much of an item identifier as the internal inventory can accept.
   *
   * @param id - Opaque binary identifier returned by a digitize method.
   * @returns The number of items inserted. The identifier remains valid if items remain.
   * @throws A Lua error if the identifier is unknown or has decayed.
   */
  rematerialize(id: string): number;

  /**
   * Removes and digitizes an exact number of items from an internal slot.
   *
   * @param amount - Positive number of items to remove.
   * @param slot - One-based inventory slot. Defaults to slot 1.
   * @returns A new opaque 16-byte binary identifier.
   * @throws A Lua error if `amount` is not positive, the slot is empty, or fewer items are present.
   */
  digitizeAmount(amount: number, slot?: number): string;

  /**
   * Attempts to insert an exact requested portion into the internal inventory.
   *
   * @param id - Opaque binary item identifier.
   * @param amount - Positive number of items requested; it cannot exceed the stored count.
   * @returns The number actually inserted, which may be lower when inventory space is limited.
   * @throws A Lua error for an invalid or decayed identifier, invalid amount, or insufficient stored count.
   */
  rematerializeAmount(id: string, amount: number): number;

  /**
   * Resets an identifier's decay deadline to current game time plus `getDecayTicks()`.
   *
   * @param id - Opaque binary item identifier.
   * @throws A Lua error if the identifier is unknown or has decayed.
   */
  refresh(id: string): void;

  /**
   * Reads the current item and timing metadata for an identifier.
   *
   * @param id - Opaque binary item identifier.
   * @returns Identifier timing metadata and full item details.
   * @throws A Lua error if the identifier is unknown or has decayed.
   */
  getIDInfo(id: string): ItemIDInfo;
}

/**
 * Discovers attached peripherals whose type is `digitizer`.
 *
 * @example
 * `const digitizer = digitizerProvider.findOrThrow();`
 */
export const digitizerProvider = new IPeripheralProvider<Digitizer>(
  "digitizer",
  () => null
);
