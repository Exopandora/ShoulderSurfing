plugins {
	id("java-library")
	id("idea")
	alias(libs.plugins.vanillagradle)
}

val modName: String by project
val javaVersion: String by project
val minecraftVersion: String by project

base {
	archivesName.set("$modName-Common")
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
	compileOnly(libs.jade.common)
	
	implementation(libs.jsr305)
	implementation(libs.nightconfig.core)
	implementation(libs.nightconfig.toml)
}

minecraft {
	version(libs.versions.minecraft.get())
}
