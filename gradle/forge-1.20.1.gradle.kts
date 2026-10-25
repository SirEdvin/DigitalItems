import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.neoforged.moddevgradle.legacyforge.dsl.ObfuscationExtension
import site.siredvin.peripheralium.gradle.mavenDependencies

plugins {
    id("net.neoforged.moddev.legacyforge")
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
}

val commonProject = project(":core:1.20.1")
baseShaking {
    projectPart.set("forge")
    shake()
}

val testMod = sourceSets.create("testMod") {
    compileClasspath += sourceSets.main.get().compileClasspath + sourceSets.main.get().output
    compileClasspath += commonProject.sourceSets["testMod"].output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath + sourceSets.main.get().output
    runtimeClasspath += commonProject.sourceSets["testMod"].output
}

legacyForge {
    enable { forgeVersion = "1.20.1-${libs.versions.forge.asProvider().get()}" }
    parchment {
        minecraftVersion = libs.versions.parchmentMc.get()
        mappingsVersion = libs.versions.parchment.get()
    }
    validateAccessTransformers.set(true)
    setAccessTransformers(sourceSets.main.map { it.resources.matching { include("META-INF/accesstransformer.cfg") } })
    mods {
        create("digitalitems") { sourceSet(sourceSets.main.get()) }
        create("digitalitems_testmod") {
            sourceSet(testMod)
            sourceSet(commonProject.sourceSets["testMod"])
        }
    }
    runs {
        create("client") {
            client()
            gameDirectory = file("run")
        }
        create("server") {
            server()
            gameDirectory = file("run/server")
            programArgument("--nogui")
        }
        create("data") {
            data()
            gameDirectory = file("run")
            programArguments.addAll(
                "--mod", "digitalitems", "--all", "--output", file("src/generated/resources").absolutePath,
                "--existing", rootProject.file("projects/core/src/main/resources").absolutePath,
                "--existing", rootProject.file("projects/forge/src/main/resources").absolutePath,
                "--existing", file("src/main/resources").absolutePath,
            )
        }
        create("gameTestServer") {
            type = "gameTestServer"
            sourceSet.set(testMod)
            gameDirectory = file("run/digitalitems-gametest")
            systemProperty("forge.enabledGameTestNamespaces", "digitalitems_testmod")
            systemProperty("testiarium.tags", "digitalitems")
            systemProperty("testiarium.structures", commonProject.layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            systemProperty("testiarium.fixture-source", commonProject.file("src/testMod/resources/gameteststructures").absolutePath)
            systemProperty("testiarium.cct-fixtures", commonProject.layout.buildDirectory.dir("resources/testMod/computer").get().asFile.absolutePath)
            systemProperty("testiarium.gametest-report", layout.buildDirectory.file("test-results/digitalitems-gametest.xml").get().asFile.absolutePath)
            jvmArgument("-ea")
            programArgument("--nogui")
            programArgument("--mixin.config=digitalitems-cct-gametest.mixins.json")
            loadedMods.add(mods.named("digitalitems"))
            loadedMods.add(mods.named("digitalitems_testmod"))
        }
    }
}

repositories {
    maven("https://thedarkcolour.github.io/KotlinForForge/") { content { includeGroup("thedarkcolour") } }
    maven("https://maven.blamejared.com/") { content { includeGroup("mezz.jei") } }
}
val obfuscation = extensions.getByType<ObfuscationExtension>()
val modImplementation = configurations.getByName("modImplementation")
val modRuntimeOnly = configurations.getByName("modRuntimeOnly")
val modTestImplementation = obfuscation.createRemappingConfiguration(configurations.getByName(testMod.implementationConfigurationName))
dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.forge.raw)
    compileOnly(commonProject)
    add(modImplementation.name, libs.bundles.forge.cc)
    add(modImplementation.name, libs.bundles.forge.include)
    if (!providers.gradleProperty("minimalTestEnvironment").isPresent) {
        add(modRuntimeOnly.name, libs.bundles.externalMods.forge.runtime)
    }
    jarJar(libs.bundles.forge.jjar) { isTransitive = false }
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    for (suffix in listOf("", ":cct-test-mod@jar")) {
        add(modTestImplementation.name, "site.siredvin:testiarium-forge-1.20.1:0.1.1$suffix") { isTransitive = false }
    }
}

tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") { source(commonProject.sourceSets.main.get().allSource) }
tasks.named<JavaCompile>("compileJava") { source(commonProject.sourceSets.main.get().allSource) }
tasks.named("createMinecraftArtifacts") { dependsOn("stonecutterGenerate") }
tasks.processResources {
    from(commonProject.sourceSets.main.get().resources)
    val versions = mapOf(
        "version" to project.version,
        "file" to mapOf("jarVersion" to project.version),
        "forgeVersion" to libs.versions.forge.asProvider().get(),
        "computercraftVersion" to libs.versions.cc.tweaked.get(),
        "tweakiumVersion" to libs.versions.tweakium.asProvider().get(),
        "broccoliumVersion" to libs.versions.broccolium.asProvider().get(),
    )
    inputs.properties(versions)
    filesMatching("META-INF/mods.toml") { expand(versions) }
}
tasks.jar { manifest.attributes["MixinConfigs"] = "digitalitems.mixins.json" }
extra["releaseJar"] = "reobfJar"

// Buildenv 0.9.2 only accepts archive tasks, not MDG's separate reobfuscated output.
val modVersion: String by project
val unstable = modVersion.contains('-')
changelog {
    version.set(modVersion)
    path.set(rootProject.file("CHANGELOG.md").absolutePath)
}
if (!unstable) {
    tasks.register<TaskPublishCurseForge>("publishCurseForge") {
        group = "publishing"
        apiToken = project.extra["curseforgeKey"]
        enabled = apiToken != ""
        dependsOn("check", "reobfJar")
        val main = upload(project.property("CURSEFORGE_ID"), tasks.named("reobfJar"))
        main.releaseType = project.property("CURSEFORGE_RELEASE_TYPE")
        main.addGameVersion("1.20.1")
        main.addModLoader("Forge")
        main.addEnvironment("Client", "Server")
        main.addRequirement("cc-tweaked", "kotlin-for-forge")
        main.changelogType = "markdown"
        main.changelog = KotlinClosure0({ changelog.renderItem(changelog.get(modVersion).withHeader(false)) })
    }
}
modrinth {
    token.set(project.extra["modrinthKey"].toString())
    projectId.set(project.property("MODRINTH_ID").toString())
    versionNumber.set("1.20.1-${project.version}")
    versionName.set("1.20.1-${project.version}")
    versionType.set(if (unstable) "alpha" else project.property("MODRINTH_RELEASE_TYPE").toString())
    uploadFile.set(tasks.named("reobfJar"))
    gameVersions.set(listOf("1.20.1"))
    loaders.set(listOf("forge"))
    changelog.set(
        provider {
            if (unstable) {
                "Unstable version, please don't use it!"
            } else {
                project.changelog.renderItem(project.changelog.get(modVersion).withHeader(false))
            }
        },
    )
    dependencies {
        required.project("cc-tweaked")
        required.project("kotlin-for-forge")
    }
}
tasks.named("modrinth") { dependsOn("check", "reobfJar") }
configurations.named("reobfRuntimeElements") {
    extendsFrom(configurations.implementation.get(), modImplementation)
}
publishingShaking {
    shake()
    project.publishing.publications.named<MavenPublication>("maven") {
        mavenDependencies {
            exclude(dependencies.create("site.siredvin:"))
            exclude(libs.jei.forge.get())
        }
    }
}
