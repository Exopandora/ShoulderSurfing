package com.teamderpy.shouldersurfing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teamderpy.shouldersurfing.proxy.CommonProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion(ShoulderSurfing.MC_VERSION)
@Mod(modid = ShoulderSurfing.MODID, name = ShoulderSurfing.NAME, acceptedMinecraftVersions = "[" + ShoulderSurfing.MC_VERSION + ",)", version = ShoulderSurfing.VERSION, canBeDeactivated = false, guiFactory = "com.teamderpy.shouldersurfing.gui.GuiShoulderSurfingConfigFactory", certificateFingerprint = ShoulderSurfing.CERTIFICATE)
public class ShoulderSurfing
{
	@SidedProxy(clientSide = "com.teamderpy.shouldersurfing.proxy.ClientProxy", serverSide = "com.teamderpy.shouldersurfing.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static final String NAME = "Shoulder Surfing";
	public static final String MODID = "shouldersurfing";
	public static final String MC_VERSION = "1.7.10";
	public static final String VERSION = "2.2.3";
	public static final String DEVELOPERS = "Joshua Powers, Exopandora (for 1.8+)";
	public static final String CERTIFICATE = "d6261bb645f41db84c74f98e512c2bb43f188af2";
	public static final Logger LOGGER = LogManager.getLogger("Shoulder Surfing");
	
	@Instance(MODID)
	private static ShoulderSurfing instance;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ShoulderSurfing.proxy.preInit(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		ShoulderSurfing.proxy.init(event);
	}
	
	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		ShoulderSurfing.proxy.loadComplete(event);
	}
	
	public static ShoulderSurfing getInstance()
	{
		return instance;
	}
}