package com.teamderpy.shouldersurfing.client;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
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
		if(Minecraft.getInstance().screen == null)
		{
			ShoulderInstance shoulderInstance = ShoulderInstance.getInstance();
			
			if(TOGGLE_SHOULDER_SURFING.consumeClick())
			{
				if(shoulderInstance.doShoulderSurfing())
				{
					shoulderInstance.changePerspective(Perspective.FIRST_PERSON);
				}
				else if(Minecraft.getInstance().options.getCameraType() == PointOfView.FIRST_PERSON)
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
