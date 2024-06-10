plugins {
	id("java-library")
	id("idea")
	alias(libs.plugins.fabricloom)
}

val modId: String by project
val jarName: String by project
val javaVersion: String by project

base {
	archivesName.set("$jarName-Common")
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
	minecraft(libs.minecraft.fabric)
	mappings(fileTree("../mapping") { include("**.jar") })
	
	compileOnly(project(":api"))
	
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
