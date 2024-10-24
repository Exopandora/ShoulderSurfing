plugins {
	id("java")
	id("idea")
	alias(libs.plugins.moddevgradle)
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

neoForge {
	neoFormVersion = libs.versions.neoform.get()
}
