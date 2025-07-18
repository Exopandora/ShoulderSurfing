plugins {
    id("multiloader-common")
}

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
