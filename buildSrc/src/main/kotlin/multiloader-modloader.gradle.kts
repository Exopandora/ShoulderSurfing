plugins {
    id("multiloader-common")
}

dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":common"))
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
    finalizedBy("apiSourcesJar")
}

tasks.register<Jar>("apiSourcesJar") {
    from(project(":api").sourceSets.main.get().allSource)
    archiveClassifier = "api-sources"
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allSource)
    from(project(":api").sourceSets.main.get().allSource)
    from(project(":common").sourceSets.main.get().allSource)
    archiveClassifier = "sources"
}

tasks.build {
    finalizedBy("apiJar")
}
