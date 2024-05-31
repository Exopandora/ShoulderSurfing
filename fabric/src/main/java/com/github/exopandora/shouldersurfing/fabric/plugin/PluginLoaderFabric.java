package com.github.exopandora.shouldersurfing.fabric.plugin;

import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
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
				this.loadPlugin(mod.getMetadata().getName(), mod.getMetadata().getId(), path);
			});
		}
	}
}
