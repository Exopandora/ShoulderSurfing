import net.fabricmc.loom.task.RemapJarTask

plugins {
	id("java")
	id("idea")
	id("fabric-loom")
	id("me.modmuss50.mod-publish-plugin")
}

val modId: String by project
val modName: String by project
val modVersion: String by project
val javaVersion: String by project
val minecraftVersion: String by project
val fabricLoaderVersion: String by project
val fabricVersion: String by project
val forgeconfigapiportVersion: String by project
val wthitVersionFabric: String by project
val badpacketsVersion: String by project

base {
	archivesName.set("$modName-Fabric")
}

java {
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

sourceSets {
	named("main") {
		java {
			compileClasspath += project(":common").sourceSets.named("api").get().output
		}
	}
}

dependencies {
	implementation(project(":api"))
	implementation(project(":common"))
	
	minecraft("com.mojang:minecraft:${minecraftVersion}")
	mappings(loom.officialMojangMappings())
	
	modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
	modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
	modImplementation("net.minecraftforge:forgeconfigapiport-fabric:$forgeconfigapiportVersion") {
		exclude(group = "net.fabricmc.fabric-api")
	}
	modImplementation("mcp.mobius.waila:wthit:fabric-$wthitVersionFabric")
	modImplementation("lol.bai:badpackets:fabric-$badpacketsVersion")
	implementation("com.electronwill.night-config:core:3.6.5")
	implementation("com.electronwill.night-config:toml:3.6.5")
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
	source(project(":api").sourceSets.named("main").get().allSource)
	source(project(":common").sourceSets.named("main").get().allSource)
}

tasks.named<ProcessResources>("processResources").configure {
	from(project(":common").sourceSets.named("main").get().resources)
	
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
	from(project(":api").sourceSets.named("main").get().output)
	from(project(":api").sourceSets.named("main").get().allSource)
	
	from(sourceSets.named("main").get().resources) {
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
	displayName = "$modName-Fabric-$minecraftVersion-$modVersion"
	file = tasks.named<RemapJarTask>("remapJar").get().archiveFile
	additionalFiles.from(tasks.named("apiJar").get())
	changelog = provider { file("../changelog.txt").readText() }
	modLoaders.add("fabric")
	type = STABLE
	
	val compatibleVersions = provider {
		findProperty("fabric_compatible_minecraft_versions").toString().split(",")
	}
	
	curseforge {
		projectId = findProperty("curse_project_id").toString()
		accessToken = findProperty("curse_api_key").toString()
		minecraftVersions.set(compatibleVersions)
		javaVersions.add(JavaVersion.toVersion(javaVersion))
		clientRequired = true
		serverRequired = false
		requires("fabric-api", "forge-config-api-port-fabric")
	}
	
	modrinth {
		projectId = findProperty("modrinth_project_id").toString()
		accessToken = findProperty("modrinth_api_key").toString()
		minecraftVersions.set(compatibleVersions)
		requires("fabric-api", "forge-config-api-port")
	}
}
