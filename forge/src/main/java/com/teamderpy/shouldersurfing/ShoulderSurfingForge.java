package com.teamderpy.shouldersurfing;

import org.apache.commons.lang3.tuple.Pair;

import com.teamderpy.shouldersurfing.client.KeyHandler;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.forge.ClientEventHandler;
import com.teamderpy.shouldersurfing.plugin.PluginLoader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod(ShoulderSurfing.MODID)
public class ShoulderSurfingForge
{
	public ShoulderSurfingForge()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext modLoadingContext = ModLoadingContext.get();
		modEventBus.addListener(this::clientSetup);
		modEventBus.addListener(this::loadComplete);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
		{
			modLoadingContext.registerConfig(Type.CLIENT, Config.CLIENT_SPEC, ShoulderSurfing.MODID + ".toml");
			modEventBus.addListener(this::modConfigLoadingEvent);
			modEventBus.addListener(this::modConfigReloadingEvent);
		});
		modLoadingContext.registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
	}
	
	@SubscribeEvent
	public void clientSetup(FMLClientSetupEvent event)
	{
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::keyInputEvent);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::clientTickEvent);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, ClientEventHandler::preRenderGuiOverlayEvent);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::computeCameraAnglesEvent);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::renderLevelStageEvent);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::onDatapackSyncEvent);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::playerRespawnEvent);
		
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_LEFT);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_RIGHT);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_IN);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_OUT);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_UP);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_DOWN);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_SWAP_SHOULDER);
		ClientRegistry.registerKeyBinding(KeyHandler.KEYBIND_TOGGLE_SHOULDER_SURFING);
	}
	
	@SubscribeEvent
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		PluginLoader.getInstance().loadPlugins();
	}
	
	@SubscribeEvent
	public void modConfigLoadingEvent(ModConfig.Loading event)
	{
		ShoulderInstance.getInstance().changePerspective(Config.CLIENT.getDefaultPerspective());
	}
	
	@SubscribeEvent
	public void modConfigReloadingEvent(ModConfig.Reloading event)
	{
		Config.onConfigReload(event.getConfig());
	}
}
