package com.teamderpy.shouldersurfing;

import com.teamderpy.shouldersurfing.client.ClientEventHandler;
import com.teamderpy.shouldersurfing.client.KeyHandler;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;
import com.teamderpy.shouldersurfing.config.Config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ShoulderSurfing.MODID)
public class ShoulderSurfing
{
	public static final String MODID = "shouldersurfing";
	
	public ShoulderSurfing()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext modLoadingContext = ModLoadingContext.get();
		modEventBus.addListener(this::clientSetup);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
		{
			modLoadingContext.registerConfig(Type.CLIENT, Config.CLIENT_SPEC, ShoulderSurfing.MODID + ".toml");
			modEventBus.addListener(this::registerKeyMappingsEvent);
		});
		modLoadingContext.registerExtensionPoint(DisplayTest.class, () -> new DisplayTest(() -> "ANY", (remote, isServer) -> true));
		modEventBus.register(Config.class);
	}
	
	@SubscribeEvent
	public void clientSetup(FMLClientSetupEvent event)
	{
		ShoulderInstance shoulderInstance = new ShoulderInstance();
		shoulderInstance.changePerspective(Config.CLIENT.getDefaultPerspective());
		ShoulderRenderer shoulderRenderer = new ShoulderRenderer(shoulderInstance);
		KeyHandler keyHandler = new KeyHandler(shoulderInstance);
		ClientEventHandler clientEventHandler = new ClientEventHandler(shoulderInstance, shoulderRenderer);
		MinecraftForge.EVENT_BUS.addListener(keyHandler::keyInputEvent);
		MinecraftForge.EVENT_BUS.addListener(clientEventHandler::preRenderPlayerEvent);
		MinecraftForge.EVENT_BUS.addListener(clientEventHandler::clientTickEvent);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, clientEventHandler::preRenderGuiOverlayEvent);
		MinecraftForge.EVENT_BUS.addListener(clientEventHandler::computeCameraAnglesEvent);
		MinecraftForge.EVENT_BUS.addListener(clientEventHandler::renderLevelStageEvent);
	}
	
	@SubscribeEvent
	public void registerKeyMappingsEvent(RegisterKeyMappingsEvent event)
	{
		event.register(KeyHandler.KEYBIND_CAMERA_LEFT);
		event.register(KeyHandler.KEYBIND_CAMERA_RIGHT);
		event.register(KeyHandler.KEYBIND_CAMERA_IN);
		event.register(KeyHandler.KEYBIND_CAMERA_OUT);
		event.register(KeyHandler.KEYBIND_CAMERA_UP);
		event.register(KeyHandler.KEYBIND_CAMERA_DOWN);
		event.register(KeyHandler.KEYBIND_SWAP_SHOULDER);
		event.register(KeyHandler.KEYBIND_TOGGLE_SHOULDER_SURFING);
	}
}
