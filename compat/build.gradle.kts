plugins {
	id("multiloader-common")
	alias(libs.plugins.vanillagradle)
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
				"src/iris-1.7.0/java",
				"src/iris-1.6.15/java"
			)
		}
	}
}

minecraft {
	version(libs.versions.minecraft.get())
}
