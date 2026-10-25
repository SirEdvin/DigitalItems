import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.fabric")
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
}

val modVersion: String by extra
val minecraftVersion: String by extra
val modBaseName: String by extra
val is121 = sc.current.parsed >= "1.21"
val commonProject = project(":core:$minecraftVersion")

baseShaking {
    projectPart.set("fabric")
    integrationRepositories.set(true)
    shake()
}

fabricShaking {
    commonProjectName.set("core:$minecraftVersion")
    accessWidener.set(rootProject.file("projects/core/src/main/resources/digitalitems.accesswidener"))
    extraRawVersionMappings.set(
        mapOf(
            "computercraft" to libs.versions.cc.tweaked.get(),
            "forgeconfigapiport" to if (is121) libs121.versions.forgeconfigapirt.get() else libs.versions.forgeconfigapirt.get(),
            "tweakium" to if (is121) libs121.versions.tweakium.asProvider().get() else libs.versions.tweakium.asProvider().get(),
            "broccolium" to if (is121) libs121.versions.broccolium.asProvider().get() else libs.versions.broccolium.asProvider().get(),
        ),
    )
    stablePlayer.convention(true)
    shake()
}

val testMod = sourceSets.create("testMod") {
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    compileClasspath += commonProject.sourceSets["testMod"].output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += commonProject.sourceSets["testMod"].output
}

net.fabricmc.loom.configuration.RemapConfigurations.setupForSourceSet(project, testMod)

val docsRender = sourceSets.create("docsRender") {
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
}

net.fabricmc.loom.configuration.RemapConfigurations.setupForSourceSet(project, docsRender)

val testiariumCctArtifacts = configurations.detachedConfiguration(
    project.dependencies.create("site.siredvin:testiarium-core-$minecraftVersion:0.1.1:cct-test-mod@jar"),
    project.dependencies.create("site.siredvin:testiarium-fabric-$minecraftVersion:0.1.1:cct-test-mod@jar"),
).apply {
    isTransitive = false
}

val testiariumTestArtifacts = configurations.detachedConfiguration(
    project.dependencies.create("site.siredvin:testiarium-core-$minecraftVersion:0.1.1:test-mod@jar"),
    project.dependencies.create("site.siredvin:testiarium-fabric-$minecraftVersion:0.1.1:test-mod@jar"),
).apply {
    isTransitive = false
}

val testiariumMainArtifacts = configurations.detachedConfiguration(
    project.dependencies.create("site.siredvin:testiarium-core-$minecraftVersion:0.1.1"),
    project.dependencies.create("site.siredvin:testiarium-fabric-$minecraftVersion:0.1.1"),
).apply {
    isTransitive = false
}

loom {
    mods {
        register("digitalitems-docs-render") {
            sourceSet(docsRender)
        }
        register("digitalitems-testmod") {
            sourceSet(testMod)
            sourceSet(commonProject.sourceSets["testMod"])
        }
    }
    runs {
        create("docsRender") {
            client()
            source(docsRender)
            property("digitalitems.docsOutput", layout.buildDirectory.dir("docs-models").get().asFile.absolutePath)
            runDir("run/docs-render")
        }
        named("client") {
            this.programArgs("--username=Player")
        }
        create("digitalItemsGameTest") {
            server()
            source(testMod)
            property("fabric-api.gametest", "true")
            // Testiarium 0.1.1 scans once from either fixture entrypoint.
            property("fabric.debug.loadLate", "testiarium_testmod,testiarium_cct_testmod")
            property("testiarium.tags", "digitalitems")
            property("testiarium.structures", commonProject.layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            property("testiarium.fixture-source", commonProject.file("src/testMod/resources/gameteststructures").absolutePath)
            property("testiarium.cct-fixtures", commonProject.layout.buildDirectory.dir("resources/testMod/computer").get().asFile.absolutePath)
            property("testiarium.gametest-report", layout.buildDirectory.file("test-results/digitalitems-gametest.xml").get().asFile.absolutePath)
            vmArg("-ea")
            runDir("run/digitalitems-gametest")
        }
    }
}

repositories {
    // location of the maven that hosts JEI files since January 2023
    maven {
        name = "Jared's maven"
        url = uri("https://maven.blamejared.com/")
        content {
            includeGroup("mezz.jei")
        }
    }
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
}

val docsModels = layout.buildDirectory.dir("docs-models")
tasks.named("runDocsRender") {
    doFirst {
        delete(docsModels)
    }
    doLast {
        for (name in listOf("digitizer", "advanced_digitizer")) {
            check(docsModels.get().file("$name.png").asFile.isFile) {
                "Missing native documentation render: $name"
            }
        }
    }
}

tasks.register<Copy>("exportDocsModels") {
    group = "documentation"
    description = "Renders native Minecraft item models and refreshes the documentation PNGs."
    dependsOn("runDocsRender")
    from(docsModels) {
        include("digitizer.png", "advanced_digitizer.png")
    }
    into(project(":typed-peripheral-digitalitems").file("documentation/assets/peripherals"))
}

dependencies {
    implementation(if (is121) libs121.bundles.kotlin else libs.bundles.kotlin)

    modImplementation(if (is121) libs121.bundles.fabric.core else libs.bundles.fabric.core)
    modImplementation(if (is121) libs121.bundles.fabric.cc else libs.bundles.fabric.cc) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
    }
    modImplementation(if (is121) libs121.bundles.fabric.include else libs.bundles.fabric.include) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
        exclude("com.terraformersmc", "modmenu")
    }
    include(if (is121) libs121.bundles.fabric.include else libs.bundles.fabric.include) {
        exclude("com.terraformersmc", "modmenu")
    }
    if (!providers.gradleProperty("minimalTestEnvironment").isPresent) {
        modRuntimeOnly("maven.modrinth:modmenu:${if (is121) libs121.versions.modmenu.get() else libs.versions.modmenu.get()}")
        modRuntimeOnly(if (is121) libs121.bundles.externalMods.fabric.runtime else libs.bundles.externalMods.fabric.runtime) {
            exclude("net.fabricmc.fabric-api")
            exclude("net.fabricmc", "fabric-loader")
        }
    }

    add("modTestModImplementation", if (is121) libs121.bundles.kotlin else libs.bundles.kotlin)
    add("modTestModImplementation", if (is121) libs121.bundles.fabric.core else libs.bundles.fabric.core)
    add("modTestModImplementation", if (is121) libs121.bundles.fabric.cc else libs.bundles.fabric.cc)
    add("modTestModImplementation", files(testiariumMainArtifacts))
    add("modTestModImplementation", files(testiariumTestArtifacts))
    add("modTestModImplementation", files(testiariumCctArtifacts))
}

modPublishing {
    output.set(tasks.remapJar)
    requiredDependencies.set(
        listOf(
            "cc-tweaked",
            "fabric-language-kotlin",
        ),
    )
    requiredDependenciesCurseforge.add("forge-config-api-port")
    requiredDependenciesModrinth.add("forge-config-api-port")
    shake()
}

modrinth { loaders.set(listOf("fabric")) }

publishingShaking {
    shake()
    project.publishing {
        publications {
            named<MavenPublication>("maven") {
                mavenDependencies {
                    exclude(project.dependencies.create("site.siredvin:"))
                }
            }
        }
    }
}
