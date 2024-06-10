plugins {
	id("java")
	id("idea")
	alias(libs.plugins.fabricloom)
}

val modId: String by project
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
	minecraft(libs.minecraft.fabric)
	mappings(fileTree("../mapping") { include("**.jar") })
	
	compileOnly(libs.mixin)
	
	implementation(libs.jsr305)
}

loom {
	@Suppress("UnstableApiUsage")
	mixin {
		defaultRefmapName.set("$modId.refmap.json")
		useLegacyMixinAp.set(false)
	}
}

