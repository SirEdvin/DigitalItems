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
     accessWidener.set(project(":core").file("src/main/resources/template.accesswidener"))
    extraVersionMappings.set(
        mapOf(
            "computercraft" to "cc-tweaked",
            "forgeconfigapiport" to "forgeconfigapirt",
            "peripheralium" to "peripheralium",
        ),
    )
    shake()
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

    modImplementation(libs.bundles.fabric.core)
    modImplementation(libs.bundles.fabric.cc) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
    }

    modRuntimeOnly(libs.bundles.externalMods.fabric.runtime) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
    }
}

//modPublishing {
//    output.set(tasks.remapJar)
//    requiredDependencies.set(
//        listOf(
//            "cc-tweaked",
//            "fabric-language-kotlin",
//            "peripheralium",
//        ),
//    )
//    requiredDependenciesCurseforge.add("forge-config-api-port-fabric")
//    requiredDependenciesModrinth.add("forge-config-api-port")
//    shake()
//}

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
