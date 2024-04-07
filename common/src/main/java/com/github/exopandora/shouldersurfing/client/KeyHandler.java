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
		ShoulderInstance shoulderInstance = ShoulderInstance.getInstance();
		Minecraft minecraft = Minecraft.getInstance();
		
		while(TOGGLE_SHOULDER_SURFING.consumeClick())
		{
			if(shoulderInstance.doShoulderSurfing())
			{
				shoulderInstance.changePerspective(Perspective.FIRST_PERSON);
			}
			else if(minecraft.options.getCameraType() == CameraType.FIRST_PERSON)
			{
				shoulderInstance.changePerspective(Perspective.SHOULDER_SURFING);
			}
		}
		
		while(CAMERA_LEFT.consumeClick())
		{
			if(shoulderInstance.doShoulderSurfing())
			{
				Config.CLIENT.adjustCameraLeft();
			}
		}
		
		while(CAMERA_RIGHT.consumeClick())
		{
			if(shoulderInstance.doShoulderSurfing())
			{
				Config.CLIENT.adjustCameraRight();
			}
		}
		
		while(CAMERA_OUT.consumeClick())
		{
			if(shoulderInstance.doShoulderSurfing())
			{
				Config.CLIENT.adjustCameraOut();
			}
		}
		
		while(CAMERA_IN.consumeClick())
		{
			if(shoulderInstance.doShoulderSurfing())
			{
				Config.CLIENT.adjustCameraIn();
			}
		}
		
		while(CAMERA_UP.consumeClick())
		{
			if(shoulderInstance.doShoulderSurfing())
			{
				Config.CLIENT.adjustCameraUp();
			}
		}
		
		while(CAMERA_DOWN.consumeClick())
		{
			if(shoulderInstance.doShoulderSurfing())
			{
				Config.CLIENT.adjustCameraDown();
			}
		}
		
		while(SWAP_SHOULDER.consumeClick())
		{
			if(shoulderInstance.doShoulderSurfing())
			{
				Config.CLIENT.swapShoulder();
			}
		}
		
		while(minecraft.options.keyTogglePerspective.consumeClick())
		{
			Perspective perspective = Perspective.current();
			Perspective next = perspective.next();
			boolean isFirstPerson = next.getCameraType().isFirstPerson();
			shoulderInstance.changePerspective(next);
			minecraft.levelRenderer.needsUpdate();
			
			if(perspective.getCameraType().isFirstPerson() != isFirstPerson)
			{
				minecraft.gameRenderer.checkEntityPostEffect(isFirstPerson ? minecraft.getCameraEntity() : null);
			}
			
			if(Config.CLIENT.doRememberLastPerspective())
			{
				Config.CLIENT.setDefaultPerspective(next);
			}
		}
		
		while(FREE_LOOK.consumeClick());
	}
}
