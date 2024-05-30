package com.github.exopandora.shouldersurfing.forge;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.client.InputHandler;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.forge.event.ClientEventHandler;
import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
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
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ShoulderSurfingCommon.MOD_ID)
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
			modEventBus.addListener(this::registerKeyMappingsEvent);
			modEventBus.addListener(this::modConfigLoadingEvent);
			modEventBus.addListener(this::modConfigReloadingEvent);
		});
		modLoadingContext.registerExtensionPoint(DisplayTest.class, () -> new DisplayTest(() -> "ANY", (remote, isServer) -> true));
	}
	
	@SubscribeEvent
	public void clientSetup(FMLClientSetupEvent event)
	{
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::clientTickEvent);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::renderLevelStageEvent);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, ClientEventHandler::movementInputUpdateEvent);
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
