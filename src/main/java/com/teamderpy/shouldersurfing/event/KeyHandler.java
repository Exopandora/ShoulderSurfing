package com.teamderpy.shouldersurfing.event;

import org.lwjgl.glfw.GLFW;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Config.ClientConfig.Perspective;

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
	private static final String KEY_CATEGORY = "Shoulder Surfing";
	
	public static final KeyBinding KEYBIND_ROTATE_CAMERA_LEFT = new KeyBinding("Camera left", GLFW.GLFW_KEY_LEFT, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_ROTATE_CAMERA_RIGHT = new KeyBinding("Camera right", GLFW.GLFW_KEY_RIGHT, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_ZOOM_CAMERA_OUT = new KeyBinding("Camera closer", GLFW.GLFW_KEY_UP, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_ZOOM_CAMERA_IN = new KeyBinding("Camera farther", GLFW.GLFW_KEY_DOWN, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_SWAP_SHOULDER = new KeyBinding("Swap shoulder", GLFW.GLFW_KEY_O, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_TOGGLE_SHOULDER_SURFING = new KeyBinding("Toggle perspective", GLFW.GLFW_KEY_F6, KeyHandler.KEY_CATEGORY);
	
	@SubscribeEvent
	public static void keyInputEvent(KeyInputEvent event)
	{
		if(Minecraft.getInstance() != null && Minecraft.getInstance().currentScreen == null)
		{
			if(KeyHandler.isPressed(KeyHandler.KEYBIND_TOGGLE_SHOULDER_SURFING))
			{
				if(Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId())
				{
					Minecraft.getInstance().gameSettings.thirdPersonView = Perspective.FIRST_PERSON.getPerspectiveId();
				}
				else if(Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.FIRST_PERSON.getPerspectiveId())
				{
					Minecraft.getInstance().gameSettings.thirdPersonView = Perspective.SHOULDER_SURFING.getPerspectiveId();
				}
			}
			
			if(Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId())
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
