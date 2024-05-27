pluginManagement {
	repositories {
		exclusiveContent {
			forRepository {
				maven("https://maven.fabricmc.net/")
			}
			filter {
				includeGroup("net.fabricmc")
				includeGroup("fabric-loom")
			}
		}
		exclusiveContent {
			forRepository {
				maven("https://repo.spongepowered.org/repository/maven-public/")
			}
			filter {
				includeGroupByRegex("org\\.spongepowered.*")
			}
		}
		exclusiveContent {
			forRepository {
				maven("https://maven.minecraftforge.net/")
			}
			filter {
				includeGroupByRegex("net\\.minecraftforge.*")
			}
		}
		gradlePluginPortal()
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version("0.5.0")
}

rootProject.name = "ShoulderSurfing"
include(
	":api",
	":common",
	":compat",
	":forge",
	":fabric"
)
