plugins {
    id("multiloader-common")
    alias(libs.plugins.vanillagradle)
}

val jarName: String by project

base {
    archivesName.set("$jarName-LegacyAPI")
}

dependencies {
    compileOnly(project(":api"))
    compileOnly(libs.mixin)
}

minecraft {
    version(libs.versions.minecraft.get())
}
