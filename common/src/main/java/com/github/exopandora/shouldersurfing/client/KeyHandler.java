package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.Perspective;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.util.InputMappings;
import org.lwjgl.glfw.GLFW;

public class KeyHandler
{
	private static final String KEY_CATEGORY = "Shoulder Surfing";
	
	public static final KeyBinding CAMERA_LEFT = new KeyBinding("Camera left", GLFW.GLFW_KEY_LEFT, KEY_CATEGORY);
	public static final KeyBinding CAMERA_RIGHT = new KeyBinding("Camera right", GLFW.GLFW_KEY_RIGHT, KEY_CATEGORY);
	public static final KeyBinding CAMERA_IN = new KeyBinding("Camera closer", GLFW.GLFW_KEY_UP, KEY_CATEGORY);
	public static final KeyBinding CAMERA_OUT = new KeyBinding("Camera farther", GLFW.GLFW_KEY_DOWN, KEY_CATEGORY);
	public static final KeyBinding CAMERA_UP = new KeyBinding("Camera up", GLFW.GLFW_KEY_PAGE_UP, KEY_CATEGORY);
	public static final KeyBinding CAMERA_DOWN = new KeyBinding("Camera down", GLFW.GLFW_KEY_PAGE_DOWN, KEY_CATEGORY);
	public static final KeyBinding SWAP_SHOULDER = new KeyBinding("Swap shoulder", GLFW.GLFW_KEY_O, KEY_CATEGORY);
	public static final KeyBinding TOGGLE_SHOULDER_SURFING = new KeyBinding("Toggle perspective", InputMappings.UNKNOWN.getValue(), KEY_CATEGORY);
	public static final KeyBinding FREE_LOOK = new KeyBinding("Free look", GLFW.GLFW_KEY_LEFT_ALT, KEY_CATEGORY);
	
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
			else if(minecraft.options.getCameraType() == PointOfView.FIRST_PERSON)
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
