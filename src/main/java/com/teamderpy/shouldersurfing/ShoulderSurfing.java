package com.teamderpy.shouldersurfing;

import com.teamderpy.shouldersurfing.client.ClientEventHandler;
import com.teamderpy.shouldersurfing.client.KeyHandler;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;
import com.teamderpy.shouldersurfing.config.Config;

import com.teamderpy.shouldersurfing.config.Perspective;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.config.ModConfig;

public class ShoulderSurfing implements ClientModInitializer
{
	public static final String MODID = "shouldersurfing";
	private ShoulderRenderer shoulderRenderer;
	public static ShoulderSurfing INSTANCE;
	private ShoulderInstance shoulderInstance;
	private KeyHandler keyHandler;

	@Override
	public void onInitializeClient()
	{
		INSTANCE = this;


		this.shoulderInstance = new ShoulderInstance();


		ClientTickEvents.START_CLIENT_TICK.register(this.shoulderInstance::tick);

		this.keyHandler = new KeyHandler(shoulderInstance);

		ModLoadingContext.registerConfig(MODID, ModConfig.Type.CLIENT, Config.setup(), ShoulderSurfing.MODID + ".toml");
		registerKeyMappingsEvent();


		//modEventBus.register(Config.class);
	}

	public void setupPerspective(Options options) {
		ModConfigEvent.RELOADING.register(event -> {
			if(ShoulderInstance.getInstance() != null && ModConfig.Type.CLIENT.equals(event.getType()) && Config.CLIENT.doRememberLastPerspective())
			{
				Config.CLIENT.setDefaultPerspective(Perspective.current(options));
			}
		});
		shoulderInstance.changePerspective(options, Config.CLIENT.getDefaultPerspective());
		this.shoulderRenderer = new ShoulderRenderer(shoulderInstance);
	}

	public void handleKeyInputs(Minecraft minecraft) {
		keyHandler.keyInputEvent(minecraft);
	}
	
	public void registerKeyMappingsEvent()
	{
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_LEFT);
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_RIGHT);
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_IN);
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_OUT);
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_UP);
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_CAMERA_DOWN);
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_SWAP_SHOULDER);
		KeyBindingHelper.registerKeyBinding(KeyHandler.KEYBIND_TOGGLE_SHOULDER_SURFING);
	}

	public ShoulderRenderer getShoulderRenderer() {
		return shoulderRenderer;
	}
}
