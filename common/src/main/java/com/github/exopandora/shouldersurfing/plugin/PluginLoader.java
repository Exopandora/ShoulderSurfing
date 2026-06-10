package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

public abstract class PluginLoader<T> {
	private static final PluginLoader<?> INSTANCE = ServiceLoader.load(PluginLoader.class).findFirst().orElseThrow();
	private static final String ENTRYPOINT_KEY = "entrypoint";
	protected static final String PLUGIN_JSON_PATH = "shouldersurfing_plugin.json";
	
	private final List<PluginContainer> plugins = new LinkedList<PluginContainer>();
	
	public abstract void loadPlugins();
	
	protected void loadPlugin(String modName, String modId, T source) {
		ShoulderSurfingCommon.LOGGER.info("Registering plugin for {} ({})", modName, modId);
		try (Reader reader = this.readConfiguration(source)) {
			JsonObject configuration = JsonParser.parseReader(reader).getAsJsonObject();
			if (configuration.has(ENTRYPOINT_KEY)) {
				String entrypoint = configuration.get(ENTRYPOINT_KEY).getAsString();
				IShoulderSurfingPlugin plugin = (IShoulderSurfingPlugin) Class.forName(entrypoint).getConstructor().newInstance();
				this.plugins.add(new PluginContainer(modName, modId, plugin));
			} else {
				ShoulderSurfingCommon.LOGGER.error("Plugin for {} ({}) does not contain an entrypoint", modName, modId);
			}
		} catch (Throwable e) {
			ShoulderSurfingCommon.LOGGER.error("Failed to load plugin for {} ({})", modName, modId, e);
		}
	}
	
	protected abstract Reader readConfiguration(T source) throws IOException;
	
	public List<PluginContainer> getPluginContainers() {
		return this.plugins;
	}
	
	public static PluginLoader<?> getInstance() {
		return INSTANCE;
	}
}
