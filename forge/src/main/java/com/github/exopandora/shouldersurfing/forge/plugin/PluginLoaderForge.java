package com.github.exopandora.shouldersurfing.forge.plugin;

import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class PluginLoaderForge extends PluginLoader<Path>
{
	@Override
	public void loadPlugins()
	{
		for(IModFileInfo modFileInfo : ModList.get().getModFiles())
		{
			Path path = modFileInfo.getFile().findResource(PLUGIN_JSON_PATH);
			
			if(Files.exists(path))
			{
				IModInfo modInfo = modFileInfo.getMods().getFirst();
				this.loadPlugin(modInfo.getDisplayName(), modInfo.getModId(), path);
			}
		}
		
		this.freeze();
	}
	
	@Override
	protected Reader readConfiguration(Path source) throws IOException
	{
		return Files.newBufferedReader(source);
	}
}
