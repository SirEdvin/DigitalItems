plugins {
    java
    id("site.siredvin.root") version "0.8.17"
    id("site.siredvin.release") version "0.8.17"
}

subprojectShaking {
    withKotlin.set(true)
    kotlinVersion.set("2.0.0")
}

val setupSubproject = subprojectShaking::setupSubproject

subprojects {
    setupSubproject(this)
}

githubShaking {
    modBranch.set("1.20")
    useForgeJarJar.set(true)
    shake()
}

repositories {
    mavenCentral()
}