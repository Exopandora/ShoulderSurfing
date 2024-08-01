package com.github.exopandora.shouldersurfing.fabric;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.client.InputHandler;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public class ShoulderSurfingFabric implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		NeoForgeConfigRegistry.INSTANCE.register(ShoulderSurfingCommon.MOD_ID, Type.CLIENT, Config.CLIENT_SPEC);
		ConfigScreenFactoryRegistry.INSTANCE.register(ShoulderSurfingCommon.MOD_ID, ConfigurationScreen::new);
		KeyBindingHelper.registerKeyBinding(InputHandler.CAMERA_LEFT);
		KeyBindingHelper.registerKeyBinding(InputHandler.CAMERA_RIGHT);
		KeyBindingHelper.registerKeyBinding(InputHandler.CAMERA_IN);
		KeyBindingHelper.registerKeyBinding(InputHandler.CAMERA_OUT);
		KeyBindingHelper.registerKeyBinding(InputHandler.CAMERA_UP);
		KeyBindingHelper.registerKeyBinding(InputHandler.CAMERA_DOWN);
		KeyBindingHelper.registerKeyBinding(InputHandler.SWAP_SHOULDER);
		KeyBindingHelper.registerKeyBinding(InputHandler.TOGGLE_SHOULDER_SURFING);
		KeyBindingHelper.registerKeyBinding(InputHandler.FREE_LOOK);
		PluginLoader.getInstance().loadPlugins();
	}
}
