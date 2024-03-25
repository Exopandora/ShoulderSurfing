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
val modVersion: String by project
val javaVersion: String by project
val neoForgeCompatibleMinecraftVersions: String by project
val curseProjectId: String by project
val modrinthProjectId: String by project

base {
	archivesName.set("$modName-NeoForge")
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

val notNeoTask = Spec<Task> { !it.name.startsWith("neo") }

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

tasks.withType<JavaCompile>().matching(notNeoTask).configureEach {
	source(project(":api").sourceSets.main.get().allSource)
	source(project(":common").sourceSets.main.get().allSource)
}

tasks.withType<ProcessResources>().matching(notNeoTask).configureEach {
	from(project(":common").sourceSets.main.get().resources)
	
	inputs.property("mod_version", modVersion)
	inputs.property("mod_name", modName)
	
	filesMatching("META-INF/mods.toml") {
		expand(mapOf("version" to modVersion))
	}
	
	filesMatching("pack.mcmeta") {
		expand(mapOf("mod_name" to modName))
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
	displayName = "$modName-Forge-${libs.versions.minecraft.get()}-$modVersion"
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
	}
	
	modrinth {
		projectId = modrinthProjectId
		accessToken = findProperty("modrinth_api_key").toString()
		minecraftVersions.set(compatibleVersions)
		requires("forge-config-api-port")
	}
}
