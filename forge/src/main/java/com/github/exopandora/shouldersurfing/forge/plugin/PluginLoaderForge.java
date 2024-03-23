package com.github.exopandora.shouldersurfing.forge.plugin;

import java.nio.file.Files;
import java.nio.file.Path;

import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;

public class PluginLoaderForge extends PluginLoader
{
	@Override
	public void loadPlugins()
	{
		for(ModFileInfo modFileInfo : ModList.get().getModFiles())
		{
			Path path = modFileInfo.getFile().findResource(PLUGIN_JSON_PATH);
			
			if(Files.exists(path))
			{
				this.loadPlugin(modFileInfo.getMods().get(0).getModId(), path);
			}
		}
	}
}
