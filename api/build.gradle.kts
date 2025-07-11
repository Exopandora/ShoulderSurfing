plugins {
	id("multiloader-common")
	alias(libs.plugins.moddevgradle)
}

val jarName: String by project

base {
	archivesName.set("$jarName-API")
}

neoForge {
	neoFormVersion = libs.versions.neoform.get()
}
