plugins {
    `kotlin-dsl`
    alias(libs.plugins.modpublishplugin)
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(plugin(libs.plugins.modpublishplugin))
}

fun DependencyHandlerScope.plugin(plugin: Provider<PluginDependency>) =
    plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
