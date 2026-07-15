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
    commonProjectName.set("core")
    useAT.set(true)
    extraVersionMappings.set(
        mapOf(
            "computercraft" to "cc-tweaked",
            "tweakium" to "tweakium",
            "broccolium" to "broccolium",
        ),
    )
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
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.forge.raw)
    implementation(libs.bundles.forge.cc)
    implementation(libs.bundles.forge.include)
    jarJar(libs.bundles.forge.jjar) {
        isTransitive = false
    }

    runtimeOnly(libs.bundles.externalMods.forge.runtime)

    val testiariumArtifacts = listOf(
        "site.siredvin:testiarium-forge-1.21.1:0.1.1",
        "site.siredvin:testiarium-forge-1.21.1:0.1.1:cct-test-mod@jar",
    )
    testiariumArtifacts.forEach { notation ->
        add(testMod.implementationConfigurationName, notation) {
            isTransitive = false
        }
    }
    runtimeOnly(libs.testiarium.forge)
    runtimeOnly("site.siredvin:testiarium-forge-1.21.1:0.1.1:cct-test-mod@jar") {
        isTransitive = false
    }
}

neoForge {
    val digitalitems = mods.named("digitalitems")
    val digitalitemsTestMod by mods.registering {
        sourceSet(testMod)
        sourceSet(project(":core").sourceSets["testMod"])
    }
    runs {
        register("gameTestServer") {
            type = "gameTestServer"
            gameDirectory = file("run/digitalitems-gametest")
            systemProperty("testiarium.tags", "digitalitems")
            systemProperty("testiarium.structures", project.project(":core").layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            systemProperty("testiarium.fixture-source", project.project(":core").file("src/testMod/resources/gameteststructures").absolutePath)
            systemProperty("testiarium.cct-fixtures", project.project(":core").layout.buildDirectory.dir("resources/testMod/computer").get().asFile.absolutePath)
            systemProperty("testiarium.gametest-report", layout.buildDirectory.file("test-results/digitalitems-gametest.xml").get().asFile.absolutePath)
            jvmArgument("-ea")
            programArgument("--nogui")
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

publishingShaking {
    shake()
    project.publishing {
        publications {
            named<MavenPublication>("maven") {
                mavenDependencies {
                    exclude(dependencies.create("site.siredvin:"))
                    exclude(libs.jei.forge.get())
                }
            }
        }
    }
}
