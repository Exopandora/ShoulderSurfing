import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

plugins {
	id("java")
	alias(libs.plugins.modpublishplugin) apply false
	alias(libs.plugins.fabricloom) apply false
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
				maven("https://repo.spongepowered.org/repository/maven-public/")
			}
			filter {
				includeGroup("org.spongepowered")
			}
		}
		maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") {
			content {
				includeGroupByRegex("net\\.minecraftforge.*")
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
				"Implementation-Vendor" to modAuthor,
				"Implementation-Timestamp" to DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(OffsetDateTime.now()),
				"Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})",
				"Build-On-Minecraft" to libs.versions.minecraft.get()
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