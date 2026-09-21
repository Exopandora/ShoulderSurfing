plugins {
    id("multiloader-common")
    alias(libs.plugins.moddevgradle)
}

val jarName: String = project.property("jarName")!!.toString()

base {
    archivesName.set("$jarName-Common")
}

dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":compat"))
    
    compileOnly(libs.mixin)
    compileOnly(libs.forgeconfigapiport.common)
    compileOnly(libs.wthit.common)
    compileOnly(libs.jade.common)
    compileOnly(libs.cobblemon.common)
    compileOnly(libs.iris.common)
}

neoForge {
    neoFormVersion = libs.versions.neoform.get()
}
