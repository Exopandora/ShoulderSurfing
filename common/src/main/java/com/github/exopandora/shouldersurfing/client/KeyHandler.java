package com.github.exopandora.shouldersurfing.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.Perspective;

import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class KeyHandler
{
	private static final String KEY_CATEGORY = "Shoulder Surfing";
	
	public static final KeyMapping CAMERA_LEFT = new KeyMapping("Camera left", GLFW.GLFW_KEY_LEFT, KEY_CATEGORY);
	public static final KeyMapping CAMERA_RIGHT = new KeyMapping("Camera right", GLFW.GLFW_KEY_RIGHT, KEY_CATEGORY);
	public static final KeyMapping CAMERA_IN = new KeyMapping("Camera closer", GLFW.GLFW_KEY_UP, KEY_CATEGORY);
	public static final KeyMapping CAMERA_OUT = new KeyMapping("Camera farther", GLFW.GLFW_KEY_DOWN, KEY_CATEGORY);
	public static final KeyMapping CAMERA_UP = new KeyMapping("Camera up", GLFW.GLFW_KEY_PAGE_UP, KEY_CATEGORY);
	public static final KeyMapping CAMERA_DOWN = new KeyMapping("Camera down", GLFW.GLFW_KEY_PAGE_DOWN, KEY_CATEGORY);
	public static final KeyMapping SWAP_SHOULDER = new KeyMapping("Swap shoulder", GLFW.GLFW_KEY_O, KEY_CATEGORY);
	public static final KeyMapping TOGGLE_SHOULDER_SURFING = new KeyMapping("Toggle perspective", InputConstants.UNKNOWN.getValue(), KEY_CATEGORY);
	public static final KeyMapping FREE_LOOK = new KeyMapping("Free look", GLFW.GLFW_KEY_LEFT_ALT, KEY_CATEGORY);
	
	private KeyHandler()
	{
		super();
	}
	
	public static void tick()
	{
		if(Minecraft.getInstance().screen == null)
		{
			ShoulderInstance shoulderInstance = ShoulderInstance.getInstance();
			
			if(TOGGLE_SHOULDER_SURFING.consumeClick())
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
				if(CAMERA_LEFT.consumeClick())
				{
					Config.CLIENT.adjustCameraLeft();
				}
				
				if(CAMERA_RIGHT.consumeClick())
				{
					Config.CLIENT.adjustCameraRight();
				}
				
				if(CAMERA_OUT.consumeClick())
				{
					Config.CLIENT.adjustCameraOut();
				}
				
				if(CAMERA_IN.consumeClick())
				{
					Config.CLIENT.adjustCameraIn();
				}
				
				if(CAMERA_UP.consumeClick())
				{
					Config.CLIENT.adjustCameraUp();
				}
				
				if(CAMERA_DOWN.consumeClick())
				{
					Config.CLIENT.adjustCameraDown();
				}
				
				if(SWAP_SHOULDER.consumeClick())
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
			
			FREE_LOOK.consumeClick();
		}
	}
}
