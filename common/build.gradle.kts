plugins {
	id("multiloader-common")
	alias(libs.plugins.vanillagradle)
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

base {
	archivesName.set("$jarName-Common")
}

dependencies {
	compileOnly(project(":api"))
	
	compileOnly(libs.mixin)
	compileOnly(libs.forgeconfigapiport.common)
	compileOnly(libs.wthit.common)
	compileOnly(libs.create.common)
	
	implementation(libs.nightconfig.core)
	implementation(libs.nightconfig.toml)
}

minecraft {
	version(libs.versions.minecraft.get())
}
