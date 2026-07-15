# Basic Digitizer

The basic digitizer handles items in its own inventory. Slots are one-based and
default to slot 1. `digitize()` removes the complete stack; `digitizeAmount()`
requires an exact positive amount no greater than the physical stack.

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

All failures throw. Empty slots, non-positive amounts, requesting more than the
physical or digital count, and unknown or decayed identifiers are errors.

`rematerialize()` requests the entire digital count, while
`rematerializeAmount()` requests a chosen count. Both insert only what the
internal inventory can accept and return the actual inserted item count. If
space is limited, the uninserted remainder stays digital under the same ID.

Use the inherited inventory API to inspect and move physical stacks. For
example, `list()` reports occupied slots and `pullItems()` can fill the
digitizer from another attached inventory before digitization.
