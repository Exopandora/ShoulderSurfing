plugins {
	id("java")
	id("fabric-loom")
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
	minecraft("com.mojang:minecraft:${minecraftVersion}")
	mappings(fileTree("../mapping") { include("**.jar") })
	
	implementation("com.google.code.findbugs:jsr305:3.0.2")
}