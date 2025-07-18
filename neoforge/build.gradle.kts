plugins {
	id("multiloader-modloader")
	alias(libs.plugins.moddevgradle)
	alias(libs.plugins.modpublishplugin)
}

val modId: String by project
val modName: String by project
val modAuthor: String by project
val modContributors: String by project
val modVersion: String by project
val modDescription: String by project
val modUrl: String by project
val javaVersion: String by project
val jarName: String by project
val neoForgeCompatibleMinecraftVersions: String by project
val curseProjectId: String by project
val modrinthProjectId: String by project

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
	additionalFiles.from(
		tasks.named("sourcesJar").get(),
		tasks.named("apiJar").get(),
		tasks.named("apiSourcesJar").get()
	)
	changelog = provider { file("../changelog.txt").readText() }
	modLoaders.add("neoforge")
	type = STABLE
	
	val compatibleVersions = neoForgeCompatibleMinecraftVersions.split(",")
	
	curseforge {
		projectId = curseProjectId
		accessToken = System.getenv("CURSE_API_KEY")
		minecraftVersions.set(compatibleVersions)
		javaVersions.add(JavaVersion.toVersion(javaVersion))
		clientRequired = true
		serverRequired = false
		incompatible("better-third-person", "nimble", "valkyrien-skies", "ydms-custom-camera-view")
	}
	
	modrinth {
		projectId = modrinthProjectId
		accessToken = System.getenv("MODRINTH_API_KEY")
		minecraftVersions.set(compatibleVersions)
		incompatible("better-third-person", "nimble", "valkyrien-skies", "ydms-custom-camera-view")
	}
}
