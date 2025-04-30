import net.fabricmc.loom.task.RemapJarTask

plugins {
	id("java")
	id("idea")
	alias(libs.plugins.fabricloom)
	alias(libs.plugins.modpublishplugin)
}

repositories {
	exclusiveContent {
		forRepository {
			maven("https://api.modrinth.com/maven")
		}
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
val javaVersion: String by project
val jarName: String by project
val fabricCompatibleMinecraftVersions: String by project
val curseProjectId: String by project
val modrinthProjectId: String by project

base {
	archivesName.set("$jarName-Fabric")
}

java {
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

dependencies {
	compileOnly(project(":api"))
	compileOnly(project(":common"))
	compileOnly(project(":compat"))
	
	minecraft(libs.minecraft.fabric)
	mappings(loom.officialMojangMappings())
	
	modImplementation(libs.fabric.loader)
	modImplementation(libs.fabric.api)
	modImplementation(libs.forgeconfigapiport.fabric) {
		exclude(group = libs.fabric.loader.get().group)
		exclude(group = libs.fabric.api.get().group)
	}
	modCompileOnly(libs.wthit.fabric)
	modCompileOnly(libs.badpackets.fabric)
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

tasks.named<JavaCompile>("compileJava") {
	source(project(":api").sourceSets.main.get().allSource)
	source(project(":common").sourceSets.main.get().allSource)
}

tasks.withType<ProcessResources> {
	from(project(":common").sourceSets.main.get().resources)
	
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

tasks.register<Jar>("apiJar") {
	from(project(":api").sourceSets.main.get().output)
	from(project(":api").sourceSets.main.get().allSource)
	archiveClassifier = "API"
}

tasks.build {
	finalizedBy("apiJar")
}

publishMods {
	displayName = "$jarName-Fabric-${libs.versions.minecraft.get()}-$modVersion"
	version = "${rootProject.version}+fabric"
	file = tasks.named<RemapJarTask>("remapJar").get().archiveFile
	additionalFiles.from(tasks.named("apiJar").get())
	changelog = provider { file("../changelog.txt").readText() }
	modLoaders.add("fabric")
	type = STABLE
	
	val compatibleVersions = fabricCompatibleMinecraftVersions.split(",")
	
	curseforge {
		projectId = curseProjectId
		accessToken = System.getenv("CURSE_API_KEY")
		minecraftVersions.set(compatibleVersions)
		javaVersions.add(JavaVersion.toVersion(javaVersion))
		clientRequired = true
		serverRequired = false
		requires("fabric-api", "forge-config-api-port-fabric")
		incompatible("better-third-person", "nimble-fabric", "valkyrien-skies")
	}
	
	modrinth {
		projectId = modrinthProjectId
		accessToken = System.getenv("MODRINTH_API_KEY")
		minecraftVersions.set(compatibleVersions)
		requires("fabric-api", "forge-config-api-port")
		incompatible("better-third-person", "nimble", "valkyrien-skies")
	}
}
