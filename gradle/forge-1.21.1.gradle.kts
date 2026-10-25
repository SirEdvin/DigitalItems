import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
    id("site.siredvin.neoforge")
}

baseShaking {
    projectPart.set("forge")
    shake()
}

neoforgeShaking {
    commonProjectName.set("core:1.21.1")
    useAT.set(true)
    extraRawVersionMappings.set(
        mapOf(
            "computercraft" to libs121.versions.cc.tweaked.get(),
            "tweakium" to libs121.versions.tweakium.asProvider().get(),
            "broccolium" to libs121.versions.broccolium.asProvider().get(),
        ),
    )
    shake()
}

val testMod = sourceSets.create("testMod") {
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    compileClasspath += project(":core:1.21.1").sourceSets["testMod"].output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += project(":core:1.21.1").sourceSets["testMod"].output
}

repositories {
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content {
            includeGroup("thedarkcolour")
        }
    }
    // location of the maven that hosts JEI files since January 2023
    maven {
        name = "Jared's maven"
        url = uri("https://maven.blamejared.com/")
        content {
            includeGroup("mezz.jei")
        }
    }
}

dependencies {
    implementation(libs121.bundles.kotlin)
    implementation(libs121.bundles.forge.raw)
    implementation(libs121.bundles.forge.cc)
    implementation(libs121.bundles.forge.include)
    jarJar(libs121.bundles.forge.jjar) {
        isTransitive = false
    }

    if (!providers.gradleProperty("minimalTestEnvironment").isPresent) {
        runtimeOnly(libs121.bundles.externalMods.forge.runtime)
    }

    // The production all-JAR already supplies KFF; its POM adds a second FML loader.
    add(testMod.implementationConfigurationName, libs121.testiarium.forge.get().toString()) { exclude(group = "thedarkcolour") }
    for (feature in listOf("test-mod", "cct-test-mod")) {
        add(testMod.implementationConfigurationName, libs121.testiarium.forge.get().toString()) {
            exclude(group = "thedarkcolour")
            capabilities { requireCapability("site.siredvin:testiarium-forge-$feature") }
        }
    }
}

neoForge {
    runs.named("data") {
        programArguments.set(
            listOf(
                "--mod", "digitalitems", "--all", "--output", file("src/generated/resources").absolutePath,
                "--existing", rootProject.file("projects/core/src/main/resources").absolutePath,
                "--existing", rootProject.file("projects/forge/src/main/resources").absolutePath,
                "--existing", file("src/main/resources").absolutePath,
            ),
        )
    }
    parchment {
        minecraftVersion = libs121.versions.parchmentMc.get()
        mappingsVersion = libs121.versions.parchment.get()
    }
    val digitalitems = mods.named("digitalitems")
    val digitalitemsTestMod by mods.registering {
        sourceSet(testMod)
        sourceSet(project(":core:1.21.1").sourceSets["testMod"])
    }
    runs {
        register("gameTestServer") {
            type = "gameTestServer"
            sourceSet.set(testMod)
            gameDirectory = file("run/digitalitems-gametest")
            systemProperty("testiarium.tags", "digitalitems")
            systemProperty("testiarium.structures", project.project(":core:1.21.1").layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            systemProperty("testiarium.fixture-source", project.project(":core:1.21.1").file("src/testMod/resources/gameteststructures").absolutePath)
            systemProperty("testiarium.cct-fixtures", project.project(":core:1.21.1").layout.buildDirectory.dir("resources/testMod/computer").get().asFile.absolutePath)
            systemProperty("testiarium.gametest-report", layout.buildDirectory.file("test-results/digitalitems-gametest.xml").get().asFile.absolutePath)
            jvmArgument("-ea")
            programArgument("--nogui")
            programArgument("--mixin.config=digitalitems-cct-gametest.mixins.json")
            loadedMods.add(digitalitems.get())
            loadedMods.add(digitalitemsTestMod.get())
        }
    }
}

modPublishing {
    output.set(tasks.jar)
    requiredDependencies.set(
        listOf(
            "cc-tweaked",
            "kotlin-for-forge",
        ),
    )
    shake()
}

modrinth { loaders.set(listOf("neoforge")) }

publishingShaking {
    shake()
    project.publishing {
        publications {
            named<MavenPublication>("maven") {
                mavenDependencies {
                    exclude(dependencies.create("site.siredvin:"))
                    exclude(libs121.jei.forge.get())
                }
            }
        }
    }
}
