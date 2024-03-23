plugins {
	id("java")
	id("org.spongepowered.gradle.vanilla") version("0.2.1-SNAPSHOT")
}

val modName: String by project
val javaVersion: String by project
val minecraftVersion: String by project

tasks.withType<Jar>().configureEach {
	archiveBaseName.set("$modName-API")
}

java {
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

dependencies {
	implementation("com.google.code.findbugs:jsr305:3.0.2")
}

minecraft {
	version(minecraftVersion)
}
