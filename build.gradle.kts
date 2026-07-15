plugins {
    java
    id("site.siredvin.root") version "0.9.0"
    id("site.siredvin.release") version "0.9.0"
}

tasks.register("gameTest") {
    group = "verification"
    description = "Runs DigitalItems GameTests on NeoForge and Fabric."
    dependsOn(":forge:runGameTestServer", ":fabric:runDigitalItemsGameTest")
}

subprojectShaking {
    withKotlin.set(true)
    kotlinVersion.set("2.0.0")
    javaVersion.set(JavaVersion.VERSION_21)
}

val setupSubproject = subprojectShaking::setupSubproject

subprojects {
    if (name != "typescript-tests") {
        setupSubproject(this)
    }
}

githubShaking {
    modBranch.set("1.21")
    useForgeJarJar.set(true)
    shake()
}

repositories {
    mavenCentral()
}
