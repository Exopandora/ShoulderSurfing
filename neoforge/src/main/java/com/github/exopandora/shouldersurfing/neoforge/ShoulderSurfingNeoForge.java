package com.github.exopandora.shouldersurfing.neoforge;

import com.github.exopandora.shouldersurfing.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.impl.PluginLoader;
import com.github.exopandora.shouldersurfing.client.KeyHandler;
import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.neoforge.event.ClientEventHandler;
import fuzs.forgeconfigapiport.neoforge.api.forge.v4.ForgeConfigRegistry;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.IExtensionPoint.DisplayTest;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ShoulderSurfing.MODID)
public class ShoulderSurfingNeoForge
{
	public ShoulderSurfingNeoForge(IEventBus modEventBus)
	{
		ModLoadingContext modLoadingContext = ModLoadingContext.get();
		modEventBus.addListener(this::clientSetup);
		modEventBus.addListener(this::loadComplete);
		if(FMLEnvironment.dist.isClient())
		{
			ForgeConfigRegistry.INSTANCE.register(Type.CLIENT, Config.CLIENT_SPEC);
			modEventBus.addListener(this::registerKeyMappingsEvent);
			modEventBus.addListener(this::modConfigLoadingEvent);
			modEventBus.addListener(this::modConfigReloadingEvent);
		}
		modLoadingContext.registerExtensionPoint(DisplayTest.class, () -> new DisplayTest(() -> "ANY", (remote, isServer) -> true));
	}
	
	@SubscribeEvent
	public void clientSetup(FMLClientSetupEvent event)
	{
		NeoForge.EVENT_BUS.addListener(ClientEventHandler::clientTickEvent);
		NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, ClientEventHandler::preRenderGuiOverlayEvent);
		NeoForge.EVENT_BUS.addListener(ClientEventHandler::computeCameraAnglesEvent);
		NeoForge.EVENT_BUS.addListener(ClientEventHandler::renderLevelStageEvent);
		NeoForge.EVENT_BUS.addListener(ClientEventHandler::onDatapackSyncEvent);
		NeoForge.EVENT_BUS.addListener(ClientEventHandler::playerRespawnEvent);
		NeoForge.EVENT_BUS.addListener(ClientEventHandler::movementInputUpdateEvent);
	}
	
	@SubscribeEvent
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		PluginLoader.getInstance().loadPlugins();
	}
	
	@SubscribeEvent
	public void modConfigLoadingEvent(ModConfigEvent.Loading event)
	{
		ShoulderInstance.getInstance().changePerspective(Config.CLIENT.getDefaultPerspective());
	}
	
	@SubscribeEvent
	public void modConfigReloadingEvent(ModConfigEvent.Reloading event)
	{
		if(ShoulderSurfing.MODID.equals(event.getConfig().getModId()) && event.getConfig().getType() == Type.CLIENT)
		{
			Config.onConfigReload();
		}
	}
	
	@SubscribeEvent
	public void registerKeyMappingsEvent(RegisterKeyMappingsEvent event)
	{
		event.register(KeyHandler.CAMERA_LEFT);
		event.register(KeyHandler.CAMERA_RIGHT);
		event.register(KeyHandler.CAMERA_IN);
		event.register(KeyHandler.CAMERA_OUT);
		event.register(KeyHandler.CAMERA_UP);
		event.register(KeyHandler.CAMERA_DOWN);
		event.register(KeyHandler.SWAP_SHOULDER);
		event.register(KeyHandler.TOGGLE_SHOULDER_SURFING);
		event.register(KeyHandler.FREE_LOOK);
	}
}
