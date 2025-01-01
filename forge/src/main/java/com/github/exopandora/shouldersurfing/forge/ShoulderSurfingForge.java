package com.github.exopandora.shouldersurfing.forge;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.client.InputHandler;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.forge.event.ClientEventHandler;
import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
			modEventBus.addListener(this::modConfigLoadingEvent);
			modEventBus.addListener(this::modConfigReloadingEvent);
		});
		modLoadingContext.registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
	}
	
	@SubscribeEvent
	public void clientSetup(FMLClientSetupEvent event)
	{
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::clientTickEvent);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, ClientEventHandler::preRenderGuiOverlayEvent);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::renderLevelStageEvent);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, ClientEventHandler::movementInputUpdateEvent);
		
		ClientRegistry.registerKeyBinding(InputHandler.CAMERA_LEFT);
		ClientRegistry.registerKeyBinding(InputHandler.CAMERA_RIGHT);
		ClientRegistry.registerKeyBinding(InputHandler.CAMERA_IN);
		ClientRegistry.registerKeyBinding(InputHandler.CAMERA_OUT);
		ClientRegistry.registerKeyBinding(InputHandler.CAMERA_UP);
		ClientRegistry.registerKeyBinding(InputHandler.CAMERA_DOWN);
		ClientRegistry.registerKeyBinding(InputHandler.SWAP_SHOULDER);
		ClientRegistry.registerKeyBinding(InputHandler.TOGGLE_FIRST_PERSON);
		ClientRegistry.registerKeyBinding(InputHandler.TOGGLE_THIRD_PERSON_FRONT);
		ClientRegistry.registerKeyBinding(InputHandler.TOGGLE_THIRD_PERSON_BACK);
		ClientRegistry.registerKeyBinding(InputHandler.FREE_LOOK);
		ClientRegistry.registerKeyBinding(InputHandler.TOGGLE_CAMERA_COUPLING);
		
		Map<String, Object> modProperties = ModLoadingContext.get().getActiveContainer().getModInfo().getModProperties();
		List<?> incompatibleModIds = (List<?>) modProperties.getOrDefault("incompatibleMods", Collections.emptyList());
		FMLLoader.getLoadingModList().getMods().stream()
			.filter(info -> incompatibleModIds.contains(info.getModId()))
			.map(ShoulderSurfingForge::createIncompatibleModWarning)
			.forEach(ModLoader.get()::addWarning);
	}
	
	@SubscribeEvent
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		PluginLoader.getInstance().loadPlugins();
	}
	
	@SubscribeEvent
	public void modConfigLoadingEvent(ModConfig.Loading event)
	{
		ShoulderSurfingImpl.getInstance().init();
	}
	
	@SubscribeEvent
	public void modConfigReloadingEvent(ModConfig.Reloading event)
	{
		if(ShoulderSurfingCommon.MOD_ID.equals(event.getConfig().getModId()) && event.getConfig().getType() == Type.CLIENT)
		{
			Config.onConfigReload();
		}
	}
	
	private static ModLoadingWarning createIncompatibleModWarning(IModInfo incompatibleMod)
	{
		String translationKey = ShoulderSurfingCommon.MOD_ID + ".modloadingissue.incompatiblemod";
		String modId = incompatibleMod.getModId();
		String modVersion = incompatibleMod.getVersion().toString();
		return new ModLoadingWarning(null, ModLoadingStage.VALIDATE, translationKey, ShoulderSurfingCommon.MOD_ID, modId, modVersion);
	}
}
