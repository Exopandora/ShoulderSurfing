plugins {
	id("java-library")
	id("org.spongepowered.gradle.vanilla") version("0.2.1-SNAPSHOT")
}

val modName: String by project
val javaVersion: String by project
val minecraftVersion: String by project
val mixinVersion: String by project
val forgeconfigapiportVersion: String by project
val wthitVersionApi: String by project
val jadeVersionApi: String by project

base {
	archivesName.set("$modName-Common")
}

java {
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

sourceSets {
	create("api") {
		compileClasspath += sourceSets.named("main").get().compileClasspath
	}
	named("main") {
		java {
			compileClasspath += sourceSets.named("api").get().output
			runtimeClasspath += sourceSets.named("api").get().output
		}
	}
}

dependencies {
	compileOnly(project(":api"))
	
	compileOnly("org.spongepowered:mixin:$mixinVersion")
	compileOnly("fuzs.forgeconfigapiport:forgeconfigapiport-common:$forgeconfigapiportVersion")
	compileOnly("mcp.mobius.waila:wthit:mojmap-$wthitVersionApi")
	compileOnly("curse.maven:jade-324717:$jadeVersionApi")
	
	implementation("com.google.code.findbugs:jsr305:3.0.2")
}

minecraft {
	version(minecraftVersion)
}
