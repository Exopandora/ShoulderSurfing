plugins {
    id("multiloader-modloader")
    alias(libs.plugins.fabricloom)
}

val modId: String = project.property("modId")!!.toString()
val modName: String = project.property("modName")!!.toString()
val modAuthor: String = project.property("modAuthor")!!.toString()
val modContributors: String = project.property("modContributors")!!.toString()
val modVersion: String = project.property("modVersion")!!.toString()
val modDescription: String = project.property("modDescription")!!.toString()
val modUrl: String = project.property("modUrl")!!.toString()
val modIssueUrl: String = project.property("modIssueUrl")!!.toString()
val jarName: String = project.property("jarName")!!.toString()
val fabricCompatibleMinecraftVersions: String = project.property("fabricCompatibleMinecraftVersions")!!.toString()

base {
    archivesName.set("$jarName-Fabric")
}

dependencies {
    minecraft(libs.minecraft.fabric)
    
    implementation(libs.fabric.loader)
    implementation(libs.fabric.api)
    implementation(libs.forgeconfigapiport.fabric) {
        exclude(group = libs.fabric.loader.get().group)
        exclude(group = libs.fabric.api.get().group)
    }
    compileOnly(libs.wthit.fabric)
    compileOnly(libs.jade.fabric)
    compileOnly(libs.cobblemon.fabric)
    compileOnly(libs.iris.fabric)
}

loom {
    runs {
        configureEach {
            runDirectory = rootProject.layout.projectDirectory.dir("run")
            generateRunConfig = false
        }
        named("client") {
            client()
            displayName = "$modName Fabric Client"
            programArguments.addAll("--username", "Dev")
        }
        named("server") {
            server()
            displayName = "$modName Fabric Server"
        }
    }
}

tasks.withType<ProcessResources> {
    val contributors = modContributors.replace(", ", """", """")
    val properties = mapOf(
        "modVersion" to modVersion,
        "modId" to modId,
        "modName" to modName,
        "modAuthor" to modAuthor,
        "modContributors" to contributors,
        "modDescription" to modDescription,
        "modUrl" to modUrl,
        "modIssueUrl" to modIssueUrl,
        "minecraftVersion" to libs.versions.minecraft.get()
    )
    
    inputs.properties(properties)
    
    filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "**/lang/*.json")) {
        expand(properties)
    }
}

publishMods {
    displayName = "$jarName-Fabric-${libs.versions.minecraft.get()}-$modVersion"
    version = "${project.version}+fabric"
    file = tasks.named<Jar>("jar").get().archiveFile
    modLoaders.add("fabric")
    
    val compatibleVersions = fabricCompatibleMinecraftVersions.split(",")
    
    curseforge {
        minecraftVersions.set(compatibleVersions)
        requires("fabric-api", "forge-config-api-port")
        incompatible("better-third-person", "nimble-fabric")
    }
    
    modrinth {
        minecraftVersions.set(compatibleVersions)
        requires("fabric-api", "forge-config-api-port")
        incompatible("better-third-person", "nimble")
    }
}
