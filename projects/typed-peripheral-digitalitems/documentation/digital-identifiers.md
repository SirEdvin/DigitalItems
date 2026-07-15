# Digital Identifiers

A digitize operation removes physical content and returns an opaque identifier
which refers to saved world data. An identifier is exactly 16 arbitrary bytes.
In TypeScript it has type `string`; in Lua it is a binary string. It is not
human-readable text or a UUID and may contain zero bytes or invalid UTF-8.

Keep identifiers byte-for-byte. Lua table values, binary files, or a deliberate
binary-to-text encoding are suitable. Do not trim them or send them through
JSON, text files, URLs, or databases without encoding and decoding all bytes.
An identifier belongs to one mode: `item`, `fluid`, or `energy`.

## Inspect and refresh

```ts
const info = digitizer.get("item", id);
if (info != null) {
  print(`${info.item.count} items, ${info.decaysAt - info.currentTime} ticks left`);
  digitizer.refresh("item", id);
}
```

```lua
local info = digitizer.get("item", id)
if info then
  print(('%d items, %d ticks left'):format(
    info.item.count, info.decaysAt - info.currentTime
  ))
  digitizer.refresh("item", id)
end
```

`digitizedAt`, `lastRefresh`, `decaysAt`, and `currentTime` are server game-time
ticks. Minecraft normally advances 20 ticks per second, but ticks are not wall
clock time and pause or lag can change the real duration. By default decay is
enabled for 120000 ticks, or five Minecraft days. Always read current server
configuration instead of relying on that default.

Creation sets `digitizedAt` and `lastRefresh` to the current tick and sets
`decaysAt` to current time plus `decayTicks`. Refresh updates `lastRefresh` and
`decaysAt`, not `digitizedAt`. If decay is disabled, the stored deadline remains
visible but is not enforced.

The advanced API returns `null` from `get` and `false` from `refresh` for a
missing or decayed identifier. The basic API throws instead. Fully
rematerializing an identifier removes it; partial rematerialization leaves the
remainder under the same identifier.
