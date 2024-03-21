package com.teamderpy.shouldersurfing;

import com.teamderpy.shouldersurfing.client.KeyHandler;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.plugin.PluginLoader;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
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
		ForgeConfigRegistry.INSTANCE.register(ShoulderSurfing.MODID, Type.CLIENT, Config.CLIENT_SPEC, ShoulderSurfing.MODID + ".toml");
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
		{
			ShoulderRenderer.getInstance().resetCameraRotations(handler.getPlayer());
			ShoulderInstance.getInstance().resetCameraEntityRotations(handler.getPlayer());
		});
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
		{
			ShoulderRenderer.getInstance().resetCameraRotations(newPlayer);
			ShoulderInstance.getInstance().resetCameraEntityRotations(newPlayer);
		});
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_LEFT);
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_RIGHT);
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_IN);
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_OUT);
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_UP);
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_DOWN);
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_SWAP_SHOULDER);
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_TOGGLE_SHOULDER_SURFING);
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_FREE_LOOK);
		PluginLoader.getInstance().loadPlugins();
	}
}
