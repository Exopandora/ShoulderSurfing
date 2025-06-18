package com.github.exopandora.shouldersurfing.neoforge;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.client.InputHandler;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.neoforge.event.ClientEventHandler;
import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ShoulderSurfingCommon.MOD_ID)
public class ShoulderSurfingNeoForge
{
	public ShoulderSurfingNeoForge(ModContainer modContainer, IEventBus modEventBus)
	{
		modEventBus.addListener(this::clientSetup);
		modEventBus.addListener(this::loadComplete);
		
		if(FMLEnvironment.dist.isClient())
		{
			modContainer.registerConfig(Type.CLIENT, Config.CLIENT_SPEC);
			modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
			modEventBus.addListener(this::registerKeyMappingsEvent);
			modEventBus.addListener(this::modConfigReloadingEvent);
			modEventBus.addListener(ClientEventHandler::registerGuiOverlaysEvent);
		}
	}
	
	@SubscribeEvent
	public void clientSetup(FMLClientSetupEvent event)
	{
		NeoForge.EVENT_BUS.addListener(ClientEventHandler::clientTickEvent);
		NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, ClientEventHandler::preRenderGuiOverlayEvent);
		NeoForge.EVENT_BUS.addListener(ClientEventHandler::frameGraphSetupEvent);
		NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ClientEventHandler::movementInputUpdateEvent);
	}
	
	@SubscribeEvent
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		PluginLoader.getInstance().loadPlugins();
	}
	
	@SubscribeEvent
	public void clientStartedEvent(ClientStartedEvent event)
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
		event.register(InputHandler.TOGGLE_FIRST_PERSON);
		event.register(InputHandler.TOGGLE_THIRD_PERSON_FRONT);
		event.register(InputHandler.TOGGLE_THIRD_PERSON_BACK);
		event.register(InputHandler.FREE_LOOK);
		event.register(InputHandler.TOGGLE_CAMERA_COUPLING);
		event.register(InputHandler.TOGGLE_X_OFFSET_PRESETS);
		event.register(InputHandler.TOGGLE_Y_OFFSET_PRESETS);
		event.register(InputHandler.TOGGLE_Z_OFFSET_PRESETS);
	}
}
