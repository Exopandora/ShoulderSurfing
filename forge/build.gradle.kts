plugins {
	id("java")
	id("idea")
	alias(libs.plugins.forgegradle)
	alias(libs.plugins.mixingradle)
	alias(libs.plugins.modpublishplugin)
}

repositories {
	maven("https://maven.minecraftforge.net/")
}

val modId: String by project
val modName: String by project
val modVersion: String by project
val javaVersion: String by project
val forgeCompatibleMinecraftVersions: String by project
val curseProjectId: String by project
val modrinthProjectId: String by project

base {
	archivesName.set("$modName-Forge")
}

java {
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

mixin {
	add(sourceSets.main.get(), "$modId.refmap.json")
	
	config("$modId.common.mixins.json")
	config("$modId.forge.mixins.json")
}

minecraft {
	mappings("official", libs.versions.minecraft.get())
	
	copyIdeResources = true
	
	runs {
		configureEach {
			workingDirectory(project.file("../run"))
			ideaModule("${rootProject.name}.${project.name}.main")
			
			mods {
				create(modId) {
					source(sourceSets.main.get())
					source(project(":api").sourceSets.main.get())
					source(project(":common").sourceSets.main.get())
					source(project(":compatibility").sourceSets.main.get())
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
	compileOnly(project(":api"))
	compileOnly(project(":common"))
	compileOnly(project(":compatibility"))
	
	minecraft(libs.minecraft.forge)
	annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")
	implementation(libs.jetbrains.annotations)
}

tasks.named<JavaCompile>("compileJava").configure {
	source(project(":api").sourceSets.main.get().allSource)
	source(project(":common").sourceSets.main.get().allSource)
}

tasks.named<ProcessResources>("processResources").configure {
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

tasks.jar {
	finalizedBy("reobfJar")
}

publishMods {
	displayName = "$modName-Forge-${libs.versions.minecraft.get()}-$modVersion"
	file = tasks.named<Jar>("jar").get().archiveFile
	additionalFiles.from(tasks.named("apiJar").get())
	changelog = provider { file("../changelog.txt").readText() }
	modLoaders.add("forge")
	type = STABLE
	
	val compatibleVersions = forgeCompatibleMinecraftVersions.split(",")
	
	curseforge {
		projectId = curseProjectId
		accessToken = findProperty("curse_api_key").toString()
		minecraftVersions.set(compatibleVersions)
		javaVersions.add(JavaVersion.toVersion(javaVersion))
		clientRequired = true
		serverRequired = false
	}
	
	modrinth {
		projectId = modrinthProjectId
		accessToken = findProperty("modrinth_api_key").toString()
		minecraftVersions.set(compatibleVersions)
	}
}
