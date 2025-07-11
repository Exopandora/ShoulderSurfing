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
    from(project(":api").sourceSets.main.get().allSource)
    archiveClassifier = "API"
}

tasks.build {
    finalizedBy("apiJar")
}
