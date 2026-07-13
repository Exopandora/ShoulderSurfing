package com.github.exopandora.shouldersurfing.forge.plugin;

import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
import net.minecraftforge.fml.ModList;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class PluginLoaderForge extends PluginLoader<Path> {
	@Override
	public void loadPlugins() {
		for (var modFileInfo : ModList.get().getModFiles()) {
			var path = modFileInfo.getFile().findResource(PLUGIN_JSON_PATH);
			if (Files.exists(path)) {
				var modInfo = modFileInfo.getMods().get(0);
				this.loadPlugin(modInfo.getDisplayName(), modInfo.getModId(), path);
			}
		}
	}
	
	@Override
	protected Reader readConfiguration(Path source) throws IOException {
		return Files.newBufferedReader(source);
	}
}
