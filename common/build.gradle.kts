plugins {
	id("java-library")
	id("idea")
	alias(libs.plugins.moddevgradle)
}

repositories {
	exclusiveContent {
		forRepository {
			maven("https://api.modrinth.com/maven")
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}
}

val jarName: String by project
val javaVersion: String by project

base {
	archivesName.set("$jarName-Common")
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

dependencies {
	compileOnly(project(":api"))
	compileOnly(project(":compat"))
	
	compileOnly(libs.mixin)
	compileOnly(libs.forgeconfigapiport.common)
	compileOnly(libs.wthit.common)
	compileOnly(libs.jade.common)
	compileOnly(libs.create.common)
	
	implementation(libs.jsr305)
}

neoForge {
	neoFormVersion = libs.versions.neoform.get()
}
