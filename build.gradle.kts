plugins {
	id("java")
	alias(libs.plugins.idea.ext)
}

val modName: String by project
val modAuthor: String by project
val modVersion: String by project
val javaVersion: String by project
val javaToolchainVersion: String by project

subprojects {
	apply(plugin = "java")
	
	java.toolchain.languageVersion = JavaLanguageVersion.of(javaToolchainVersion)
	
	repositories {
		mavenCentral()
		exclusiveContent {
			forRepository {
				maven("https://maven.fabricmc.net/")
			}
			filter {
				includeGroupByRegex("net\\.fabricmc.*")
				includeGroup("fabric-loom")
			}
		}
		exclusiveContent {
			forRepository {
				maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
			}
			filter {
				includeGroupByRegex("fuzs.*")
			}
		}
		maven("https://maven2.bai.lol") {
			content {
				includeGroup("lol.bai")
				includeGroup("mcp.mobius.waila")
			}
		}
		maven("https://www.cursemaven.com") {
			content {
				includeGroup("curse.maven")
			}
		}
	}
	
	tasks.withType<Jar>().configureEach {
		version = "${libs.versions.minecraft.get()}-$modVersion"
		manifest {
			attributes(mapOf(
				"Specification-Title" to modName,
				"Specification-Vendor" to modAuthor,
				"Specification-Version" to modVersion,
				"Implementation-Title" to project.name,
				"Implementation-Version" to modVersion,
				"Implementation-Vendor" to modAuthor
			))
		}
	}
	
	tasks.withType<JavaCompile>().configureEach {
		options.encoding = "UTF-8"
		options.release.set(JavaLanguageVersion.of(javaVersion).asInt())
	}
	
	tasks.withType<Javadoc> {
		with(options as StandardJavadocDocletOptions) {
			addStringOption("Xdoclint:none", "-quiet")
		}
	}
	
	tasks.withType<AbstractArchiveTask>().configureEach {
		isPreserveFileTimestamps = false
		isReproducibleFileOrder = true
	}
}
