import net.fabricmc.loom.task.RemapJarTask

plugins {
	id("multiloader-modloader")
	alias(libs.plugins.fabricloom)
}

val modId: String by project
val modName: String by project
val modAuthor: String by project
val modContributors: String by project
val modVersion: String by project
val modDescription: String by project
val modUrl: String by project
val jarName: String by project
val fabricCompatibleMinecraftVersions: String by project

base {
	archivesName.set("$jarName-Fabric")
}

dependencies {
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
	modImplementation(libs.jade.fabric)
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

tasks.withType<ProcessResources> {
	val contributors = modContributors.replace(", ", """", """")
	val properties = mapOf(
		"modVersion" to modVersion,
		"modId" to modId,
		"modName" to modName,
		"modAuthor" to modAuthor,
		"modContributors" to contributors,
		"modDescription" to modDescription,
		"modUrl" to modUrl,
		"minecraftVersion" to libs.versions.minecraft.get()
	)
	
	inputs.properties(properties)
	
	filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "**/lang/*.json")) {
		expand(properties)
	}
}

publishMods {
	displayName = "$jarName-Fabric-${libs.versions.minecraft.get()}-$modVersion"
	version = "${project.version}+fabric"
	file = tasks.named<RemapJarTask>("remapJar").get().archiveFile
	modLoaders.add("fabric")
	
	val compatibleVersions = fabricCompatibleMinecraftVersions.split(",")
	
	curseforge {
		minecraftVersions.set(compatibleVersions)
		requires("fabric-api", "forge-config-api-port-fabric")
		incompatible("better-third-person", "nimble-fabric", "valkyrien-skies")
	}
	
	modrinth {
		minecraftVersions.set(compatibleVersions)
		requires("fabric-api", "forge-config-api-port")
		incompatible("better-third-person", "nimble", "valkyrien-skies")
	}
}
