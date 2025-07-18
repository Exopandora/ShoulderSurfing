plugins {
	id("multiloader-modloader")
	alias(libs.plugins.moddevgradle)
}

val modId: String by project
val modName: String by project
val modAuthor: String by project
val modContributors: String by project
val modVersion: String by project
val modDescription: String by project
val modUrl: String by project
val jarName: String by project
val neoForgeCompatibleMinecraftVersions: String by project

base {
	archivesName.set("$jarName-NeoForge")
}

neoForge {
	version = libs.versions.neoforge.get()
	
	runs {
		configureEach {
			gameDirectory = file("../run")
		}
		
		create("client") {
			client()
		}
		
		create("server") {
			server()
			programArgument("--nogui")
		}
	}
	
	mods {
		create(modId) {
			sourceSet(sourceSets.main.get())
		}
	}
}

dependencies {
	compileOnly(libs.wthit.neoforge)
	compileOnly(libs.badpackets.neoforge)
	compileOnly(libs.jade.neoforge)
	compileOnly(variantOf(libs.curios.neoforge) { classifier("api") })
}

tasks.withType<ProcessResources> {
	val properties = mapOf(
		"modVersion" to modVersion,
		"modId" to modId,
		"modName" to modName,
		"modAuthor" to modAuthor,
		"modContributors" to modContributors,
		"modDescription" to modDescription,
		"modUrl" to modUrl,
		"minecraftVersion" to libs.versions.minecraft.get()
	)
	
	inputs.properties(properties)
	
	filesMatching(listOf("pack.mcmeta", "META-INF/neoforge.mods.toml", "**/lang/*.json")) {
		expand(properties)
	}
}

publishMods {
	displayName = "$jarName-NeoForge-${libs.versions.minecraft.get()}-$modVersion"
	version = "${project.version}+neoforge"
	file = tasks.named<Jar>("jar").get().archiveFile
	modLoaders.add("neoforge")
	
	val compatibleVersions = neoForgeCompatibleMinecraftVersions.split(",")
	
	curseforge {
		minecraftVersions.set(compatibleVersions)
		incompatible("better-third-person", "nimble", "valkyrien-skies", "ydms-custom-camera-view")
	}
	
	modrinth {
		minecraftVersions.set(compatibleVersions)
		incompatible("better-third-person", "nimble", "valkyrien-skies", "ydms-custom-camera-view")
	}
}
