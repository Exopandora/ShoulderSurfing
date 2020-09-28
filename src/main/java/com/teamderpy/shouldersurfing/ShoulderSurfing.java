package com.teamderpy.shouldersurfing;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.event.ClientEventHandler;
import com.teamderpy.shouldersurfing.event.KeyHandler;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ShoulderSurfing.MODID)
public class ShoulderSurfing
{
	public static final String MODID = "shouldersurfing";
	public static final float RAYTRACE_DISTANCE = 400.0F;
	public static boolean shoulderSurfing;
	
	private static boolean shaders;
	
	public ShoulderSurfing()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::clientSetup);
		ModLoadingContext.get().registerConfig(Type.CLIENT, Config.CLIENT_SPEC, ShoulderSurfing.MODID + ".toml");
		modEventBus.register(Config.class);
	}
	
	@SubscribeEvent
	public void clientSetup(FMLClientSetupEvent event)
	{
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_LEFT);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_RIGHT);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_IN);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_OUT);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_UP);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_DOWN);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_SWAP_SHOULDER);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_TOGGLE_SHOULDER_SURFING);
		
		MinecraftForge.EVENT_BUS.addListener(KeyHandler::keyInputEvent);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::preRenderPlayerEvent);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::clientTickEvent);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, ClientEventHandler::preRenderGameOverlayEvent);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, ClientEventHandler::postRenderGameOverlayEvent);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::cameraSetup);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::renderWorldLast);
		
		ShoulderSurfing.shaders = isClassLoaded("net.optifine.shaders.Shaders");
		ShoulderSurfingHelper.setPerspective(Config.CLIENT.getDefaultPerspective());
	}
	
	public static float getShadersResMul()
	{
		if(ShoulderSurfing.shaders)
		{
			return net.optifine.shaders.Shaders.shaderPackLoaded ? net.optifine.shaders.Shaders.configRenderResMul : 1.0F;
		}
		
		return 1.0F;
	}
	
	private static boolean isClassLoaded(String klass)
	{
		try
		{
			Class.forName(klass);
		}
		catch(ClassNotFoundException e)
		{
			return false;
		}
		
		return true;
	}
}