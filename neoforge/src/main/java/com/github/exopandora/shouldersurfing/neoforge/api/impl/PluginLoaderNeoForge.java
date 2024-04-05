package com.github.exopandora.shouldersurfing.neoforge.api.impl;

import java.nio.file.Files;
import java.nio.file.Path;

import com.github.exopandora.shouldersurfing.api.impl.PluginLoader;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModFileInfo;

public class PluginLoaderNeoForge extends PluginLoader
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
