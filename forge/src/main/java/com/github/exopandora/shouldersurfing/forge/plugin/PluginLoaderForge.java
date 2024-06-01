package com.github.exopandora.shouldersurfing.forge.plugin;

import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;

import java.nio.file.Files;
import java.nio.file.Path;

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
				IModInfo modInfo = modFileInfo.getMods().get(0);
				this.loadPlugin(modInfo.getDisplayName(), modInfo.getModId(), path);
			}
		}
	}
}
