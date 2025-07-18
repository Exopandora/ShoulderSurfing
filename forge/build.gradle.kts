plugins {
	id("multiloader-modloader")
	alias(libs.plugins.forgegradle)
	alias(libs.plugins.mixingradle)
}

repositories {
	maven("https://maven.minecraftforge.net/")
	
	exclusiveContent {
		forRepository {
			maven("https://api.modrinth.com/maven")
		}
		forRepositories(fg.repository)
		filter {
			includeGroup("maven.modrinth")
		}
	}
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
	
	runs {
		configureEach {
			workingDirectory(file("../run"))
			ideaModule("${rootProject.name}.${project.name}.main")
			
			mods {
				create(modId) {
					source(sourceSets.main.get())
					source(project(":api").sourceSets.main.get())
					source(project(":common").sourceSets.main.get())
				}
			}
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
	implementation(fg.deobf(libs.wthit.forge.get()))
	implementation(fg.deobf(libs.badpackets.forge.get()))
	implementation(fg.deobf(libs.jade.forge.get()))
	compileOnly(fg.deobf(libs.create.common.get()))
	compileOnly(fg.deobf(libs.curios.forge.get()))
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

tasks.jar {
	finalizedBy("reobfJar")
}

tasks.configureEach {
	when(name) {
		"configureReobfTaskForReobfJar" -> mustRunAfter(tasks.jar)
		"configureReobfTaskForReobfJarJar" -> mustRunAfter(tasks.jarJar)
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
		incompatible("better-third-person", "nimble", "valkyrien-skies", "ydms-custom-camera-view")
	}
	
	modrinth {
		minecraftVersions.set(compatibleVersions)
		incompatible("better-third-person", "nimble", "valkyrien-skies", "ydms-custom-camera-view")
	}
}

sourceSets.forEach {
	val dir = layout.buildDirectory.dir("sourcesSets/${it.name}")
	it.output.setResourcesDir(dir)
	it.java.destinationDirectory.set(dir)
}
