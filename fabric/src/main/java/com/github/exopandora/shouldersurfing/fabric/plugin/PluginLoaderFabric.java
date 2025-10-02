package com.github.exopandora.shouldersurfing.fabric.plugin;

import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class PluginLoaderFabric extends PluginLoader<Path>
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
		
		this.freeze();
	}
	
	@Override
	protected Reader readConfiguration(Path source) throws IOException
	{
		return Files.newBufferedReader(source);
	}
}
