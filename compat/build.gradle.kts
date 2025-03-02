import org.gradle.kotlin.dsl.accessors.runtime.extensionOf

plugins {
	id("java")
	id("idea")
	alias(libs.plugins.moddevgradle)
}

val jarName: String by project
val javaVersion: String by project

base {
	archivesName.set("$jarName-Compat")
}

java {
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

idea {
	module {
		isDownloadSources = true
		isDownloadJavadoc = true
	}
}

sourceSets {
	main {
		java {
			srcDirs(
				"src/createCommon/java",
				"src/create-6.0.0/java",
				"src/create-0.5.0/java",
				"src/iris-1.7.0/java",
				"src/iris-1.6.15/java"
			)
		}
	}
}

dependencies {
	implementation(libs.jsr305)
}

neoForge {
	neoFormVersion = libs.versions.neoform.get()
}
