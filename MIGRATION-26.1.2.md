# Additive Minecraft 26.1.2 implementation brief

## Baseline and compatibility

Start: qualified `multiversion` merge `48ca204bd18afce4e05820fddc2b5585ad49f321`.
Retain 1.20.1 Fabric/Forge (Java 17) and 1.21.1 Fabric/NeoForge (Java 21).
Add exact **26.1.2 Fabric/NeoForge**, universal client/dedicated server.
**26.1.2 supports new worlds only. Cross-Minecraft-version world upgrades and downgrades are unsupported and untested.** Only disposable same-version saves may be used for persistence qualification.

Preserve `digitalitems` registry, block, criterion, stat, upgrade and peripheral IDs,
all Lua/TypeScript signatures/results and config keys/defaults/limits. Keep the
TypeScript API and Lua fixture projects version-independent. No mod/npm release,
merge, default-branch change or repository-policy change is authorized here.

## Toolchain and dependencies

The existing wrapper is Gradle 9.5.0. Target reference is the public libraries
`2.0.0-rc.1` source tag (commit `79fb93aa91d3d59bb5389bc542b6ed8c8ddd112b`),
`gradle/minecraft-26-1.gradle.kts`. Match its Java 25, Kotlin 2.4.0,
non-remapping `net.fabricmc.fabric-loom` 1.17.11 and ModDevGradle 2.0.142
contracts. Legacy Kotlin language/API metadata remains 2.0 and legacy remapping
and JVM targets remain unchanged. Official unobfuscated target names; no mappings
dependency or remapJar on target. Existing sources already use Mojang mappings,
so there is no Yarn conversion phase.

Target pins: NeoForge 26.1.2.122 (also exact target MDK); Fabric Loader 0.19.5,
Fabric API 0.155.3+26.1.2, Fabric Language Kotlin 1.13.12+kotlin.2.4.0,
KotlinForForge 6.4.0, CC:Tweaked 1.120.0, Broccolium/Tweakium/Testiarium
2.0.0-rc.1, Team Reborn Energy 5.0.0 through the library adapter.
Forge Config API Port 26.1.5 Fabric is published on Modrinth (`jUe0ucoE`);
its actual JAR contains NeoForge ModConfigSpec and the v5 ConfigRegistry API.
Use native NeoForge config on NeoForge. Required dependencies must resolve before
feature porting; no local producer substitution or invented compatibility.

## Source ownership and feature inventory

Canonical/VCS remains 1.20.1 across core/fabric/forge controllers. Target-only build
script and authored `versions/26.1.2/src` overrides follow the library's committed
strategy. Never edit processed Stonecutter outputs. Keep common behavior in core;
loader lifecycle/capabilities/metadata remain in their adapters.

| Surface | Port boundary |
| --- | --- |
| Build/publication | Add three target leaves and two playable JARs; ordinary target jar and official access namespace; preserve artifact naming and legacy toolchains. |
| Gameplay/public API | Normal/advanced digitizers, server-owned digital IDs and item/fluid/energy operations, block/turtle/pocket owners, existing validation/errors and conservation. |
| Persistence/data | Server-global codec-backed SavedData, stable IDs/counts/owners/decay fields; registry-aware item components; ValueInput/ValueOutput for block entities; no cross-version file mover. |
| Networking/menus | Trace native menu payload registration and validation through target platform toolkit; no new public wire protocol. Validate positions/type/distance before server mutations. |
| Client/rendering | Digitizer menus/screens, CC turtle models and item definitions; extraction APIs and gated client entrypoints. No direct raw OpenGL renderer in production. |
| Registration/config | Registry keys supplied before item/block construction; criteria codecs; config semantics unchanged. |
| Resources/datagen | Port actual providers, regenerate recipe/loot/advancement/tag/model/translation/upgrade JSON per loader; review deterministic second generation. |
| Tests | Port real Testiarium registry/annotations/structures and CCT harness; retain all nine Lua fixtures and four persistence/conservation cases; expand invalid-input/runtime coverage as needed. |
| Documentation/CI | Six playable nodes, literal head CI; retain historical routes and 1.20 default. Target docs use native target outputs. |

## Cumulative boundary review

Authoritative vanilla primers retrieved for every boundary, not only the last delta:
[1.21.2](https://github.com/neoforged/.github/blob/main/primers/1.21.2/index.md)
(registry keys/interaction signatures),
[1.21.4](https://github.com/neoforged/.github/blob/main/primers/1.21.4/index.md)
(item definitions and datagen split),
[1.21.5](https://github.com/neoforged/.github/blob/main/primers/1.21.5/index.md)
(components, models/render pipelines),
[1.21.6](https://github.com/neoforged/.github/blob/main/primers/1.21.6/index.md)
(ValueInput/Output and rendering),
[1.21.7](https://github.com/neoforged/.github/blob/main/primers/1.21.7/index.md),
[1.21.8](https://github.com/neoforged/.github/blob/main/primers/1.21.8/index.md),
[1.21.9](https://github.com/neoforged/.github/blob/main/primers/1.21.9/index.md)
(transactional transfer, render extraction),
[1.21.10](https://github.com/neoforged/.github/blob/main/primers/1.21.10/index.md),
[1.21.11](https://github.com/neoforged/.github/blob/main/primers/1.21.11/index.md)
(Identifier, render/material changes), and
[26.1](https://github.com/neoforged/.github/blob/main/primers/26.1/index.md)
(Java25/deobfuscation, templates, SavedData ownership and GUI extraction).
Patch-only 1.21.7/8/10 primers do not replace adjacent substantive boundaries.

Fabric maintained source pins are 0.92.3+1.20.1 / 0.116.14+1.21.1,
Loader0.18.4, Loom1.17.11; target uses the above RC pins. Inspect exact resolved
API sources for renamed level lifecycle/network/datagen/model APIs, not vanilla
primers alone. Official [Fabric porting](https://docs.fabricmc.net/26.1.2/develop/porting/)
and [API renames](https://docs.fabricmc.net/26.1.2/develop/porting/fabric-api)
and [target MDK](https://github.com/NeoForgeMDKs/MDK-26.1.2-ModDevGradle)
are reference contracts. Downloaded research and actual dependency inspection are
under ignored `build/migration`; source/bytecode wins over examples.

## Acceptance and current status

No target verification has passed at brief creation. Completion requires:

- resolve public dependencies, bootstrap target build, compile production/testmod;
- complete native target GameTests on both loaders with fresh nonempty XML and
  exact declared/case identity counts; TypeScript compilation/contract tests;
- datagen and deterministic resources, packaged JAR/nested/access/metadata audit;
- dedicated startup to ready/clean stop, connected client join, blocks/menus and
  turtle/pocket item/fluid/energy functional smoke;
- disposable same-version save/restart and invalid-network-input checks;
- six-node build/GameTest matrix and canonical switch-back source equality;
- scoped commit/push/new PR into multiversion, exact-head hosted CI success,
  durable attached command/log/report/hash receipts. Stage gate reviews/merges.

Known inherited limitation: Pages environment excludes multiversion; generation
and assembly are required, but do not change policy or claim live deployment.
No integration is approved for silent removal or temporary no-op substitution.
