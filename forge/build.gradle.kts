plugins {
	id("multiloader-modloader")
	alias(libs.plugins.forgegradle)
	alias(libs.plugins.mixingradle)
}

val modId: String by project
val modName: String by project
val modAuthor: String by project
val modContributors: String by project
val modVersion: String by project
val modDescription: String by project
val modUrl: String by project
val forgeCompatibleMinecraftVersions: String by project
val jarName: String by project

base {
	archivesName.set("$jarName-Forge")
}

mixin {
	add(sourceSets.main.get(), "$modId.refmap.json")
	
	config("$modId.common.mixins.json")
	config("$modId.common.compat.mixins.json")
	config("$modId.forge.mixins.json")
	config("$modId.forge.compat.mixins.json")
}

minecraft {
	mappings("official", libs.versions.minecraft.get())
	
	copyIdeResources = true
	reobf = false
	
	runs {
		configureEach {
			workingDirectory(file("../run"))
			ideaModule("${rootProject.name}.${project.name}.main")
		}
		
		create("client")
		
		create("server") {
			args("--nogui")
		}
	}
}

dependencies {
	minecraft(libs.minecraft.forge)
	annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")
	implementation(libs.forgeconfigapiport.forge) {
		exclude(group = "io.github.llamalad7")
	}
	implementation(libs.wthit.forge)
	compileOnly(libs.jade.common)
	compileOnly(libs.cobblemon.common)
}

tasks.withType<Jar> {
	manifest {
		attributes(mapOf(
			"MixinConfigs" to listOf(
				"$modId.common.mixins.json",
				"$modId.common.compat.mixins.json",
				"$modId.forge.mixins.json",
				"$modId.forge.compat.mixins.json"
			).joinToString(",")
		))
	}
}

tasks.named<ProcessResources>("processResources") {
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
	
	filesMatching(listOf("pack.mcmeta", "META-INF/mods.toml", "**/lang/*.json")) {
		expand(properties)
	}
}

publishMods {
	displayName = "$jarName-Forge-${libs.versions.minecraft.get()}-$modVersion"
	version = "${project.version}+forge"
	file = tasks.named<Jar>("jar").get().archiveFile
	modLoaders.add("forge")
	
	val compatibleVersions = forgeCompatibleMinecraftVersions.split(",")
	
	curseforge {
		minecraftVersions.set(compatibleVersions)
		requires("forge-config-api-port")
		incompatible("better-third-person", "nimble", "valkyrien-skies", "ydms-custom-camera-view")
	}
	
	modrinth {
		minecraftVersions.set(compatibleVersions)
		requires("forge-config-api-port")
		incompatible("better-third-person", "nimble", "valkyrien-skies", "ydms-custom-camera-view")
	}
}

sourceSets.forEach {
	val dir = layout.buildDirectory.dir("sourcesSets/${it.name}")
	it.output.setResourcesDir(dir)
	it.java.destinationDirectory.set(dir)
}
