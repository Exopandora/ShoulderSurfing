plugins {
    id("multiloader-common")
    id("me.modmuss50.mod-publish-plugin")
}

val javaVersion: String by project
val curseProjectId: String by project
val modrinthProjectId: String by project

dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":common"))
    compileOnly(project(":compat"))
}

tasks.named<JavaCompile>("compileJava") {
    source(project(":api").sourceSets.main.get().allSource)
    source(project(":common").sourceSets.main.get().allSource)
}

tasks.named<ProcessResources>("processResources") {
    from(project(":common").sourceSets.main.get().resources)
}

tasks.register<Jar>("apiJar") {
    from(project(":api").sourceSets.main.get().output)
    archiveClassifier = "api"
}

tasks.register<Jar>("apiSourcesJar") {
    from(project(":api").sourceSets.main.get().allSource)
    archiveClassifier = "api-sources"
}

tasks.named<Jar>("sourcesJar") {
    from(project(":api").sourceSets.main.get().allSource)
    from(project(":common").sourceSets.main.get().allSource)
}

tasks.build {
    finalizedBy("apiJar")
    finalizedBy("apiSourcesJar")
}

publishMods {
    additionalFiles.from(
        tasks.named("sourcesJar").get(),
        tasks.named("apiJar").get(),
        tasks.named("apiSourcesJar").get()
    )
    changelog = provider { rootProject.file("changelog.txt").readText() }
    type = STABLE
    
    curseforge {
        projectId = curseProjectId
        accessToken = System.getenv("CURSE_API_KEY")
        javaVersions.add(JavaVersion.toVersion(javaVersion))
        clientRequired = true
        serverRequired = false
    }
    
    modrinth {
        projectId = modrinthProjectId
        accessToken = System.getenv("MODRINTH_API_KEY")
    }
}
