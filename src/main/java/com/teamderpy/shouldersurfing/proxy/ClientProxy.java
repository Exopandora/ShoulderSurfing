package com.teamderpy.shouldersurfing.proxy;

import com.teamderpy.shouldersurfing.client.KeyHandler;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;
import com.teamderpy.shouldersurfing.compatibility.EnumShaderCompatibility;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Config.ClientConfig;
import com.teamderpy.shouldersurfing.event.ClientEventHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		Config.CLIENT = new ClientConfig(new Configuration(event.getSuggestedConfigurationFile()));
		ShoulderInstance.getInstance().changePerspective(Config.CLIENT.getDefaultPerspective());
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		MinecraftForge.EVENT_BUS.register(new KeyHandler());
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
