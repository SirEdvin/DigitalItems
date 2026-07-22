---
peripheralInterface: Digitizer
---

# Basic Digitizer

<div class="di-peripheral-hero">
  <div class="di-peripheral-image"><img src="../../assets/peripherals/digitizer.png" alt="Digitizer block" /></div>
  <div><span class="di-eyebrow">ITEM PERIPHERAL</span><p>The basic digitizer turns physical item stacks into portable digital identifiers and restores them through its internal inventory.</p><dl><div><dt>Peripheral type</dt><dd><code>{{ peripheralType }}</code></dd></div><div><dt>Storage</dt><dd>Items</dd></div><div><dt>Failure style</dt><dd>Lua errors</dd></div></dl></div>
</div>

Slots are one-based and default to slot 1. `digitize()` removes the complete
stack; `digitizeAmount()` requires an exact positive amount no greater than the
physical stack.

> **Inherited inventory API:** `size`, `list`, `getItemDetail`, `getItemLimit`,
> `pushItems`, and `pullItems` are also available on this peripheral.

## Round trip a partial stack

```ts
import { digitizerProvider } from "@siredvin/typed-peripheral-digitalitems/digitizer";

const digitizer = digitizerProvider.findOrThrow();
const id = digitizer.digitizeAmount(16, 1);
const info = digitizer.getIDInfo(id);
print(`${info.item.count} ${info.item.name}`);

const inserted = digitizer.rematerializeAmount(id, 8);
print(`Inserted ${inserted}`);
```

```lua
local digitizer = peripheral.find("digitizer")
assert(digitizer, "digitizer not attached")
local id = digitizer.digitizeAmount(16, 1)
local info = digitizer.getIDInfo(id)
print(('%d %s'):format(info.item.count, info.item.name))

local inserted = digitizer.rematerializeAmount(id, 8)
print("Inserted " .. inserted)
```

## Errors and remaining items

All failures throw. Empty slots, non-positive amounts, requesting more than the
physical or digital count, and unknown or decayed identifiers are errors.

`rematerialize()` requests the entire digital count, while
`rematerializeAmount()` requests a chosen count. Both insert only what the
internal inventory can accept and return the actual inserted item count. If
space is limited, the uninserted remainder stays digital under the same ID.

Use the inherited inventory API to inspect and move physical stacks. For
example, `list()` reports occupied slots and `pullItems()` can fill the
digitizer from another attached inventory before digitization.
