# Fluids and Energy

Fluid and energy operations require an attached compatible storage peripheral.
The advanced digitizer has no internal fluid tank or energy buffer, so `self`
is invalid as either source or destination. Always pass both peripheral names;
the API defaults are useful only for item mode.

Fluid amounts and limits are millibuckets (mB); 1000 mB is one bucket. Energy
amounts use the unit reported by the storage and returned in `energy.unit`, for
example `FE`. Digital Items preserves that unit and only merges compatible
energy stacks.

## Fluids

```ts
const [fluidID, fluidError] = digitizer.digitize(
  "fluid", tankName, "minecraft:lava", 4000
);
if (fluidID == null) error(fluidError, 0);

const fluid = digitizer.get("fluid", fluidID);
if (fluid != null) print(`${fluid.fluid.amount} mB ${fluid.fluid.name}`);

const [drained, drainError] = digitizer.rematerialize(
  "fluid", fluidID, null, tankName
);
if (drained == null) error(drainError, 0);
```

```lua
local fluidID, fluidErr = digitizer.digitize(
  "fluid", tankName, "minecraft:lava", 4000
)
if not fluidID then error(fluidErr, 0) end

local fluid = digitizer.get("fluid", fluidID)
if fluid then print(fluid.fluid.amount .. " mB " .. fluid.fluid.name) end

local drained, drainErr = digitizer.rematerialize(
  "fluid", fluidID, nil, tankName
)
if not drained then error(drainErr, 0) end
```

The optional fluid filter is a namespaced fluid ID. Omit it to select the first
available fluid.

## Energy

```ts
const unit = energyStorage.getEnergyUnit();
const [energyID, energyError] = digitizer.digitize(
  "energy", energyName, unit, 2000
);
if (energyID == null) error(energyError, 0);

const [restored, restoreError] = digitizer.rematerialize(
  "energy", energyID, null, energyName
);
if (restored == null) error(restoreError, 0);
```

```lua
local unit = energyStorage.getEnergyUnit()
local energyID, energyErr = digitizer.digitize(
  "energy", energyName, unit, 2000
)
if not energyID then error(energyErr, 0) end

local restored, restoreErr = digitizer.rematerialize(
  "energy", energyID, nil, energyName
)
if not restored then error(restoreErr, 0) end
```

The optional energy filter is an exact unit name. Omit it to accept the first
available energy stack.

An explicit fluid or energy `limit` must be from 1 through half of that mode's
configured stack limit. Existing IDs cannot grow beyond the full configured
limit. As with items, a new ID created without an explicit limit is not
implicitly capped by the accumulation limit. Operational failures return
`nil, error`; invalid ranges, missing peripherals, wrong storage types, and
`self` usage throw.
