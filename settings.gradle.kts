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
		exclusiveContent {
			forRepository {
				maven("https://maven.neoforged.net/releases/")
			}
			filter {
				includeGroupByRegex("net\\.neoforged.*")
				includeGroup("codechicken")
				includeGroup("net.covers1624")
			}
		}
		gradlePluginPortal()
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}

rootProject.name = "ShoulderSurfing"

include(
	":api",
	":common",
	":compat",
	":forge",
	":neoforge",
	":fabric"
)

// Explicitly configure the build cache
buildCache {
    local {
        directory = rootDir.resolve(".build-cache")
    }
}