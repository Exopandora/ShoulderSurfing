plugins {
	id("multiloader-common")
	alias(libs.plugins.moddevgradle)
}

val jarName: String by project

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
}

neoForge {
	neoFormVersion = libs.versions.neoform.get()
}
