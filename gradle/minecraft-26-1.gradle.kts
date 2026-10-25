import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
}

require(name == "26.1.2")
val part = path.split(':')[1]
val target = "26.1.2"
version = providers.gradleProperty("modVersion").get()
base.archivesName.set("digitalitems-${if (part == "core") "common" else part}-$target")
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    withSourcesJar()
}
kotlin { jvmToolchain(25) }
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_25)
    compilerOptions.allWarningsAsErrors.set(true)
}
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}
repositories {
    mavenCentral()
    maven("https://mvn.siredvin.site/minecraft")
    maven("https://maven.fabricmc.net/")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.squiddev.cc/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
    maven("https://api.modrinth.com/maven")
}
if (part == "forge") {
    apply(plugin = "net.neoforged.moddev")
    extensions.configure<NeoForgeExtension> { version = "26.1.2.122" }
    tasks.named("createMinecraftArtifacts") { dependsOn("stonecutterGenerate") }
} else {
    apply(plugin = "net.fabricmc.fabric-loom")
    dependencies.add("minecraft", "com.mojang:minecraft:$target")
    extensions.configure<LoomGradleExtensionAPI> {
        accessWidenerPath.set(rootProject.file("projects/core/versions/$target/src/main/resources/digitalitems-common.accesswidener"))
    }
}
val common = if (part == "core") project else project(":core:$target").also { evaluationDependsOn(it.path) }
val commonSets = common.extensions.getByType<SourceSetContainer>()
val testMod = sourceSets.create("testMod") {
    compileClasspath += sourceSets.main.get().compileClasspath + sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath + sourceSets.main.get().output
    if (part != "core") {
        compileClasspath += commonSets.named("testMod").get().output
        runtimeClasspath += commonSets.named("testMod").get().output
    }
}
configurations.named(testMod.implementationConfigurationName) { extendsFrom(configurations.implementation.get()) }
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.4.0")
    if (part == "core") {
        for (ccPart in listOf("common", "common-api", "core")) implementation("cc.tweaked:cc-tweaked-$target-$ccPart:1.120.0")
        for (library in listOf("broccolium", "tweakium")) api("site.siredvin:$library-common-$target:2.0.0-rc.1")
        compileOnly("maven.modrinth:forge-config-api-port:jUe0ucoE")
        compileOnly("org.spongepowered:mixin:0.8.7")
    } else {
        implementation(commonSets.named("main").map { it.output })
        implementation("cc.tweaked:cc-tweaked-$target-$part:1.120.0")
        for (library in listOf("broccolium", "tweakium")) {
            implementation("site.siredvin:$library-$part-$target:2.0.0-rc.1")
            add(if (part == "fabric") "include" else "jarJar", "site.siredvin:$library-$part-$target:2.0.0-rc.1") {
                isTransitive = false
                version {
                    require("[2.0.0-rc.1]")
                    prefer("2.0.0-rc.1")
                }
            }
        }
        if (part == "fabric") {
            implementation("net.fabricmc:fabric-loader:0.19.5")
            implementation("net.fabricmc.fabric-api:fabric-api:0.155.3+26.1.2")
            implementation("net.fabricmc:fabric-language-kotlin:1.13.12+kotlin.2.4.0")
            implementation("maven.modrinth:forge-config-api-port:jUe0ucoE")
        } else {
            implementation("thedarkcolour:kotlinforforge-neoforge:6.4.0")
        }
    }
    for (harnessPart in if (part == "core") listOf("core") else listOf("core", part)) {
        for (feature in listOf("", "test-mod", "cct-test-mod")) {
            add(testMod.implementationConfigurationName, "site.siredvin:testiarium-$harnessPart-$target:2.0.0-rc.1") {
                exclude(group = "thedarkcolour")
                if (feature.isNotEmpty()) capabilities { requireCapability("site.siredvin:testiarium-$harnessPart-$feature") }
            }
        }
    }
}
if (part == "core") {
    tasks.named<ProcessResources>(testMod.processResourcesTaskName) {
        dependsOn(":typescript-tests:compileTestLua")
        from(project(":typescript-tests").layout.buildDirectory.dir("generated/test-lua")) { into("computer/tests") }
        from(rootProject.file("projects/core/src/testMod/resources/gameteststructures")) { into("data/minecraft/structure") }
    }
}
val report = layout.buildDirectory.file("test-results/digitalitems-gametest.xml").get().asFile.absolutePath
val structures = common.layout.buildDirectory.dir("resources/testMod").get().asFile.absolutePath
val computers = common.layout.buildDirectory.dir("resources/testMod/computer").get().asFile.absolutePath
val generated = file("src/generated/resources")
sourceSets.main.get().resources.srcDir(generated)
if (part == "fabric") {
    extensions.configure<LoomGradleExtensionAPI> {
        mods {
            register("digitalitems") {
                sourceSet(sourceSets.main.get())
                sourceSet(commonSets.named("main").get())
            }
            register("digitalitems_testmod") {
                sourceSet(testMod)
                sourceSet(commonSets.named("testMod").get())
            }
        }
        runs {
            create("digitalItemsGameTest") {
                server()
                source(testMod)
                property("fabric-api.gametest", "true")
                property("testiarium.fixture.consumer", "true")
                property("testiarium.tags", "digitalitems")
                property("testiarium.structures", structures)
                property("testiarium.cct-fixtures", computers)
                property("testiarium.gametest-report", report)
                vmArg("-ea")
                runDir("run/digitalitems-gametest")
            }
            create("data") {
                client()
                source(sourceSets.main.get())
                property("fabric-api.datagen", "true")
                property("fabric-api.datagen.modid", "digitalitems")
                property("fabric-api.datagen.output-dir", generated.absolutePath)
                runDir("run/datagen")
            }
        }
    }
} else if (part == "forge") {
    extensions.configure<NeoForgeExtension> {
        val production = mods.create("digitalitems") {
            sourceSet(sourceSets.main.get())
            sourceSet(commonSets.named("main").get())
        }
        val fixture = mods.create("digitalitemsTestMod") {
            sourceSet(testMod)
            sourceSet(commonSets.named("testMod").get())
        }
        runs {
            create("client") {
                client()
                loadedMods.add(production)
                gameDirectory = file("run/client")
            }
            create("server") {
                server()
                loadedMods.add(production)
                programArgument("--nogui")
                gameDirectory = file("run/server")
            }
            create("gameTestServer") {
                type = "gameTestServer"
                sourceSet.set(testMod)
                loadedMods.addAll(production, fixture)
                systemProperty("testiarium.tags", "digitalitems")
                systemProperty("testiarium.structures", structures)
                systemProperty("testiarium.cct-fixtures", computers)
                systemProperty("testiarium.gametest-report", report)
                jvmArgument("-ea")
                gameDirectory = file("run/digitalitems-gametest")
            }
            for (clientData in listOf(true, false)) {
                create(if (clientData) "clientData" else "serverData") {
                    if (clientData) clientData() else serverData()
                    loadedMods.add(production)
                    programArguments.addAll("--mod", "digitalitems", "--output", generated.absolutePath)
                    gameDirectory = file("run/datagen")
                }
            }
        }
        tasks.register("runData") { dependsOn("runClientData", "runServerData") }
    }
}
if (part != "core") {
    tasks.named<Jar>("jar") {
        from(commonSets.named("main").map { it.output })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
tasks.withType<ProcessResources>().configureEach {
    exclude("digitalitems.accesswidener")
    inputs.property("version", project.version)
    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml")) { expand("version" to project.version) }
}
publishing {
    publications.register<MavenPublication>("maven") {
        artifactId = base.archivesName.get()
        from(components["java"])
    }
}
