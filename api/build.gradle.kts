plugins {
	id("java")
	id("idea")
	alias(libs.plugins.vanillagradle)
}

val jarName: String by project
val javaVersion: String by project

base {
	archivesName.set("$jarName-API")
}

java {
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

idea {
	module {
		isDownloadSources = true
		isDownloadJavadoc = true
	}
}

dependencies {
	implementation(libs.jsr305)
}

minecraft {
	version(libs.versions.minecraft.get())
}
