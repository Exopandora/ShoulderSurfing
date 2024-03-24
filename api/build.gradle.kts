plugins {
	id("java")
	id("idea")
	alias(libs.plugins.fabricloom)
}

val modName: String by project
val javaVersion: String by project

tasks.withType<Jar>().configureEach {
	archiveBaseName.set("$modName-API")
}

java {
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

dependencies {
	minecraft(libs.minecraft.fabric)
	mappings(fileTree("../mapping") { include("**.jar") })
	
	implementation(libs.jsr305)
}
