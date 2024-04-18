package com.github.exopandora.shouldersurfing.api.impl;

import com.github.exopandora.shouldersurfing.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.IShoulderSurfingPlugin;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ServiceLoader;

public abstract class PluginLoader
{
	private static final PluginLoader INSTANCE = ServiceLoader.load(PluginLoader.class).findFirst().orElseThrow();
	private static final String ENTRYPOINT_KEY = "entrypoint";
	protected static final String PLUGIN_JSON_PATH = "shouldersurfing_plugin.json";
	
	public abstract void loadPlugins();
	
	protected void loadPlugin(String modName, String modId, Path path)
	{
		ShoulderSurfing.LOGGER.info("Registering plugin for {} ({})", modName, modId);
		
		try(Reader reader = Files.newBufferedReader(path))
		{
			JsonObject configuration = JsonParser.parseReader(reader).getAsJsonObject();
			
			if(configuration.has(ENTRYPOINT_KEY))
			{
				String entrypoint = configuration.get(ENTRYPOINT_KEY).getAsString();
				IShoulderSurfingPlugin plugin = (IShoulderSurfingPlugin) Class.forName(entrypoint).getConstructor().newInstance();
				plugin.register(ShoulderSurfingRegistrar.getInstance());
			}
			else
			{
				ShoulderSurfing.LOGGER.error("Plugin for {} ({}) does not contain an entrypoint", modName, modId);
			}
		}
		catch(Throwable e)
		{
			ShoulderSurfing.LOGGER.error("Failed to load plugin for {} ({})", modName, modId, e);
		}
	}
	
	public static PluginLoader getInstance()
	{
		return INSTANCE;
	}
}
