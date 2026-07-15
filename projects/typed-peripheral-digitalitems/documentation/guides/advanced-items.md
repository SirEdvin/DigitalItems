# Advanced Items

The advanced digitizer can extract from its internal inventory with source
`self`, or from an attached inventory/item storage by peripheral name. The
source defaults to `self`.

Item filters may be:

- A one-based slot number. This requires slotted storage.
- A namespaced item string such as `minecraft:oak_log`.
- A query table using fields such as `name`, `displayName`, `tag`, `nbt`, `and`, `or`, `not`, or `none`.
- `nil`/`null`, which accepts any item.

## Digitize and accumulate

```ts
const chest = peripheral.find("minecraft:chest");
assert(chest != null, "chest not attached");
const chestName = peripheral.getName(chest);

const [id, createError] = digitizer.digitize(
  "item", chestName, "minecraft:oak_log", 32
);
if (id == null) error(createError, 0);

const [sameID, mergeError] = digitizer.digitize(
  "item", chestName, { tag: "minecraft:logs" }, 32, id
);
if (sameID == null) error(mergeError, 0);
```

```lua
local chest = peripheral.find("minecraft:chest")
assert(chest, "chest not attached")
local chestName = peripheral.getName(chest)

local id, createErr = digitizer.digitize(
  "item", chestName, "minecraft:oak_log", 32
)
if not id then error(createErr, 0) end

local sameID, mergeErr = digitizer.digitize(
  "item", chestName, { tag = "minecraft:logs" }, 32, id
)
if not sameID then error(mergeErr, 0) end
```

The optional `destination` is an existing binary digital ID, not a peripheral
name. A merge succeeds only when the extracted stack can stack with the stored
item. It refreshes the destination and returns the same ID.

An explicit `limit` must be from 1 through `itemStackLimit / 2`. When merging
without a limit, that half-limit is the default per call, further reduced by
remaining room. Accumulation cannot exceed `itemStackLimit`. A new ID created
without a limit asks the source for all matching available content; do not
assume the configured accumulation limit is an implicit cap for that call.

## Rematerialize

```ts
const [moved, moveError] = digitizer.rematerialize("item", id, 64, chestName);
if (moved == null) error(moveError, 0);
print(`Moved ${moved}`);
```

```lua
local moved, moveErr = digitizer.rematerialize("item", id, 64, chestName)
if not moved then error(moveErr, 0) end
print("Moved " .. moved)
```

Omitting `destination` rematerializes items into `self`. Omitting `limit`
requests all stored content. Success reports the amount actually inserted.
Destinations that accept no content return `nil, error` without consuming the
ID; a peripheral with the wrong storage API throws instead.
