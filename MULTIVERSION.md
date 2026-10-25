# Multiversion build

`multiversion` maintains Minecraft **1.20.1 Fabric/Forge** (Java 17) and
**1.21.1 Fabric/NeoForge** (Java 21) in one history. Run Gradle with Java 21;
install both JDKs. `gradle.properties:maintainedMinecraftVersions` is the build
and documentation discovery inventory. There is no 26.x node in this migration.

## Source ownership

Stonecutter 0.9.8 uses one controller for each existing `core`, `fabric`, and
`forge` module. The canonical/VCS state is **1.20.1**. Edit shared `src/` files
for common behavior; edit `versions/1.21.1/src/` only when its native API differs.
Version-only metadata/data lives under the matching `versions/<minecraft>/src/`.
Do not edit `build/generated/stonecutter/` or copy it into source control.

The source snapshots were taken from these history-preserving ancestors:

- 1.20: `0a17149f42a3c25ac5a81817ccd1f06f75ce9437`
- 1.21: `137cf92427bd866a392229587a1c473e43a7486c` (contains the 1.20 head)
- Build reference: Minecraft-Modding-Libs `84e09bb590a5b41918a75382f70565ffbc49ad70`

| Surface | Ownership / reason for difference |
| --- | --- |
| Peripheral names, methods, results, errors, transfer/decay policy, IDs | Shared unchanged 1.20 implementation unless an original 1.21 native call needs an override. No Lua/TypeScript contract changes. |
| Digital items and block-entity persistence | Original 1.20 NBT serialization versus original 1.21 registry-aware item/component serialization. Counts, owners, timestamps, and save keys retain each branch's format; no cross-version world conversion. |
| Criteria, advancement providers, upgrades | Original 1.20 criteria/serializers versus 1.21 codecs/registry criteria and upgrade serializer APIs. Original registration IDs retained. |
| Blocks, screen/client hooks, networking | Native signatures changed; full-file 1.21 overrides preserve the branch implementations instead of translating back into 1.20. |
| Config | Same keys/defaults and validation; ForgeConfigSpec versus ModConfigSpec API/package. |
| Loader registration and test bootstrap | Shared loader boundaries; native Forge/NeoForge lifecycle/capabilities differ. Fabric 1.21 retains its original platform adapter. Testiarium fixture initializers are ordered after the consumer's one-shot registration. A test-only 1.20 scheduler barrier mirrors the reference 1.21 harness against pinned CC 1.113 internals, preventing virtual timers from outrunning asynchronous computer work; the existing 1.21 barrier configuration is shared by both loaders. |
| Resources/datagen | Unchanged textures/models shared. Version-only generated data keeps plural 1.20 / singular 1.21 directories and each original recipe/advancement format. Regenerate via the matching native data task, never edit processed output. |
| Access files, metadata, pack format | Matching native version overrides; Forge `mods.toml` and mixin manifest are not shipped in NeoForge artifacts, or vice versa. |
| TypeScript package and Lua fixtures | Two ordinary version-independent Gradle projects. Local npm dependency remains compiled before fixture installation. |

The baseline's canonical production source adjustment matches the two
`getRenderShape` parameter names to their inherited declaration, for strict
compilation; it does not alter behavior. Build tools change because Stonecutter
requires Gradle 9: wrapper 9.5.0, Loom 1.17.11, and ModDevGradle LegacyForge 2.0.142
replace the incompatible ForgeGradle invocation. Kotlin uses the existing buildenv
compiler 2.2.20 with Kotlin 2.0 language/API metadata.

## Library RC qualification

Both lines consume Broccolium, Tweakium and test-only Testiarium **2.0.0-rc.1**
from the existing public Maven repository. Forge-family jar-in-jar uses an exact
RC constraint; Fabric includes the matching RC artifacts. No Peripheralium or
independent npm version change is involved. CC:Tweaked **1.113.1 / 1.120.0**,
Fabric Loader **0.18.4**, and Fabric API **0.92.3+1.20.1 / 0.116.14+1.21.1**
match the published RC dependency graphs.

Testiarium's `testMod` and `cctTestMod` artifacts are selected by their published
feature capabilities. Fabric retains detached fixture-file configurations because
Loom overlaps remap outputs when several variants of one component are added
directly. NeoForge excludes the fixture dependency's transitive KotlinForForge
POM: the existing production all-JAR already provides it, and that POM otherwise
adds another FML loader. Neither adjustment removes a fixture or changes production
peripheral behavior. The pocket conservation test uses CC's public registry and
stack-identity predicate instead of the now-private 1.21 server-computer lookup.

