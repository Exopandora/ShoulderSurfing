plugins {
	id("java-library")
	id("idea")
	alias(libs.plugins.vanillagradle)
}

val jarName: String by project
val javaVersion: String by project

base {
	archivesName.set("$jarName-Common")
}

java {
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

dependencies {
	compileOnly(project(":api"))
	compileOnly(project(":compatibility"))
	
	compileOnly(libs.mixin)
	compileOnly(libs.forgeconfigapiport.common)
	compileOnly(libs.wthit.common)
	
	implementation(libs.jsr305)
	implementation(libs.nightconfig.core)
	implementation(libs.nightconfig.toml)
}

minecraft {
	version(libs.versions.minecraft.get())
}
