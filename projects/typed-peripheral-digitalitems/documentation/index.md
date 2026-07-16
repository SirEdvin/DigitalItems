# Digital Items Peripheral Manual

Player-focused documentation and typed APIs for the Digital Items 3 peripherals.
Use these pages whether you write programs directly in CraftOS Lua or compile
TypeScript with TypeScriptToLua.

## Choose your digitizer

<div class="di-peripheral-grid">
  <a class="di-peripheral-card" href="guides/basic-digitizer.md">
    <span class="di-peripheral-image"><img src="../../core/src/main/resources/assets/digitalitems/textures/block/digitizer_front_on.png" alt="Digitizer block" /></span>
    <span class="di-peripheral-copy"><strong>Digitizer</strong><small>Peripheral type: <code>digitizer</code></small><span>Digitize and restore items through a simple internal inventory.</span><b>Open peripheral guide &rarr;</b></span>
  </a>
  <a class="di-peripheral-card di-peripheral-card--advanced" href="guides/advanced-items.md">
    <span class="di-peripheral-image"><img src="../../core/src/main/resources/assets/digitalitems/textures/block/advanced_digitizer_front_on.png" alt="Advanced digitizer block" /></span>
    <span class="di-peripheral-copy"><strong>Advanced Digitizer</strong><small>Peripheral type: <code>advanced_digitizer</code></small><span>Move items, fluids, and energy between remote storage and digital IDs.</span><b>Open peripheral guide &rarr;</b></span>
  </a>
</div>

Both peripherals include the standard CC:Tweaked inventory methods. The advanced
digitizer also exposes configured decay and storage limits through
`getConfiguration()`.

## Start here

- [Getting started](getting-started.md)
- [Digital identifiers](digital-identifiers.md)
- [Basic digitizer guide](guides/basic-digitizer.md)
- [Advanced item guide](guides/advanced-items.md)
- [Fluid and energy guide](guides/fluids-and-energy.md)

The **Guides** explain behavior in player terms and include both Lua and
TypeScript examples. The **API reference** is the precise source for signatures,
overloads, return types, and provider declarations.
