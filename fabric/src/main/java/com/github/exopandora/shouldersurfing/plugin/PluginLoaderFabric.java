package com.github.exopandora.shouldersurfing.plugin;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class PluginLoaderFabric extends PluginLoader
{
	@Override
	public void loadPlugins()
	{
		for(ModContainer mod : FabricLoader.getInstance().getAllMods())
		{
			mod.findPath(PLUGIN_JSON_PATH).ifPresent(path ->
			{
				this.loadPlugin(mod.getMetadata().getId(), path);
			});
		}
	}
}
