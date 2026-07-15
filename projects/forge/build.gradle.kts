import org.gradle.api.artifacts.ExternalModuleDependency
import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
    id("site.siredvin.forge")
}

baseShaking {
    projectPart.set("forge")
    shake()
}

forgeShaking {
    commonProjectName.set("core")
    useAT.set(true)
    useMixins.set(true)
    useJarJar.set(true)
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
    libs.bundles.forge.cc.get().map { implementation(fg.deobf(it)) }
    libs.bundles.forge.include.get().map { implementation(fg.deobf(it)) }
    libs.bundles.forge.jjar.get().map { jarJar(it) }

    libs.bundles.externalMods.forge.runtime.get().map { runtimeOnly(fg.deobf(it)) }

    listOf(
        "site.siredvin:testiarium-forge-1.20.1:0.1.1",
        "site.siredvin:testiarium-forge-1.20.1:0.1.1:cct-test-mod@jar",
    ).forEach { notation ->
        add(
            testMod.implementationConfigurationName,
            fg.deobf((project.dependencies.create(notation) as ExternalModuleDependency).apply { isTransitive = false }),
        )
    }
}

minecraft {
    runs {
        create("gameTestServer") {
            workingDirectory(file("run/digitalitems-gametest"))
            property("forge.enabledGameTestNamespaces", "digitalitems_testmod")
            property("testiarium.tags", "digitalitems")
            property("testiarium.structures", project(":core").layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            property("testiarium.fixture-source", project(":core").file("src/testMod/resources/gameteststructures").absolutePath)
            property("testiarium.cct-fixtures", project(":core").layout.buildDirectory.dir("resources/testMod/computer").get().asFile.absolutePath)
            property("testiarium.gametest-report", layout.buildDirectory.file("test-results/digitalitems-gametest.xml").get().asFile.absolutePath)
            jvmArgs("-ea")
            args("--nogui")
            mods {
                create("digitalitems") {
                    source(sourceSets.main.get())
                }
                create("digitalitems_testmod") {
                    source(testMod)
                    source(project(":core").sourceSets["testMod"])
                }
            }
        }
    }
}

modPublishing {
    output.set(tasks.jarJar)
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
                fg.component(this)
                mavenDependencies {
                    exclude(dependencies.create("site.siredvin:"))
                    exclude(libs.jei.forge.get())
                }
            }
        }
    }
}
