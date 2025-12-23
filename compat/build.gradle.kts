plugins {
    id("multiloader-common")
    alias(libs.plugins.moddevgradle)
}

val jarName: String by project

base {
    archivesName.set("$jarName-Compat")
}

sourceSets {
    main {
        java {
            srcDirs(
                "src/createCommon/java",
                "src/create-6.0.0/java",
                "src/create-0.5.0/java",
                "src/createFly/java",
            )
        }
    }
}

neoForge {
    neoFormVersion = libs.versions.neoform.get()
}
