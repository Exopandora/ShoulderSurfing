package com.github.exopandora.shouldersurfing.neoforge.plugin;

import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
import cpw.mods.jarhandling.JarContents;
import cpw.mods.jarhandling.JarResource;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class PluginLoaderNeoForge extends PluginLoader<JarResource>
{
	@Override
	public void loadPlugins()
	{
		for(IModFileInfo modFileInfo : ModList.get().getModFiles())
		{
			JarResource resource = modFileInfo.getFile().getContents().get(PLUGIN_JSON_PATH);
			
			if(resource != null)
			{
				IModInfo modInfo = modFileInfo.getMods().getFirst();
				this.loadPlugin(modInfo.getDisplayName(), modInfo.getModId(), resource);
			}
		}
		
		this.freeze();
	}
	
	@Override
	protected Reader readConfiguration(JarResource source) throws IOException
	{
		return source.bufferedReader();
	}
}
