# Getting Started

Install the package in a TypeScriptToLua computer project, import the provider
for the attached peripheral, and call `findOrThrow()`. Providers use the
peripheral types `digitizer` and `advanced_digitizer`.

## Find an advanced digitizer

```ts
import { advancedDigitizerProvider } from "@siredvin/typed-peripheral-digitalitems/advanced_digitizer";

const digitizer = advancedDigitizerProvider.findOrThrow();
const config = digitizer.getConfiguration();
print(`Decay: ${config.decayEnabled}, lifetime: ${config.decayTicks} ticks`);
```

```lua
local digitizer = peripheral.find("advanced_digitizer")
assert(digitizer, "advanced digitizer not attached")
local config = digitizer.getConfiguration()
print(("Decay: %s, lifetime: %d ticks"):format(
  tostring(config.decayEnabled), config.decayTicks
))
```

## Inherited APIs

Both peripherals include the inventory view and transfer methods `size`,
`list`, `getItemDetail`, `getItemLimit`, `pushItems`, and `pullItems`. They also
include `getConfiguration()`. Inventory slots are one-based, as in CC:Tweaked.

The basic digitizer configuration reports `inventoryAPIVersion`. The advanced
configuration additionally reports `decayEnabled`, `decayTicks`, and the item,
fluid, and energy stack limits. Treat configuration as runtime data: server
owners can change defaults.

The shipped server defaults are decay enabled, a 120000-tick lifetime, an item
stack limit of 16384, and fluid and energy limits of `Long.MAX_VALUE / 2`
(4611686018427387903). Very large Java `long` values cannot be represented
exactly by every Lua number runtime, so use the returned configuration value for
comparisons rather than copying the literal into a program.

## Error model

Basic digitizer methods throw Lua errors on failure. Advanced `digitize` and
`rematerialize` return a value/error pair for expected operational failures:

```ts
const [id, error] = digitizer.digitize("item", "self", 1, 16);
if (id == null) error(`Digitization failed: ${error}`, 0);
```

```lua
local id, err = digitizer.digitize("item", "self", 1, 16)
if id == nil then error("Digitization failed: " .. err, 0) end
```

Invalid modes, malformed item query or slot filters, out-of-range limits,
missing peripherals, and incompatible storage still throw. A fluid or energy
filter that does not match returns the normal operational error pair. Use
`pcall` when thrown conditions are not under your program's control.
