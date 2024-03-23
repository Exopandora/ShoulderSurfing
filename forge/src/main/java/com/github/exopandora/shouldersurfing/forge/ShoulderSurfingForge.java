package com.github.exopandora.shouldersurfing.forge;

import com.github.exopandora.shouldersurfing.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.forge.event.ClientEventHandler;
import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
import org.apache.commons.lang3.tuple.Pair;

import com.github.exopandora.shouldersurfing.client.KeyHandler;
import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.config.Config;
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
			modLoadingContext.registerConfig(Type.CLIENT, Config.CLIENT_SPEC);
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
		
		ClientRegistry.registerKeyBinding(KeyHandler.CAMERA_LEFT);
		ClientRegistry.registerKeyBinding(KeyHandler.CAMERA_RIGHT);
		ClientRegistry.registerKeyBinding(KeyHandler.CAMERA_IN);
		ClientRegistry.registerKeyBinding(KeyHandler.CAMERA_OUT);
		ClientRegistry.registerKeyBinding(KeyHandler.CAMERA_UP);
		ClientRegistry.registerKeyBinding(KeyHandler.CAMERA_DOWN);
		ClientRegistry.registerKeyBinding(KeyHandler.SWAP_SHOULDER);
		ClientRegistry.registerKeyBinding(KeyHandler.TOGGLE_SHOULDER_SURFING);
		ClientRegistry.registerKeyBinding(KeyHandler.FREE_LOOK);
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
