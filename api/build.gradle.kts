plugins {
	id("multiloader-common")
	alias(libs.plugins.vanillagradle)
}

val jarName: String by project

base {
	archivesName.set("$jarName-API")
}

minecraft {
	version(libs.versions.minecraft.get())
}
