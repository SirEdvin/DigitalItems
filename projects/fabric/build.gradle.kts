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

baseShaking {
    projectPart.set("fabric")
    integrationRepositories.set(true)
    shake()
}

fabricShaking {
    commonProjectName.set("core")
    accessWidener.set(project(":core").file("src/main/resources/digitalitems.accesswidener"))
    extraVersionMappings.set(
        mapOf(
            "computercraft" to "cc-tweaked",
            "forgeconfigapiport" to "forgeconfigapirt",
            "tweakium" to "tweakium",
            "broccolium" to "broccolium",
        ),
    )
    stablePlayer.convention(true)
    shake()
}

val testMod = sourceSets.create("testMod") {
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    compileClasspath += project(":core").sourceSets["testMod"].output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += project(":core").sourceSets["testMod"].output
}

net.fabricmc.loom.configuration.RemapConfigurations.setupForSourceSet(project, testMod)

val testiariumCctArtifacts = configurations.detachedConfiguration(
    project.dependencies.create("site.siredvin:testiarium-core-1.21.1:0.1.1:cct-test-mod@jar"),
    project.dependencies.create("site.siredvin:testiarium-fabric-1.21.1:0.1.1:cct-test-mod@jar"),
).apply {
    isTransitive = false
}

val testiariumTestArtifacts = configurations.detachedConfiguration(
    project.dependencies.create("site.siredvin:testiarium-core-1.21.1:0.1.1:test-mod@jar"),
    project.dependencies.create("site.siredvin:testiarium-fabric-1.21.1:0.1.1:test-mod@jar"),
).apply {
    isTransitive = false
}

val testiariumMainArtifacts = configurations.detachedConfiguration(
    project.dependencies.create("site.siredvin:testiarium-core-1.21.1:0.1.1"),
    project.dependencies.create("site.siredvin:testiarium-fabric-1.21.1:0.1.1"),
).apply {
    isTransitive = false
}

loom {
    mods {
        register("digitalitems-testmod") {
            sourceSet(testMod)
            sourceSet(project(":core").sourceSets["testMod"])
        }
    }
    runs {
        named("client") {
            this.programArgs("--username=Player")
        }
        create("digitalItemsGameTest") {
            server()
            source(testMod)
            property("fabric-api.gametest", "true")
            property("fabric.debug.loadLate", "testiarium_cct_testmod")
            property("testiarium.tags", "digitalitems")
            property("testiarium.structures", project(":core").layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            property("testiarium.fixture-source", project(":core").file("src/testMod/resources/gameteststructures").absolutePath)
            property("testiarium.cct-fixtures", project(":core").layout.buildDirectory.dir("resources/testMod/computer").get().asFile.absolutePath)
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

dependencies {
    implementation(libs.bundles.kotlin)

    modImplementation(libs.bundles.fabric.core)
    modImplementation(libs.bundles.fabric.cc) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
    }
    modImplementation(libs.bundles.fabric.include) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
        exclude("com.terraformersmc", "modmenu")
    }
    include(libs.bundles.fabric.include) {
        exclude("com.terraformersmc", "modmenu")
    }
    modRuntimeOnly("maven.modrinth:modmenu:${libs.versions.modmenu.get()}")

    modRuntimeOnly(libs.bundles.externalMods.fabric.runtime) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
    }

    add("modTestModImplementation", libs.bundles.kotlin)
    add("modTestModImplementation", libs.bundles.fabric.core)
    add("modTestModImplementation", libs.bundles.fabric.cc)
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
    requiredDependenciesCurseforge.add("forge-config-api-port-fabric")
    requiredDependenciesModrinth.add("forge-config-api-port")
    shake()
}

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
