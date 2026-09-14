# Maintaining the documentation

The site combines handwritten `documentation/*.md` guides with API references
extracted from the TypeScript interfaces. Install `requirements-docs.txt` in a
Python virtual environment and keep that environment active when running:

```sh
mkdir -p build
LOG="build/docs-$(date +%Y%m%d-%H%M%S).log"
timeout --foreground 10m ./gradlew :typed-peripheral-digitalitems:generateDocs --no-daemon >"$LOG" 2>&1
```

Run commands from the repository root. The generated site is in
`projects/typed-peripheral-digitalitems/docs/`.

## Native Minecraft model images

The documentation logo and favicon use the official Digital Items 3
[Modrinth project icon](https://cdn.modrinth.com/data/YDOa7yWU/5a43451bbf835a77b527ca0e2edde739f4488cc0.png),
stored locally as `documentation/theme/favicon.png`. The repository's `pack.png`
is a blank placeholder and is intentionally not used for the site.

The PNGs in `documentation/assets/peripherals/` are **Minecraft-rendered inventory
models**, not reconstructed cubes. They use each registered block item's baked
model, inherited GUI display transforms, textures, and native item lighting.
Both current item models select the powered-on block model, matching inventory.

To refresh the images after changing models, textures, or Minecraft versions:

```sh
mkdir -p build
LOG="build/docs-models-$(date +%Y%m%d-%H%M%S).log"
timeout --foreground 10m xvfb-run -a ./gradlew :fabric:exportDocsModels --no-daemon >"$LOG" 2>&1
```

This launches an isolated Fabric client in `projects/fabric/run/docs-render`,
waits for resource loading, exports both models at 128×128, and exits without
creating a world. The run rejects missing models, blank renders, and missing
outputs. `exportDocsModels` copies the PNGs from `projects/fabric/build/docs-models`
into the documentation's source assets. Inspect and commit both refreshed images
with the source changes, then regenerate the site. Never hand-edit the renders.
On a graphical workstation, `xvfb-run -a` can be omitted.

The exporter lives only in Fabric's `docsRender` source set. It is not packaged
in the production mod or loaded by normal clients, servers, or GameTests.
Normal documentation builds use the checked-in PNGs, so Pages does not need to
launch Minecraft/OpenGL for each branch and historical release.

The requested [Item Image Export](https://github.com/SchachSebastian/item-image-export)
targets NeoForge 1.21.1 rather than this branch's Fabric/Forge 1.20.1. Our tiny
client exporter instead calls vanilla `GuiGraphics.renderItem`; its screenshot
chroma-key technique follows the approach used by
[Icon Exporter](https://github.com/CyclopsMC/IconExporter). It does not reimplement
model geometry or introduce a runtime dependency.

## Checks

```sh
.gradle/nodejs/node-v22.14.0-linux-x64/bin/node --test \
  .github/scripts/docs-site.test.mjs \
  projects/typed-peripheral-digitalitems/documentation/generate-docs.test.mjs
```

After assembling a versioned site, run `.github/scripts/docs-site.mjs validate`
with `--site` pointing at the assembled directory. Check the overview and both
peripheral guides in a browser at desktop and phone widths; the guide cards sit
to the right on wide screens and stack on narrow screens.
