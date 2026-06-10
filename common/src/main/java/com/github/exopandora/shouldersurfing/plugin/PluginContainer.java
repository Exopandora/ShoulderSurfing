package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;

public record PluginContainer(String modName, String modId, IShoulderSurfingPlugin instance, String entrypoint) {
}
