package com.teamderpy.shouldersurfing;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.event.ClientEventHandler;
import com.teamderpy.shouldersurfing.event.KeyHandler;

import net.minecraft.client.Minecraft;
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
	
	private static boolean SHADER_ACTIVE;
	
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
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_ROTATE_CAMERA_LEFT);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_ROTATE_CAMERA_RIGHT);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_ZOOM_CAMERA_OUT);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_ZOOM_CAMERA_IN);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_SWAP_SHOULDER);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_TOGGLE_SHOULDER_SURFING);
		
		MinecraftForge.EVENT_BUS.addListener(KeyHandler::keyInputEvent);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::preRenderPlayerEvent);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::clientTickEvent);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, ClientEventHandler::preRenderGameOverlayEvent);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, ClientEventHandler::postRenderGameOverlayEvent);
		
		ShoulderSurfing.SHADER_ACTIVE = isClassLoaded("net.optifine.shaders.Shaders");
		Minecraft.getInstance().gameSettings.thirdPersonView = Config.CLIENT.getDefaultPerspective().getPerspectiveId();
	}
	
	public static float getShadersResMul()
	{
		if(ShoulderSurfing.SHADER_ACTIVE)
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