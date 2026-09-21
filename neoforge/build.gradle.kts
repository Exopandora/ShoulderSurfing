plugins {
    id("multiloader-modloader")
    alias(libs.plugins.moddevgradle)
}

val modId: String = project.property("modId")!!.toString()
val modName: String = project.property("modName")!!.toString()
val modAuthor: String = project.property("modAuthor")!!.toString()
val modContributors: String = project.property("modContributors")!!.toString()
val modVersion: String = project.property("modVersion")!!.toString()
val modDescription: String = project.property("modDescription")!!.toString()
val modUrl: String = project.property("modUrl")!!.toString()
val jarName: String = project.property("jarName")!!.toString()
val neoForgeCompatibleMinecraftVersions: String = project.property("neoForgeCompatibleMinecraftVersions")!!.toString()

base {
    archivesName.set("$jarName-NeoForge")
}

neoForge {
    version = libs.versions.neoforge.get()
    
    runs {
        configureEach {
            gameDirectory = rootProject.layout.projectDirectory.dir("run")
        }
        
        create("client") {
            client()
            ideName = "$modName NeoForge Client"
        }
        
        create("server") {
            server()
            ideName = "$modName NeoForge Server"
            programArgument("--nogui")
        }
    }
    
    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    compileOnly(libs.wthit.neoforge)
    compileOnly(libs.jade.neoforge)
    compileOnly(variantOf(libs.curios.neoforge) { classifier("api") })
    compileOnly(libs.cobblemon.neoforge)
}

tasks.withType<ProcessResources> {
    val properties = mapOf(
        "modVersion" to modVersion,
        "modId" to modId,
        "modName" to modName,
        "modAuthor" to modAuthor,
        "modContributors" to modContributors,
        "modDescription" to modDescription,
        "modUrl" to modUrl,
        "minecraftVersion" to libs.versions.minecraft.get()
    )
    
    inputs.properties(properties)
    
    filesMatching(listOf("pack.mcmeta", "META-INF/neoforge.mods.toml", "**/lang/*.json")) {
        expand(properties)
    }
}

publishMods {
    displayName = "$jarName-NeoForge-${libs.versions.minecraft.get()}-$modVersion"
    version = "${project.version}+neoforge"
    file = tasks.named<Jar>("jar").get().archiveFile
    modLoaders.add("neoforge")
    
    val compatibleVersions = neoForgeCompatibleMinecraftVersions.split(",")
    
    curseforge {
        minecraftVersions.set(compatibleVersions)
        incompatible("better-third-person", "nimble", "ydms-custom-camera-view")
    }
    
    modrinth {
        minecraftVersions.set(compatibleVersions)
        incompatible("better-third-person", "nimble", "ydms-custom-camera-view")
    }
}
