plugins {
    id("multiloader-common")
    alias(libs.plugins.moddevgradle)
}

val jarName: String by project

base {
    archivesName.set("$jarName-LegacyAPI")
}

dependencies {
    compileOnly(project(":api"))
    compileOnly(libs.mixin)
}

neoForge {
    neoFormVersion = libs.versions.neoform.get()
}
