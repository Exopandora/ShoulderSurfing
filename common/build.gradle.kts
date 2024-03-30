plugins {
	id("java-library")
	id("idea")
	alias(libs.plugins.fabricloom)
}

val modId: String by project
val modName: String by project
val javaVersion: String by project

base {
	archivesName.set("$modName-Common")
}

java {
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

dependencies {
	minecraft(libs.minecraft.fabric)
	mappings(fileTree("../mapping") { include("**.jar") })
	
	compileOnly(project(":api"))
	compileOnly(project(":compatibility"))
	
	compileOnly(libs.mixin)
	compileOnly(libs.forgeconfigapiport.common)
	
	implementation(libs.jsr305)
	implementation(libs.nightconfig.core)
	implementation(libs.nightconfig.toml)
}

loom {
	@Suppress("UnstableApiUsage")
	mixin {
		defaultRefmapName.set("$modId.refmap.json")
		useLegacyMixinAp.set(false)
	}
}
