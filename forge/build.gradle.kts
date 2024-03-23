plugins {
	id("java")
	id("idea")
	id("net.minecraftforge.gradle") version("[6.0.16,6.2)")
	id("org.spongepowered.mixin") version("0.7.+")
	id("me.modmuss50.mod-publish-plugin")
}

repositories {
	maven("https://maven.minecraftforge.net/")
}

val modId: String by project
val modName: String by project
val modVersion: String by project
val javaVersion: String by project
val minecraftVersion: String by project
val forgeVersion: String by project
val forgeCompatibleMinecraftVersions: String by project
val mixinVersion: String by project
val wthitVersionForge: String by project
val badpacketsVersion: String by project
val curseProjectId: String by project
val modrinthProjectId: String by project

tasks.withType<Jar>().configureEach {
	archiveBaseName.set("$modName-Forge")
}

java {
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

sourceSets {
	create("api") {
		java {
			compileClasspath += project(":common").sourceSets.named("main").get().compileClasspath
		}
	}
	named("main") {
		java {
			compileClasspath += sourceSets.named("api").get().output
			compileClasspath += project(":common").sourceSets.named("api").get().output
			runtimeClasspath += sourceSets.named("api").get().output
		}
	}
}

mixin {
	add(sourceSets.named("main").get(), "$modId.refmap.json")
	
	config("$modId.common.mixins.json")
	config("$modId.forge.mixins.json")
	config("$modId.compat.oculus.mixins.json")
}

minecraft {
	mappings("official", minecraftVersion)
	
	copyIdeResources = true
	
	runs {
		configureEach {
			workingDirectory(project.file("../run"))
			ideaModule("${rootProject.name}.${project.name}.main")

			mods {
				create(modId) {
					source(sourceSets.named("api").get())
					source(sourceSets.named("main").get())
					source(project(":api").sourceSets.named("main").get())
					source(project(":common").sourceSets.named("main").get())
				}
			}
		}
		
		create("client") {
			taskName("${modName}ForgeClient")
		}
		
		create("server") {
			taskName("${modName}ForgeServer")
			args("--nogui")
		}
	}
}

dependencies {
	compileOnly(project(":api"))
	compileOnly(project(":common"))
	
	minecraft("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")
	annotationProcessor("org.spongepowered:mixin:$mixinVersion:processor")
	implementation(fg.deobf("mcp.mobius.waila:wthit:forge-$wthitVersionForge"))
	implementation(fg.deobf("lol.bai:badpackets:forge-$badpacketsVersion"))
}

tasks.named<JavaCompile>("compileJava").configure {
	source(project(":api").sourceSets.named("main").get().allSource)
	source(project(":common").sourceSets.named("main").get().allSource)
}

tasks.named<ProcessResources>("processResources").configure {
	from(project(":common").sourceSets.named("main").get().resources)
	
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
	from(project(":api").sourceSets.named("main").get().output)
	from(project(":api").sourceSets.named("main").get().allSource)
	archiveClassifier = "API"
}

tasks.build {
	finalizedBy("apiJar")
}

tasks.jar {
	finalizedBy("reobfJar")
}

publishMods {
	displayName = "$modName-Forge-$minecraftVersion-$modVersion"
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

sourceSets.forEach {
	val dir = layout.buildDirectory.dir("sourcesSets/${it.name}")
	it.output.setResourcesDir(dir)
	it.java.destinationDirectory.set(dir)
}
