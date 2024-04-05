package com.github.exopandora.shouldersurfing.fabric;

import com.github.exopandora.shouldersurfing.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.client.KeyHandler;
import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.api.impl.PluginLoader;
import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraftforge.fml.config.ModConfig.Type;

public class ShoulderSurfingFabric implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		ForgeConfigRegistry.INSTANCE.register(ShoulderSurfing.MODID, Type.CLIENT, Config.CLIENT_SPEC);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ShoulderRenderer.getInstance().resetState(handler.getPlayer()));
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> ShoulderRenderer.getInstance().resetState(newPlayer));
		KeyBindingHelper.registerKeyBinding(KeyHandler.CAMERA_LEFT);
		KeyBindingHelper.registerKeyBinding(KeyHandler.CAMERA_RIGHT);
		KeyBindingHelper.registerKeyBinding(KeyHandler.CAMERA_IN);
		KeyBindingHelper.registerKeyBinding(KeyHandler.CAMERA_OUT);
		KeyBindingHelper.registerKeyBinding(KeyHandler.CAMERA_UP);
		KeyBindingHelper.registerKeyBinding(KeyHandler.CAMERA_DOWN);
		KeyBindingHelper.registerKeyBinding(KeyHandler.SWAP_SHOULDER);
		KeyBindingHelper.registerKeyBinding(KeyHandler.TOGGLE_SHOULDER_SURFING);
		KeyBindingHelper.registerKeyBinding(KeyHandler.FREE_LOOK);
		PluginLoader.getInstance().loadPlugins();
	}
}
