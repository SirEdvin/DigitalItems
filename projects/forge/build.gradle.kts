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
    extraVersionMappings.set(
        mapOf(
            "computercraft" to "cc-tweaked",
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
    implementation(libs.bundles.forge.raw)
    libs.bundles.forge.cc.get().map { implementation(fg.deobf(it)) }

    libs.bundles.externalMods.forge.runtime.get().map { runtimeOnly(fg.deobf(it)) }
}


//modPublishing {
//    output.set(tasks.jar)
//    requiredDependencies.set(
//        listOf(
//            "cc-tweaked",
//            "kotlin-for-forge",
//            "peripheralium"
//        ),
//    )
//    shake()
//}

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
