plugins {
	id("java")
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

sourceSets {
	create("api") {
		compileClasspath += sourceSets.named("main").get().compileClasspath
	}
	named("main") {
		java {
			compileClasspath += sourceSets.named("api").get().output
			runtimeClasspath += sourceSets.named("api").get().output
		}
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
