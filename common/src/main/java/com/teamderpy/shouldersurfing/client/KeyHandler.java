package com.teamderpy.shouldersurfing.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;

import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class KeyHandler
{
	private static final String KEY_CATEGORY = "Shoulder Surfing";
	
	public static final KeyMapping KEYBIND_CAMERA_LEFT = new KeyMapping("Camera left", GLFW.GLFW_KEY_LEFT, KEY_CATEGORY);
	public static final KeyMapping KEYBIND_CAMERA_RIGHT = new KeyMapping("Camera right", GLFW.GLFW_KEY_RIGHT, KEY_CATEGORY);
	public static final KeyMapping KEYBIND_CAMERA_IN = new KeyMapping("Camera closer", GLFW.GLFW_KEY_UP, KEY_CATEGORY);
	public static final KeyMapping KEYBIND_CAMERA_OUT = new KeyMapping("Camera farther", GLFW.GLFW_KEY_DOWN, KEY_CATEGORY);
	public static final KeyMapping KEYBIND_CAMERA_UP = new KeyMapping("Camera up", GLFW.GLFW_KEY_PAGE_UP, KEY_CATEGORY);
	public static final KeyMapping KEYBIND_CAMERA_DOWN = new KeyMapping("Camera down", GLFW.GLFW_KEY_PAGE_DOWN, KEY_CATEGORY);
	public static final KeyMapping KEYBIND_SWAP_SHOULDER = new KeyMapping("Swap shoulder", GLFW.GLFW_KEY_O, KEY_CATEGORY);
	public static final KeyMapping KEYBIND_TOGGLE_SHOULDER_SURFING = new KeyMapping("Toggle perspective", InputConstants.UNKNOWN.getValue(), KEY_CATEGORY);
	
	public static void onKeyInput()
	{
		if(Minecraft.getInstance() != null && Minecraft.getInstance().screen == null)
		{
			ShoulderInstance shoulderInstance = ShoulderInstance.getInstance();
			
			if(KEYBIND_TOGGLE_SHOULDER_SURFING.consumeClick())
			{
				if(shoulderInstance.doShoulderSurfing())
				{
					shoulderInstance.changePerspective(Perspective.FIRST_PERSON);
				}
				else if(Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON)
				{
					shoulderInstance.changePerspective(Perspective.SHOULDER_SURFING);
				}
			}
			
			if(shoulderInstance.doShoulderSurfing())
			{
				if(KEYBIND_CAMERA_LEFT.consumeClick())
				{
					Config.CLIENT.adjustCameraLeft();
				}
				
				if(KEYBIND_CAMERA_RIGHT.consumeClick())
				{
					Config.CLIENT.adjustCameraRight();
				}
				
				if(KEYBIND_CAMERA_OUT.consumeClick())
				{
					Config.CLIENT.adjustCameraOut();
				}
				
				if(KEYBIND_CAMERA_IN.consumeClick())
				{
					Config.CLIENT.adjustCameraIn();
				}
				
				if(KEYBIND_CAMERA_UP.consumeClick())
				{
					Config.CLIENT.adjustCameraUp();
				}
				
				if(KEYBIND_CAMERA_DOWN.consumeClick())
				{
					Config.CLIENT.adjustCameraDown();
				}
				
				if(KEYBIND_SWAP_SHOULDER.consumeClick())
				{
					Config.CLIENT.swapShoulder();
				}
			}
			
			if(Minecraft.getInstance().options.keyTogglePerspective.consumeClick())
			{
				Perspective perspective = Perspective.current();
				Perspective next = perspective.next();
				boolean firstPerson = next.getCameraType().isFirstPerson();
				shoulderInstance.changePerspective(next);
				
				if(perspective.getCameraType().isFirstPerson() != firstPerson)
				{
					Minecraft.getInstance().gameRenderer.checkEntityPostEffect(firstPerson ? Minecraft.getInstance().getCameraEntity() : null);
				}
				
				if(Config.CLIENT.doRememberLastPerspective())
				{
					Config.CLIENT.setDefaultPerspective(next);
				}
			}
		}
	}
}
