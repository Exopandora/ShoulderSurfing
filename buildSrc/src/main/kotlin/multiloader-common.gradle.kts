plugins {
    id("java-library")
    id("idea")
}

val versionCatalog = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

val modName: String by project
val modId: String by project
val modAuthor: String by project
val modVersion: String by project
val javaVersion: String by project
val javaToolchainVersion: String by project
val minecraftVersion = versionCatalog.findVersion("minecraft").get().toString()

version = "$minecraftVersion-$modVersion"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaToolchainVersion)
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

repositories {
    mavenCentral()
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
            maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
        }
        filter {
            includeGroupAndSubgroups("fuzs")
        }
    }
    maven("https://maven2.bai.lol") {
        content {
            includeGroup("lol.bai")
            includeGroup("mcp.mobius.waila")
        }
    }
    maven("https://www.cursemaven.com") {
        content {
            includeGroup("curse.maven")
        }
    }
    maven("https://maven.theillusivec4.top/") {
        content {
            includeGroup("top.theillusivec4.curios")
        }
    }
}

tasks.withType<Jar>().configureEach {
    manifest {
        attributes(mapOf(
            "Specification-Title" to modName,
            "Specification-Vendor" to modAuthor,
            "Specification-Version" to modVersion,
            "Implementation-Title" to project.name,
            "Implementation-Version" to "$minecraftVersion-$modVersion",
            "Implementation-Vendor" to modAuthor
        ))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(JavaLanguageVersion.of(javaVersion).asInt())
}

tasks.withType<Javadoc> {
    with(options as StandardJavadocDocletOptions) {
        addStringOption("Xdoclint:none", "-quiet")
    }
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}
