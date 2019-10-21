package com.teamderpy.shouldersurfing.event;

import org.lwjgl.glfw.GLFW;

import com.teamderpy.shouldersurfing.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class KeyHandler
{
	public static final KeyBinding KEYBIND_ROTATE_CAMERA_LEFT = new KeyBinding("Camera left", GLFW.GLFW_KEY_LEFT, "Shoulder Surfing");
	public static final KeyBinding KEYBIND_ROTATE_CAMERA_RIGHT = new KeyBinding("Camera right", GLFW.GLFW_KEY_RIGHT, "Shoulder Surfing");
	public static final KeyBinding KEYBIND_ZOOM_CAMERA_OUT = new KeyBinding("Camera closer", GLFW.GLFW_KEY_UP, "Shoulder Surfing");
	public static final KeyBinding KEYBIND_ZOOM_CAMERA_IN = new KeyBinding("Camera farther", GLFW.GLFW_KEY_DOWN, "Shoulder Surfing");
	public static final KeyBinding KEYBIND_SWAP_SHOULDER = new KeyBinding("Swap shoulder", GLFW.GLFW_KEY_O, "Shoulder Surfing");
	
	@SubscribeEvent
	public static void keyInputEvent(KeyInputEvent event)
	{
		if(Minecraft.getInstance() != null && Minecraft.getInstance().currentScreen == null)
		{
			if(Minecraft.getInstance().gameSettings.thirdPersonView == Config.CLIENT.getShoulderSurfing3ppId())
			{
				if(KeyHandler.isKeyDown(KeyHandler.KEYBIND_ROTATE_CAMERA_LEFT))
				{
					Config.CLIENT.adjustCameraLeft();
				}
				
				if(KeyHandler.isKeyDown(KeyHandler.KEYBIND_ROTATE_CAMERA_RIGHT))
				{
					Config.CLIENT.adjustCameraRight();
				}
				
				if(KeyHandler.isKeyDown(KeyHandler.KEYBIND_ZOOM_CAMERA_IN))
				{
					Config.CLIENT.adjustCameraIn();
				}
				
				if(KeyHandler.isKeyDown(KeyHandler.KEYBIND_ZOOM_CAMERA_OUT))
				{
					Config.CLIENT.adjustCameraOut();
				}
				
				if(KeyHandler.isPressed(KeyHandler.KEYBIND_SWAP_SHOULDER))
				{
					Config.CLIENT.swapShoulder();
				}
			}
		}
	}
	
	private static boolean isPressed(KeyBinding keyBinding)
	{
		return keyBinding.isPressed() && (KeyModifier.NONE.equals(keyBinding.getKeyModifier()) || KeyModifier.getActiveModifier().equals(keyBinding.getKeyModifier()));
	}
	
	private static boolean isKeyDown(KeyBinding keyBinding)
	{
		return keyBinding.isKeyDown() && (KeyModifier.NONE.equals(keyBinding.getKeyModifier()) || KeyModifier.getActiveModifier().equals(keyBinding.getKeyModifier()));
	}
}
