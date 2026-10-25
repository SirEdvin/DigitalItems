@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.vanilla")
    id("site.siredvin.publishing")
}

val modVersion: String by extra
val minecraftVersion: String by extra
val modBaseName: String by extra
val is121 = sc.current.parsed >= "1.21"

baseShaking {
    projectPart.set("common")
    shake()
}

vanillaShaking {
    accessWideners.add(rootProject.file("projects/core/${if (is121) "versions/1.21.1/" else ""}src/main/resources/digitalitems-common.accesswidener").absolutePath)
    accessWideners.add(rootProject.file("projects/core/src/main/resources/digitalitems.accesswidener").absolutePath)
    shake()
}

val testMod = sourceSets.create("testMod") {
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
}

dependencies {
    implementation(if (is121) libs121.bundles.kotlin else libs.bundles.kotlin)
    implementation(if (is121) libs121.bundles.cccommon else libs.bundles.cccommon)
    api(if (is121) libs121.bundles.apicommon else libs.bundles.apicommon)
    compileOnly(if (is121) libs121.fabric.config else libs.fabric.config) {
        isTransitive = false
    }
    val testiarium = (if (is121) libs121.testiarium.core else libs.testiarium.core).get().toString()
    add(testMod.implementationConfigurationName, testiarium)
    for (feature in listOf("test-mod", "cct-test-mod")) {
        add(testMod.implementationConfigurationName, testiarium) {
            capabilities { requireCapability("site.siredvin:testiarium-core-$feature") }
        }
    }
    add(testMod.compileOnlyConfigurationName, if (is121) libs121.bundles.cccommon else libs.bundles.cccommon)
    if (!is121) {
        add(testMod.compileOnlyConfigurationName, libs.mixin)
        add(testMod.compileOnlyConfigurationName, "cc.tweaked:cc-tweaked-1.20.1-common:${libs.versions.cc.tweaked.get()}")
    }
}

tasks.named<ProcessResources>(testMod.processResourcesTaskName) {
    dependsOn(":typescript-tests:compileTestLua")
    from(project(":typescript-tests").layout.buildDirectory.dir("generated/test-lua")) {
        into("computer/tests")
    }
}

publishingShaking {
    shake()
}
