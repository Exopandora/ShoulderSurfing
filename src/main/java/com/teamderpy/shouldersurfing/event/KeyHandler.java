package com.teamderpy.shouldersurfing.event;

import org.lwjgl.glfw.GLFW;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Config.ClientConfig.Perspective;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
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
	public static final KeyBinding KEYBIND_TOGGLE_SHOULDER_SURFING = new KeyBinding("Toggle perspective", InputMappings.INPUT_INVALID.getKeyCode(), KeyHandler.KEY_CATEGORY);
	
	@SubscribeEvent
	public static void keyInputEvent(KeyInputEvent event)
	{
		if(Minecraft.getInstance() != null && Minecraft.getInstance().currentScreen == null)
		{
			if(KEYBIND_TOGGLE_SHOULDER_SURFING.isPressed())
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
				if(KEYBIND_ROTATE_CAMERA_LEFT.isKeyDown())
				{
					Config.CLIENT.adjustCameraLeft();
				}
				
				if(KEYBIND_ROTATE_CAMERA_RIGHT.isKeyDown())
				{
					Config.CLIENT.adjustCameraRight();
				}
				
				if(KEYBIND_ZOOM_CAMERA_IN.isKeyDown())
				{
					Config.CLIENT.adjustCameraIn();
				}
				
				if(KEYBIND_ZOOM_CAMERA_OUT.isKeyDown())
				{
					Config.CLIENT.adjustCameraOut();
				}
				
				if(KEYBIND_SWAP_SHOULDER.isPressed())
				{
					Config.CLIENT.swapShoulder();
				}
			}
			
			if(Minecraft.getInstance().gameSettings.keyBindTogglePerspective.isPressed())
			{
				Perspective next = Perspective.of(Minecraft.getInstance().gameSettings.thirdPersonView + 1);
				
				if(Config.CLIENT.doRememberLastPerspective())
				{
					Config.CLIENT.setDefaultPerspective(next);
				}
				
				Minecraft.getInstance().gameSettings.thirdPersonView = next.getPerspectiveId();
			}
		}
	}
}
