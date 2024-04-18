package com.github.exopandora.shouldersurfing.neoforge.api.impl;

import com.github.exopandora.shouldersurfing.api.impl.PluginLoader;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.IModInfo;

import java.nio.file.Files;
import java.nio.file.Path;

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
				IModInfo modInfo = modFileInfo.getMods().get(0);
				this.loadPlugin(modInfo.getDisplayName(), modInfo.getModId(), path);
			}
		}
	}
}
