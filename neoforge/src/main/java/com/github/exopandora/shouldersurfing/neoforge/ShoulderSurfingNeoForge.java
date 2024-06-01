package com.github.exopandora.shouldersurfing.neoforge;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.client.InputHandler;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.neoforge.event.ClientEventHandler;
import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
import fuzs.forgeconfigapiport.neoforge.api.forge.v4.ForgeConfigRegistry;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ShoulderSurfingCommon.MOD_ID)
public class ShoulderSurfingNeoForge
{
	public ShoulderSurfingNeoForge(IEventBus modEventBus)
	{
		modEventBus.addListener(this::clientSetup);
		modEventBus.addListener(this::loadComplete);
		
		if(FMLEnvironment.dist.isClient())
		{
			ForgeConfigRegistry.INSTANCE.register(Type.CLIENT, Config.CLIENT_SPEC);
			modEventBus.addListener(this::registerKeyMappingsEvent);
			modEventBus.addListener(this::modConfigLoadingEvent);
			modEventBus.addListener(this::modConfigReloadingEvent);
		}
	}
	
	@SubscribeEvent
	public void clientSetup(FMLClientSetupEvent event)
	{
		NeoForge.EVENT_BUS.addListener(ClientEventHandler::clientTickEvent);
		NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, ClientEventHandler::preRenderGuiOverlayEvent);
		NeoForge.EVENT_BUS.addListener(ClientEventHandler::renderLevelStageEvent);
		NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ClientEventHandler::movementInputUpdateEvent);
	}
	
	@SubscribeEvent
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		PluginLoader.getInstance().loadPlugins();
	}
	
	@SubscribeEvent
	public void modConfigLoadingEvent(ModConfigEvent.Loading event)
	{
		ShoulderSurfingImpl.getInstance().init();
	}
	
	@SubscribeEvent
	public void modConfigReloadingEvent(ModConfigEvent.Reloading event)
	{
		if(ShoulderSurfingCommon.MOD_ID.equals(event.getConfig().getModId()) && event.getConfig().getType() == Type.CLIENT)
		{
			Config.onConfigReload();
		}
	}
	
	@SubscribeEvent
	public void registerKeyMappingsEvent(RegisterKeyMappingsEvent event)
	{
		event.register(InputHandler.CAMERA_LEFT);
		event.register(InputHandler.CAMERA_RIGHT);
		event.register(InputHandler.CAMERA_IN);
		event.register(InputHandler.CAMERA_OUT);
		event.register(InputHandler.CAMERA_UP);
		event.register(InputHandler.CAMERA_DOWN);
		event.register(InputHandler.SWAP_SHOULDER);
		event.register(InputHandler.TOGGLE_SHOULDER_SURFING);
		event.register(InputHandler.FREE_LOOK);
	}
}
