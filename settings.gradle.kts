pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://mvn.siredvin.site/minecraft") {
            name = "SirEdvin's Minecraft repository"
            content {
                includeGroup("net.minecraftforge")
                includeGroup("net.minecraftforge.gradle")
                includeGroup("net.neoforged")
                includeGroup("net.neoforged.moddev")
                includeGroup("org.parchmentmc")
                includeGroup("org.parchmentmc.feather")
                includeGroup("org.parchmentmc.data")
                includeGroup("org.spongepowered")
                includeGroup("org.spongepowered.gradle.vanilla")
                includeGroup("net.fabricmc")
                includeGroup("fabric-loom")
                includeGroupByRegex("site.siredvin.*")
            }
        }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.spongepowered.mixin") {
                useModule("org.spongepowered:mixingradle:${requested.version}")
            }
            if (requested.id.id.startsWith("site.siredvin.")) {
                useModule("site.siredvin:modding-buildenv:${requested.version}")
            }
        }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs121") { from(files("gradle/libs121.versions.toml")) }
    }
}

rootProject.name = "DigitalItems"

include(":core")
include(":forge")
include(":fabric")
include(":typed-peripheral-digitalitems")
include(":typescript-tests")

for (project in rootProject.children) {
    project.projectDir = file("projects/${project.name}")
}

stonecutter {
    create("core", "fabric", "forge") {
        versions(*providers.gradleProperty("maintainedMinecraftVersions").get().split(',').toTypedArray())
        vcsVersion = "1.20.1"
    }
}

for (version in listOf("1.20.1", "1.21.1")) {
    val node = project(":forge:$version")
    node.buildFileName = rootDir.resolve("gradle/forge-$version.gradle.kts").relativeTo(node.projectDir).path
}
