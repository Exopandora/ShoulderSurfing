package com.teamderpy.shouldersurfing.plugin;

import java.util.HashSet;
import java.util.Set;

import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.api.IShoulderSurfingPlugin;

import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;

public class PluginLoader
{
	private static final PluginLoader INSTANCE = new PluginLoader();
	
	private final Set<ASMData> plugins = new HashSet<ASMData>();
	
	private PluginLoader()
	{
		super();
	}
	
	public void registerPlugins(Set<ASMData> plugins)
	{
		this.plugins.addAll(plugins);
	}
	
	public void loadPlugins()
	{
		for(ASMData data : this.plugins)
		{
			this.loadPlugin(data);
		}
		
		this.plugins.clear();
	}
	
	private void loadPlugin(ASMData data)
	{
		try
		{
			IShoulderSurfingPlugin plugin = (IShoulderSurfingPlugin) Class.forName(data.getClassName()).getConstructor().newInstance();
			plugin.register(ShoulderSurfingRegistrar.getInstance());
			ShoulderSurfing.LOGGER.info("Registered plugin " + data.getClassName());
		}
		catch(Exception e)
		{
			ShoulderSurfing.LOGGER.error("Failed to register plugin " + data.getClassName(), e);
		}
	}
	
	public static PluginLoader getInstance()
	{
		return INSTANCE;
	}
}
