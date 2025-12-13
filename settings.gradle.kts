@file:Suppress("UnstableApiUsage")

pluginManagement {
	repositories {
		exclusiveContent {
			forRepository {
				maven("https://maven.fabricmc.net/")
			}
			filter {
				includeGroupAndSubgroups("net.fabricmc")
				includeGroup("fabric-loom")
			}
		}
		exclusiveContent {
			forRepository {
				maven("https://repo.spongepowered.org/repository/maven-public/")
			}
			filter {
				includeGroupAndSubgroups("org.spongepowered")
			}
		}
		exclusiveContent {
			forRepository {
				maven("https://maven.neoforged.net/releases/")
			}
			filter {
				includeGroupAndSubgroups("net.neoforged")
				includeGroup("codechicken")
				includeGroup("net.covers1624")
			}
		}
        maven("https://maven.minecraftforge.net/")
		gradlePluginPortal()
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0")
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
