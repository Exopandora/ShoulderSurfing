package com.teamderpy.shouldersurfing;

import com.teamderpy.shouldersurfing.client.KeyHandler;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.plugin.PluginLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class ShoulderSurfingFabric implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		ModLoadingContext.registerConfig(ShoulderSurfing.MODID, Type.CLIENT, Config.CLIENT_SPEC, ShoulderSurfing.MODID + ".toml");
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
		{
			ShoulderRenderer.getInstance().resetCameraRotations(handler.player);
			ShoulderInstance.getInstance().resetCameraEntityRotations(handler.player);
		});
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
