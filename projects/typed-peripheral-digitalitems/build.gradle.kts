import com.github.gradle.node.npm.task.NpmTask

plugins {
    base
    id("com.github.node-gradle.node") version "7.1.0"
}

node {
    version.set("22.14.0")
    download.set(true)
    nodeProjectDir.set(projectDir)
    workDir.set(rootProject.layout.projectDirectory.dir(".gradle/nodejs"))
    npmWorkDir.set(rootProject.layout.projectDirectory.dir(".gradle/npm"))
    npmInstallCommand.set("ci")
}

val compileTypeScript by tasks.registering(NpmTask::class) {
    dependsOn(tasks.npmInstall)
    npmCommand.set(listOf("run", "build"))
    inputs.files(fileTree(projectDir) {
        include("package.json", "package-lock.json", "tsconfig.json", "*.ts")
        exclude("node_modules/**")
    })
    outputs.files(
        file("advanced_digitizer.d.ts"),
        file("advanced_digitizer.lua"),
        file("digitizer.d.ts"),
        file("digitizer.lua"),
        file("lualib_bundle.lua"),
        file("shared.d.ts"),
        file("shared.lua"),
    )
}

val docsOutput = providers.gradleProperty("docsOutput").orElse(file("docs").absolutePath)
val docsGitRevision = providers.gradleProperty("docsGitRevision")
val docsHostedBaseUrl = providers.gradleProperty("docsHostedBaseUrl")

val generateDocs by tasks.registering(NpmTask::class) {
    dependsOn(tasks.npmInstall)
    npmCommand.set(providers.provider {
        buildList {
            addAll(listOf("run", "docs", "--", "--out", docsOutput.get()))
            docsGitRevision.orNull?.let {
                addAll(listOf("--gitRevision", it))
            }
            docsHostedBaseUrl.orNull?.let {
                addAll(listOf("--hostedBaseUrl", it))
            }
        }
    })
    inputs.files(fileTree(projectDir) {
        include(
            "package.json",
            "package-lock.json",
            "tsconfig.json",
            "mkdocs.yml",
            "requirements-docs.txt",
            "*.ts",
            "documentation/**/*.md",
            "documentation/**/*.mjs",
            "documentation/theme/**",
        )
        exclude("node_modules/**", "*.d.ts")
    })
    outputs.dir(docsOutput.map { file(it) })
}

tasks.assemble {
    dependsOn(compileTypeScript)
}

tasks.clean {
    delete(file("docs"))
    delete(file(".mkdocs-build"))
    delete(fileTree(projectDir) {
        include("*.d.ts", "*.lua")
    })
}