## Commands

Always use `--no-daemon`, a deadline, and complete redirected logs. Root `build`
builds all six common/loader leaves (four playable loader nodes); root `gameTest`
runs the four nodes and validates fresh XML with the complete case inventory.

```sh
mkdir -p build
timeout --foreground 5m ./gradlew :typescript-tests:compileTestLua --no-daemon >build/typescript-tests.log 2>&1
timeout --foreground 10m ./gradlew build --no-daemon >build/gradle-build.log 2>&1
timeout --foreground 20m xvfb-run -a ./gradlew gameTest --no-daemon -PminimalTestEnvironment >build/gametest.log 2>&1
```

Individual tasks (replace `1.20.1` with `1.21.1` for the newer line):

- `:fabric:1.20.1:build`, `:forge:1.20.1:build`
- `:fabric:1.20.1:runDigitalItemsGameTest`, `:forge:1.20.1:runGameTestServer`
- `:fabric:1.20.1:runData`, `:forge:1.20.1:runData`
- `:fabric:1.20.1:exportDocsModels` (opt-in native model rendering)

`-PminimalTestEnvironment` omits direct optional development-viewer and ModMenu
declarations, not tests or required mod integrations; transitive development mods
from the existing library artifacts may remain. Test runs delete their exact old
report first. XML/HTML is under
`projects/<loader>/versions/<minecraft>/build/test-results/digitalitems-gametest.*`.
The nine original Lua cases remain; the persistence case reloads a copied disposable
same-version `digitalitems.dat` through native DimensionDataStorage, covering
oversized named/damaged items, fluid/energy identity and quantity, owners and decay
timestamps, including complete item and fluid NBT/components. Three additional
cases check occupied, partial, full and empty rematerialization destinations for
block, turtle and pocket owners (13 cases per node).

The persistence fixture also accepts optional **game JVM** properties
`digitalitems.persistenceOutput` and `digitalitems.persistenceInput`. Output
preserves a copied disposable `digitalitems.dat` and complete expected entry SNBT;
input loads a same-version copy through native `DimensionDataStorage` and compares
all three entries. Use a distinct directory per node, stop the writing process,
copy the snapshot, then start a separate reading process. This supports both
RC-to-RC restart and old-library-to-RC compatibility checks without changing save
formats or touching user worlds. It is not a real socket/client test or a
cross-Minecraft-version upgrade test.

Switch all controllers together, then return to canonical state before committing:

```sh
./gradlew ':core:Set active project to 1.21.1' ':fabric:Set active project to 1.21.1' ':forge:Set active project to 1.21.1' --no-daemon
./gradlew ':core:Reset active project' ':fabric:Reset active project' ':forge:Reset active project' --no-daemon
```

Run the versioned build after switching; compare source hashes before/after the
round trip. A switch is not permission to edit an override's processed copy.

## Artifacts, CI, documentation

Binary names remain `digitalitems-<common|fabric|forge>-<minecraft>-<modVersion>.jar`;
the `forge` part denotes the existing Forge-family module (NeoForge on 1.21.1).
Internal Gradle groups distinguish module identities; public Maven group remains
`site.siredvin`. The aggregate GitHub release configuration targets `multiversion`,
uses `v<modVersion>`, and selects the four playable binaries, not controller JARs.
No release or npm publication is part of this migration.

CI checks the literal feature head on PR and feature-branch push: full build,
four native GameTest jobs, and documentation tooling/build checks. Branch rules
remain unchanged. Review/merge and post-merge verification belong to the stage gate.

Documentation discovery reads both maintained versions from one multiversion
commit, preserving `branch/1.20/`, `branch/1.21/`, historical `tag/<tag>/` URLs,
the 1.20.1 default and selector behavior. Source links use the exact commit SHA.
The typed API/generator stays version-independent. To preview, discover with
`--source-ref HEAD`, build the two branch fragments with the existing
`:typed-peripheral-digitalitems:generateDocs` task and its `docsOutput`,
`docsGitRevision`, `docsHostedBaseUrl` properties, then `docs-site.mjs assemble`
and `validate`. Pages deployment is a separate workflow; a local preview or PR
check is not evidence of a hosted deployment.
