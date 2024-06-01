package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

public abstract class PluginLoader
{
	private static final PluginLoader INSTANCE = find(ServiceLoader.load(PluginLoader.class));
	private static final String ENTRYPOINT_KEY = "entrypoint";
	protected static final String PLUGIN_JSON_PATH = "shouldersurfing_plugin.json";
	
	public abstract void loadPlugins();
	
	protected void loadPlugin(String modName, String modId, Path path)
	{
		ShoulderSurfingCommon.LOGGER.info("Registering plugin for {} ({})", modName, modId);
		
		try(Reader reader = Files.newBufferedReader(path))
		{
			JsonObject configuration = new JsonParser().parse(reader).getAsJsonObject();
			
			if(configuration.has(ENTRYPOINT_KEY))
			{
				String entrypoint = configuration.get(ENTRYPOINT_KEY).getAsString();
				IShoulderSurfingPlugin plugin = (IShoulderSurfingPlugin) Class.forName(entrypoint).getConstructor().newInstance();
				plugin.register(ShoulderSurfingRegistrar.getInstance());
			}
			else
			{
				ShoulderSurfingCommon.LOGGER.error("Plugin for {} ({}) does not contain an entrypoint", modName, modId);
			}
		}
		catch(Throwable e)
		{
			ShoulderSurfingCommon.LOGGER.error("Failed to load plugin for {} ({})", modName, modId, e);
		}
	}
	
	public static PluginLoader getInstance()
	{
		return INSTANCE;
	}
	
	private static <S> S find(ServiceLoader<S> serviceLoader)
	{
		Iterator<S> iterator = serviceLoader.iterator();
		
		if(iterator.hasNext())
		{
			return iterator.next();
		}
		
		throw new NoSuchElementException();
	}
}
