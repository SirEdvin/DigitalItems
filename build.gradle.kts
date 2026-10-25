import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    java
    id("fabric-loom") version "1.17.11" apply false
    id("org.jetbrains.kotlin.jvm") version "2.2.20" apply false
    id("net.neoforged.moddev.legacyforge") version "2.0.142" apply false
    id("site.siredvin.root") version "0.9.2"
    id("site.siredvin.release") version "0.9.2"
    id("site.siredvin.linting") version "0.9.2"
}

val minecraftVersions = providers.gradleProperty("maintainedMinecraftVersions").get().split(',')
tasks.register("gameTest") {
    group = "verification"
    description = "Runs all DigitalItems GameTests on Fabric, Forge and NeoForge."
    dependsOn(minecraftVersions.flatMap { listOf(":forge:$it:runGameTestServer", ":fabric:$it:runDigitalItemsGameTest") })
    doLast {
        providers.exec {
            commandLine(
                listOf("python3", ".github/scripts/check-gametest-reports.py") + minecraftVersions.flatMap { v ->
                    listOf("fabric", "forge").map { "projects/$it/versions/$v/build/test-results/digitalitems-gametest.xml" }
                },
            )
        }.result.get().assertNormalExitValue()
    }
}
tasks.named("check") {
    doLast {
        for (version in minecraftVersions) {
            val native = project(":forge:$version").extensions.getByType<net.neoforged.moddevgradle.dsl.ModDevExtension>()
            for (run in native.runs) {
                val fixture = if (version == "1.20.1") "digitalitems_testmod" else "digitalitemsTestMod"
                val expected = if (run.name == "gameTestServer") setOf("digitalitems", fixture) else setOf("digitalitems")
                check(run.loadedMods.get().map { it.name }.toSet() == expected) { "Unexpected mods in $version ${run.name}" }
                check(run.sourceSet.get().name == if (run.name == "gameTestServer") "testMod" else "main")
            }
        }
    }
}

tasks.named("build") {
    dependsOn(minecraftVersions.flatMap { v -> listOf("core", "fabric", "forge").map { ":$it:$v:build" } })
}

subprojectShaking {
    withKotlin.set(true)
    kotlinVersion.set("2.0.0")
}
val config = subprojectShaking
val setupSubproject = subprojectShaking::setupSubproject
val projectGroup: String by project
subprojects {
    if (projectDir.parentFile.name == "versions") {
        val minecraftVersion = name
        extra["minecraftVersion"] = minecraftVersion
        config.javaVersion.set(if (name == "1.20.1") JavaVersion.VERSION_17 else JavaVersion.VERSION_21)
        setupSubproject(this)
        if (path.startsWith(":core:")) pluginManager.apply("net.fabricmc.fabric-loom-companion")
        // Every leaf has the same name; Gradle module identities must still be unique.
        group = "$projectGroup.${path.split(':')[1]}"
        extensions.configure<SpotlessExtension> {
            java { target(emptyList<String>()) }
            kotlin { target(emptyList<String>()) }
            kotlinGradle { target(emptyList<String>()) }
        }
        val shared = projectDir.parentFile.parentFile.resolve("src").toPath()
        val local = projectDir.resolve("src")
        val overridden = Spec<FileTreeElement> { entry ->
            val file = entry.file.toPath()
            file.startsWith(shared) && local.resolve(shared.relativize(file).toString()).isFile
        }
        pluginManager.withPlugin("java") {
            extensions.getByType<SourceSetContainer>().configureEach {
                java.exclude(overridden)
                resources.exclude(overridden)
                allJava.exclude(overridden)
                allSource.exclude(overridden)
            }
            tasks.withType<Jar>().configureEach {
                exclude("**/.cache/**")
                dependsOn("stonecutterGenerate")
            }
            tasks.withType<ProcessResources>().configureEach { dependsOn("stonecutterGenerate") }
            val resources = extensions.getByType<SourceSetContainer>().named("main").get().resources
            gradle.projectsEvaluated {
                resources.setSrcDirs(
                    resources.srcDirs.filterNot { it.parentFile.name == "generated" } +
                        listOf(local.resolve("generated/resources"), shared.resolve("generated/resources").toFile()),
                )
            }
        }
        pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
                compilerOptions.languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
                compilerOptions.apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
                compilerOptions.freeCompilerArgs.add("-Xsuppress-version-warnings")
            }
            extensions.getByType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension>().sourceSets.configureEach {
                kotlin.exclude(overridden)
            }
        }
        if (path.startsWith(":fabric:") || path.startsWith(":forge:")) {
            evaluationDependsOn(":core:$minecraftVersion")
            afterEvaluate {
                tasks.named("processResources") { dependsOn(":core:$minecraftVersion:stonecutterGenerate") }
                tasks.withType<Jar>().configureEach { dependsOn(":core:$minecraftVersion:stonecutterGenerate") }
                tasks.named<Jar>("sourcesJar") {
                    from(project(":core:$minecraftVersion").extensions.getByType<SourceSetContainer>().named("main").map { it.allSource })
                    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                }
                if (path.startsWith(":forge:")) {
                    val native = extensions.getByType<net.neoforged.moddevgradle.dsl.ModDevExtension>()
                    native.runs.configureEach {
                        if (name != "gameTestServer") loadedMods.set(setOf(native.mods.named("digitalitems").get()))
                    }
                }
                val report = layout.buildDirectory.file("test-results/digitalitems-gametest.xml")
                tasks.named(if (path.startsWith(":fabric:")) "runDigitalItemsGameTest" else "runGameTestServer") {
                    doFirst { report.get().asFile.delete() }
                    doLast { check(report.get().asFile.isFile) { "GameTest process exited without a fresh report: ${report.get().asFile}" } }
                }
            }
        }
        pluginManager.withPlugin("maven-publish") {
            extensions.configure<PublishingExtension> {
                publications.withType<MavenPublication>().configureEach { groupId = projectGroup }
            }
        }
    }
}

githubShaking {
    modBranch.set("multiversion")
    useFabric.set(false)
    useForge.set(false)
    shake()
}

extensions.configure<com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension> {
    val modVersion = providers.gradleProperty("modVersion")
    tagName.set(modVersion.map { "v$it" })
    releaseName.set(modVersion.map { "Digital Items $it (1.20.1 / 1.21.1)" })
    overwrite.set(false)
    allowUploadToExisting.set(false)
    gradle.projectsEvaluated {
        for (version in minecraftVersions) {
            releaseAssets.from(project(":fabric:$version").tasks.named("remapJar"))
            releaseAssets.from(project(":forge:$version").tasks.named(if (version == "1.20.1") "reobfJar" else "jar"))
        }
    }
}

repositories { mavenCentral() }

spotless {
    java {
        target(
            fileTree("projects") {
                include("**/src/**/*.java")
                exclude("**/build/**", "**/src/generated/**")
            },
        )
    }
    kotlin {
        target(
            fileTree("projects") {
                include("**/src/**/*.kt")
                exclude("**/build/**", "**/src/generated/**")
            },
        )
    }
    kotlinGradle { target("*.gradle.kts", "gradle/*.gradle.kts", "projects/*/*.gradle.kts") }
}
