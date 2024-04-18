package com.github.exopandora.shouldersurfing.forge.api.impl;

import java.nio.file.Files;
import java.nio.file.Path;

import com.github.exopandora.shouldersurfing.api.impl.PluginLoader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;

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
				IModInfo modInfo = modFileInfo.getMods().get(0);
				this.loadPlugin(modInfo.getDisplayName(), modInfo.getModId(), path);
			}
		}
	}
}
