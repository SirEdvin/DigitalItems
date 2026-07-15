import com.github.gradle.node.npm.task.NpmTask

plugins {
    java
    id("com.github.node-gradle.node") version "7.1.0"
    id("site.siredvin.root") version "0.8.18"
    id("site.siredvin.release") version "0.8.18"
}

node {
    version.set("22.14.0")
    download.set(true)
    nodeProjectDir.set(file("projects/core/src/testTypeScript"))
    workDir.set(layout.projectDirectory.dir(".gradle/nodejs"))
    npmWorkDir.set(layout.projectDirectory.dir(".gradle/npm"))
    npmInstallCommand.set("ci")
}

val compileTestLua by tasks.registering(NpmTask::class) {
    dependsOn(tasks.npmInstall)
    npmCommand.set(listOf("run", "build"))
    inputs.files(fileTree("projects/core/src/testTypeScript") {
        include("package.json", "package-lock.json", "tsconfig.json", "build.mjs", "src/**/*.ts", "packages/**/*.ts", "packages/**/package.json")
        exclude("node_modules/**")
    })
    outputs.dir(layout.projectDirectory.dir("projects/core/build/generated/test-lua"))
}

tasks.register("gameTest") {
    group = "verification"
    description = "Runs DigitalItems GameTests on Forge and Fabric."
    dependsOn(":forge:runGameTestServer", ":fabric:runDigitalItemsGameTest")
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
