@file:Suppress("UnstableApiUsage")

plugins {
    id("multiloader-modloader")
    alias(libs.plugins.forgegradle)
}

val modId: String = project.property("modId")!!.toString()
val modName: String = project.property("modName")!!.toString()
val modAuthor: String = project.property("modAuthor")!!.toString()
val modContributors: String = project.property("modContributors")!!.toString()
val modVersion: String = project.property("modVersion")!!.toString()
val modDescription: String = project.property("modDescription")!!.toString()
val modUrl: String = project.property("modUrl")!!.toString()
val modIssueUrl: String = project.property("modIssueUrl")!!.toString()
val forgeCompatibleMinecraftVersions: String = project.property("forgeCompatibleMinecraftVersions")!!.toString()
val jarName: String = project.property("jarName")!!.toString()

base {
    archivesName.set("$jarName-Forge")
}

minecraft {
    mappings("official", libs.versions.minecraft.get())
    
    runs {
        configureEach {
            workingDir = rootProject.layout.projectDirectory.dir("run")
            systemProperty("eventbus.api.strictRuntimeChecks", "true")
            
            args(
                "-mixin.config=$modId.common.mixins.json",
                "-mixin.config=$modId.common.compat.mixins.json",
                "-mixin.config=$modId.forge.mixins.json",
                "-mixin.config=$modId.forge.compat.mixins.json",
            )
        }
        
        register("client")
        
        register("server") {
            args("--nogui")
        }
    }
}

repositories {
    minecraft.mavenizer(this)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
}

dependencies {
    // version catalog does not work with FG7
    implementation(minecraft.dependency("net.minecraftforge:forge:${libs.versions.forge.get()}"))
    annotationProcessor(libs.eventbus.validator)
    implementation(libs.forgeconfigapiport.forge) {
        exclude(group = "io.github.llamalad7")
    }
    compileOnly(libs.wthit.forge)
    compileOnly(libs.jade.forge)
    compileOnly(libs.cobblemon.common)
    compileOnly(libs.iris.common)
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
            "MixinConfigs" to listOf(
                "$modId.common.mixins.json",
                "$modId.common.compat.mixins.json",
                "$modId.forge.mixins.json",
                "$modId.forge.compat.mixins.json"
            ).joinToString(",")
        ))
    }
}

tasks.named<ProcessResources>("processResources") {
    val properties = mapOf(
        "modVersion" to modVersion,
        "modId" to modId,
        "modName" to modName,
        "modAuthor" to modAuthor,
        "modContributors" to modContributors,
        "modDescription" to modDescription,
        "modUrl" to modUrl,
        "modIssueUrl" to modIssueUrl,
        "minecraftVersion" to libs.versions.minecraft.get()
    )
    
    inputs.properties(properties)
    
    filesMatching(listOf("pack.mcmeta", "META-INF/mods.toml", "**/lang/*.json")) {
        expand(properties)
    }
}

publishMods {
    displayName = "$jarName-Forge-${libs.versions.minecraft.get()}-$modVersion"
    version = "${project.version}+forge"
    file = tasks.named<Jar>("jar").get().archiveFile
    modLoaders.add("forge")
    
    val compatibleVersions = forgeCompatibleMinecraftVersions.split(",")
    
    curseforge {
        minecraftVersions.set(compatibleVersions)
        requires("forge-config-api-port")
        incompatible("better-third-person", "nimble", "ydms-custom-camera-view")
    }
    
    modrinth {
        minecraftVersions.set(compatibleVersions)
        requires("forge-config-api-port")
        incompatible("better-third-person", "nimble", "ydms-custom-camera-view")
    }
}
