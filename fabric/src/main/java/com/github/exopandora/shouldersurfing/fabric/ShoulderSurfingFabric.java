package com.github.exopandora.shouldersurfing.fabric;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.client.InputHandler;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public class ShoulderSurfingFabric implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		ConfigRegistry.INSTANCE.register(ShoulderSurfingCommon.MOD_ID, Type.CLIENT, Config.CLIENT_SPEC);
		ConfigScreenFactoryRegistry.INSTANCE.register(ShoulderSurfingCommon.MOD_ID, ConfigurationScreen::new);
		KeyMappingHelper.registerKeyMapping(InputHandler.CAMERA_LEFT);
		KeyMappingHelper.registerKeyMapping(InputHandler.CAMERA_RIGHT);
		KeyMappingHelper.registerKeyMapping(InputHandler.CAMERA_IN);
		KeyMappingHelper.registerKeyMapping(InputHandler.CAMERA_OUT);
		KeyMappingHelper.registerKeyMapping(InputHandler.CAMERA_UP);
		KeyMappingHelper.registerKeyMapping(InputHandler.CAMERA_DOWN);
		KeyMappingHelper.registerKeyMapping(InputHandler.SWAP_SHOULDER);
		KeyMappingHelper.registerKeyMapping(InputHandler.TOGGLE_FIRST_PERSON);
		KeyMappingHelper.registerKeyMapping(InputHandler.TOGGLE_THIRD_PERSON_FRONT);
		KeyMappingHelper.registerKeyMapping(InputHandler.TOGGLE_THIRD_PERSON_BACK);
		KeyMappingHelper.registerKeyMapping(InputHandler.FREE_LOOK);
		KeyMappingHelper.registerKeyMapping(InputHandler.TOGGLE_CAMERA_COUPLING);
		KeyMappingHelper.registerKeyMapping(InputHandler.TOGGLE_X_OFFSET_PRESETS);
		KeyMappingHelper.registerKeyMapping(InputHandler.TOGGLE_Y_OFFSET_PRESETS);
		KeyMappingHelper.registerKeyMapping(InputHandler.TOGGLE_Z_OFFSET_PRESETS);
		KeyMappingHelper.registerKeyMapping(InputHandler.ENTER_FIRST_PERSON);
		KeyMappingHelper.registerKeyMapping(InputHandler.ENTER_THIRD_PERSON_FRONT);
		KeyMappingHelper.registerKeyMapping(InputHandler.ENTER_THIRD_PERSON_BACK);
		KeyMappingHelper.registerKeyMapping(InputHandler.ENTER_SHOULDER_SURFING);
		PluginLoader.getInstance().loadPlugins();
	}
}
