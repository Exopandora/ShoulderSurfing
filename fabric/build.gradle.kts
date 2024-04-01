import net.fabricmc.loom.task.RemapJarTask

plugins {
	id("java")
	id("idea")
	alias(libs.plugins.fabricloom)
	alias(libs.plugins.modpublishplugin)
}

val modId: String by project
val modName: String by project
val modVersion: String by project
val javaVersion: String by project
val fabricCompatibleMinecraftVersions: String by project
val curseProjectId: String by project
val modrinthProjectId: String by project

base {
	archivesName.set("$modName-Fabric")
}

java {
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

dependencies {
	compileOnly(project(":api"))
	compileOnly(project(":common"))
	compileOnly(project(":compatibility"))
	
	minecraft(libs.minecraft.fabric)
	mappings(loom.officialMojangMappings())
	
	modImplementation(libs.fabric.loader)
	modImplementation(libs.fabric.api)
	modImplementation(libs.forgeconfigapiport.fabric) {
		exclude(group = libs.fabric.loader.get().group)
		exclude(group = libs.fabric.api.get().group)
	}
	modImplementation(libs.wthit.fabric)
	modImplementation(libs.badpackets.fabric)
	implementation(libs.nightconfig.core)
	implementation(libs.nightconfig.toml)
}

loom {
	@Suppress("UnstableApiUsage")
	mixin {
		defaultRefmapName.set("$modId.refmap.json")
	}
	
	runs {
		named("client") {
			client()
			configName = "$modName Fabric Client"
			ideConfigGenerated(true)
			runDir("../run")
			programArgs("--username", "Dev")
		}
		named("server") {
			server()
			configName = "$modName Fabric Server"
			ideConfigGenerated(true)
			runDir("../run")
		}
	}
}

tasks.named<JavaCompile>("compileJava").configure {
	source(project(":api").sourceSets.main.get().allSource)
	source(project(":common").sourceSets.main.get().allSource)
}

tasks.named<ProcessResources>("processResources").configure {
	from(project(":common").sourceSets.main.get().resources)
	
	inputs.property("version", modVersion)
	inputs.property("mod_name", modName)
	
	filesMatching("fabric.mod.json") {
		expand(mapOf("version" to modVersion))
	}
	
	filesMatching("pack.mcmeta") {
		expand(mapOf("mod_name" to modName))
	}
	
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Jar>("apiJar").configure {
	from(project(":api").sourceSets.main.get().output)
	from(project(":api").sourceSets.main.get().allSource)
	
	from(sourceSets.main.get().resources) {
		include("fabric.mod.json")
	}
	
	inputs.property("version", modVersion)
	
	filesMatching("fabric.mod.json") {
		expand(mapOf("version" to modVersion))
	}
	
	archiveClassifier = "API"
}

tasks.build {
	finalizedBy("apiJar")
}

publishMods {
	displayName = "$modName-Fabric-${libs.versions.minecraft.get()}-$modVersion"
	file = tasks.named<RemapJarTask>("remapJar").get().archiveFile
	additionalFiles.from(tasks.named("apiJar").get())
	changelog = provider { file("../changelog.txt").readText() }
	modLoaders.add("fabric")
	type = STABLE
	
	val compatibleVersions = fabricCompatibleMinecraftVersions.split(",")
	
	curseforge {
		projectId = curseProjectId
		accessToken = findProperty("curse_api_key").toString()
		minecraftVersions.set(compatibleVersions)
		javaVersions.add(JavaVersion.toVersion(javaVersion))
		clientRequired = true
		serverRequired = false
		requires("fabric-api", "forge-config-api-port-fabric")
		incompatible("better-third-person", "cameraoverhaul", "nimble-fabric")
	}
	
	modrinth {
		projectId = modrinthProjectId
		accessToken = findProperty("modrinth_api_key").toString()
		minecraftVersions.set(compatibleVersions)
		requires("fabric-api", "forge-config-api-port")
		incompatible("better-third-person", "cameraoverhaul", "nimble")
	}
}
