package com.github.exopandora.shouldersurfing.fabric;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.client.InputHandler;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class ShoulderSurfingFabric implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		ModLoadingContext.registerConfig(ShoulderSurfingCommon.MOD_ID, Type.CLIENT, Config.CLIENT_SPEC);
		KeyBindingHelper.registerKeyBinding(InputHandler.CAMERA_LEFT);
		KeyBindingHelper.registerKeyBinding(InputHandler.CAMERA_RIGHT);
		KeyBindingHelper.registerKeyBinding(InputHandler.CAMERA_IN);
		KeyBindingHelper.registerKeyBinding(InputHandler.CAMERA_OUT);
		KeyBindingHelper.registerKeyBinding(InputHandler.CAMERA_UP);
		KeyBindingHelper.registerKeyBinding(InputHandler.CAMERA_DOWN);
		KeyBindingHelper.registerKeyBinding(InputHandler.SWAP_SHOULDER);
		KeyBindingHelper.registerKeyBinding(InputHandler.TOGGLE_FIRST_PERSON);
		KeyBindingHelper.registerKeyBinding(InputHandler.TOGGLE_THIRD_PERSON_FRONT);
		KeyBindingHelper.registerKeyBinding(InputHandler.TOGGLE_THIRD_PERSON_BACK);
		KeyBindingHelper.registerKeyBinding(InputHandler.FREE_LOOK);
		KeyBindingHelper.registerKeyBinding(InputHandler.TOGGLE_CAMERA_COUPLING);
		PluginLoader.getInstance().loadPlugins();
	}
}
