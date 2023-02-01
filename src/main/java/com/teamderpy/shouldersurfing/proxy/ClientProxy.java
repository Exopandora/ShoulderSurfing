package com.teamderpy.shouldersurfing.proxy;

import com.teamderpy.shouldersurfing.api.ShoulderSurfingPlugin;
import com.teamderpy.shouldersurfing.client.KeyHandler;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;
import com.teamderpy.shouldersurfing.compatibility.EnumShaderCompatibility;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Config.ClientConfig;
import com.teamderpy.shouldersurfing.event.ClientEventHandler;
import com.teamderpy.shouldersurfing.plugin.PluginLoader;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		Config.CLIENT = new ClientConfig(new Configuration(event.getSuggestedConfigurationFile()));
		ShoulderInstance.getInstance().changePerspective(Config.CLIENT.getDefaultPerspective());
		ClientEventHandler clientEventHandler = new ClientEventHandler();
		FMLCommonHandler.instance().bus().register(clientEventHandler);
		MinecraftForge.EVENT_BUS.register(clientEventHandler);
		PluginLoader.getInstance().registerPlugins(event.getAsmData().getAll(ShoulderSurfingPlugin.class.getCanonicalName()));
	}
	
	@Override
	public void init(FMLInitializationEvent event)
	{
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_LEFT);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_RIGHT);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_IN);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_OUT);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_UP);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_DOWN);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_SWAP_SHOULDER);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_TOGGLE_SHOULDER_SURFING);
	}
	
	@Override
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		if(isClassLoaded("shadersmod.client.Shaders"))
		{
			ShoulderRenderer.getInstance().setShaderType(EnumShaderCompatibility.OLD);
		}
		else if(isClassLoaded("net.optifine.shaders.Shaders"))
		{
			ShoulderRenderer.getInstance().setShaderType(EnumShaderCompatibility.NEW);
		}
		
		PluginLoader.getInstance().loadPlugins();
	}
	
	private static boolean isClassLoaded(String className)
	{
		try
		{
			return Class.forName(className) != null;
		}
		catch(ClassNotFoundException e)
		{
			return false;
		}
	}
}
