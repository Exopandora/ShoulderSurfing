package com.teamderpy.shouldersurfing.plugin;

import java.nio.file.Files;
import java.nio.file.Path;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;

public class PluginLoaderForge extends PluginLoader
{
	@Override
	public void loadPlugins()
	{
		for(IModFileInfo modFileInfo : ModList.get().getModFiles())
		{
			Path path = modFileInfo.getFile().findResource(PLUGIN_JSON_PATH);
			
			if(Files.exists(path))
			{
				this.loadPlugin(modFileInfo.getMods().get(0).getModId(), path);
			}
		}
	}
}
