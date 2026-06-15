package com.github.exopandora.shouldersurfing.neoforge.plugin;

import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.IModInfo;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class PluginLoaderNeoForge extends PluginLoader<Path> {
	@Override
	public void loadPlugins() {
		for (IModFileInfo modFileInfo : ModList.get().getModFiles()) {
			Path resource = modFileInfo.getFile().findResource(PLUGIN_JSON_PATH);
			if (Files.exists(resource)) {
				IModInfo modInfo = modFileInfo.getMods().getFirst();
				this.loadPlugin(modInfo.getDisplayName(), modInfo.getModId(), resource);
			}
		}
	}
	
	@Override
	protected Reader readConfiguration(Path source) throws IOException {
		return Files.newBufferedReader(source);
	}
}
