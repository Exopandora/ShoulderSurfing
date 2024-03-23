plugins {
	id("java-library")
	id("fabric-loom")
}

val modId: String by project
val modName: String by project
val javaVersion: String by project
val minecraftVersion: String by project
val mixinVersion: String by project
val forgeconfigapiportVersion: String by project

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
	minecraft("com.mojang:minecraft:${minecraftVersion}")
	mappings(fileTree("../mapping") { include("**.jar") })
	
	compileOnly(project(":api"))
	
	compileOnly("org.spongepowered:mixin:$mixinVersion")
	compileOnly("net.minecraftforge:forgeconfigapiport-fabric:$forgeconfigapiportVersion")
	
	implementation("com.google.code.findbugs:jsr305:3.0.2")
	implementation("com.electronwill.night-config:core:3.6.3")
	implementation("com.electronwill.night-config:toml:3.6.3")
}

loom {
	@Suppress("UnstableApiUsage")
	mixin {
		defaultRefmapName.set("$modId.refmap.json")
		useLegacyMixinAp.set(false)
	}
}