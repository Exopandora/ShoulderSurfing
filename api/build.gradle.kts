plugins {
	id("java")
	id("idea")
	alias(libs.plugins.vanillagradle)
}

val modName: String by project
val javaVersion: String by project

tasks.withType<Jar>().configureEach {
	archiveBaseName.set("$modName-API")
}

java {
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

dependencies {
	implementation(libs.jsr305)
}

minecraft {
	version(libs.versions.minecraft.get())
}
