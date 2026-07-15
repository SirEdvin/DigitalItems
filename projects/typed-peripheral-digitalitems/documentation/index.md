# Digital Items Typed Peripheral API

This package supplies TypeScriptToLua declarations and peripheral providers for
Digital Items 3. It covers the item-only `digitizer` and the multi-storage
`advanced_digitizer` peripherals.

## Choose a peripheral

- Use the basic digitizer for simple item workflows through its internal inventory.
- Use the advanced digitizer to address remote item, fluid, or energy storage, merge content into existing identifiers, and handle recoverable failures.
- Both peripherals inherit inventory methods. The advanced digitizer additionally reports decay and stack limits through `getConfiguration()`.

## Documentation

- [Getting started](getting-started.md)
- [Digital identifiers](digital-identifiers.md)
- [Basic digitizer guide](guides/basic-digitizer.md)
- [Advanced item guide](guides/advanced-items.md)
- [Fluid and energy guide](guides/fluids-and-energy.md)

The generated API reference documents every exported type, method overload, and
provider. Guide examples are shown first in TypeScriptToLua and then in direct
CraftOS Lua.
