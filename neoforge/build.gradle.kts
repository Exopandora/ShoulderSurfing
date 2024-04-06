plugins {
	id("idea")
	id("java")
	alias(libs.plugins.neogradle)
	alias(libs.plugins.modpublishplugin)
}

repositories {
	maven("https://maven.neoforged.net/releases/")
}

val modId: String by project
val modName: String by project
val modAuthor: String by project
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

java {
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

runs {
	configureEach {
		workingDirectory(project.file("../run"))
		modSource(sourceSets.main.get())
	}
	
	create("client")
	
	create("server") {
		programArgument("--nogui")
	}
}

dependencies {
	compileOnly(project(":api"))
	compileOnly(project(":common"))
	compileOnly(project(":compatibility"))
	
	implementation(libs.minecraft.neoforge)
	implementation(libs.forgeconfigapiport.neoforge)
	implementation(libs.wthit.neoforge.get())
	implementation(libs.badpackets.neoforge.get())
	implementation(libs.jade.neoforge.get())
	
	testCompileOnly(project(":api"))
	testCompileOnly(project(":common"))
	testCompileOnly(project(":compatibility"))
}

val notNeoTask = Spec<Task> { !it.name.startsWith("neo") }

tasks.withType<JavaCompile>().matching(notNeoTask).configureEach {
	source(project(":api").sourceSets.main.get().allSource)
	source(project(":common").sourceSets.main.get().allSource)
}

tasks.withType<ProcessResources>().matching(notNeoTask).configureEach {
	from(project(":common").sourceSets.main.get().resources)
	
	val properties = mapOf(
		"modVersion" to modVersion,
		"modId" to modId,
		"modName" to modName,
		"modAuthor" to modAuthor,
		"modDescription" to modDescription,
		"modUrl" to modUrl,
		"minecraftVersion" to libs.versions.minecraft.get()
	)
	
	inputs.properties(properties)
	
	filesMatching(listOf("pack.mcmeta", "META-INF/mods.toml")) {
		expand(properties)
	}
}

tasks.register<Jar>("apiJar").configure {
	from(project(":api").sourceSets.main.get().output)
	from(project(":api").sourceSets.main.get().allSource)
	archiveClassifier = "API"
}

tasks.build {
	finalizedBy("apiJar")
}

publishMods {
	displayName = "$jarName-NeoForge-${libs.versions.minecraft.get()}-$modVersion"
	file = tasks.named<Jar>("jar").get().archiveFile
	additionalFiles.from(tasks.named("apiJar").get())
	changelog = provider { file("../changelog.txt").readText() }
	modLoaders.add("neoforge")
	type = STABLE
	
	val compatibleVersions = neoForgeCompatibleMinecraftVersions.split(",")
	
	curseforge {
		projectId = curseProjectId
		accessToken = findProperty("curse_api_key").toString()
		minecraftVersions.set(compatibleVersions)
		javaVersions.add(JavaVersion.toVersion(javaVersion))
		clientRequired = true
		serverRequired = false
		requires("forge-config-api-port-fabric")
		incompatible("better-third-person", "cameraoverhaul", "nimble")
	}
	
	modrinth {
		projectId = modrinthProjectId
		accessToken = findProperty("modrinth_api_key").toString()
		minecraftVersions.set(compatibleVersions)
		requires("forge-config-api-port")
		incompatible("better-third-person", "cameraoverhaul", "nimble")
	}
}
