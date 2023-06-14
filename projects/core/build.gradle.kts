@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.vanilla")
    id("site.siredvin.publishing")
}

val modVersion: String by extra
val minecraftVersion: String by extra
val modBaseName: String by extra

baseShaking {
    projectPart.set("common")
    shake()
}

vanillaShaking {
    accessWideners.add("src/main/resources/template-common.accesswidener")
    accessWideners.add("src/main/resources/template.accesswidener")
    shake()
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.cccommon)
    api(libs.bundles.apicommon)
}

publishingShaking {
    shake()
}
