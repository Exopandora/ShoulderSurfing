package com.teamderpy.shouldersurfing.event;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.util.ShoulderState;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KeyHandler
{
	private static final String KEY_CATEGORY = "Shoulder Surfing";
	
	public static final KeyMapping KEYBIND_CAMERA_LEFT = new KeyMapping("Camera left", GLFW.GLFW_KEY_LEFT, KeyHandler.KEY_CATEGORY);
	public static final KeyMapping KEYBIND_CAMERA_RIGHT = new KeyMapping("Camera right", GLFW.GLFW_KEY_RIGHT, KeyHandler.KEY_CATEGORY);
	public static final KeyMapping KEYBIND_CAMERA_IN = new KeyMapping("Camera closer", GLFW.GLFW_KEY_UP, KeyHandler.KEY_CATEGORY);
	public static final KeyMapping KEYBIND_CAMERA_OUT = new KeyMapping("Camera farther", GLFW.GLFW_KEY_DOWN, KeyHandler.KEY_CATEGORY);
	public static final KeyMapping KEYBIND_CAMERA_UP = new KeyMapping("Camera up", GLFW.GLFW_KEY_PAGE_UP, KeyHandler.KEY_CATEGORY);
	public static final KeyMapping KEYBIND_CAMERA_DOWN = new KeyMapping("Camera down", GLFW.GLFW_KEY_PAGE_DOWN, KeyHandler.KEY_CATEGORY);
	
	public static final KeyMapping KEYBIND_SWAP_SHOULDER = new KeyMapping("Swap shoulder", GLFW.GLFW_KEY_O, KeyHandler.KEY_CATEGORY);
	public static final KeyMapping KEYBIND_TOGGLE_SHOULDER_SURFING = new KeyMapping("Toggle perspective", InputConstants.UNKNOWN.getValue(), KeyHandler.KEY_CATEGORY);
	
	@SubscribeEvent
	public static void keyInputEvent(KeyInputEvent event)
	{
		if(Minecraft.getInstance() != null && Minecraft.getInstance().screen == null)
		{
			if(KEYBIND_TOGGLE_SHOULDER_SURFING.consumeClick())
			{
				if(ShoulderState.doShoulderSurfing())
				{
					ShoulderSurfingHelper.setPerspective(Perspective.FIRST_PERSON);
				}
				else if(Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON)
				{
					ShoulderSurfingHelper.setPerspective(Perspective.SHOULDER_SURFING);
				}
			}
			
			if(ShoulderState.doShoulderSurfing())
			{
				if(KEYBIND_CAMERA_LEFT.isDown())
				{
					Config.CLIENT.adjustCameraLeft();
				}
				
				if(KEYBIND_CAMERA_RIGHT.isDown())
				{
					Config.CLIENT.adjustCameraRight();
				}
				
				if(KEYBIND_CAMERA_OUT.isDown())
				{
					Config.CLIENT.adjustCameraOut();
				}
				
				if(KEYBIND_CAMERA_IN.isDown())
				{
					Config.CLIENT.adjustCameraIn();
				}
				
				if(KEYBIND_CAMERA_UP.isDown())
				{
					Config.CLIENT.adjustCameraUp();
				}
				
				if(KEYBIND_CAMERA_DOWN.isDown())
				{
					Config.CLIENT.adjustCameraDown();
				}
				
				if(KEYBIND_SWAP_SHOULDER.isDown())
				{
					Config.CLIENT.swapShoulder();
				}
			}
			
			if(Minecraft.getInstance().options.keyTogglePerspective.consumeClick())
			{
				Perspective perspective = Perspective.current();
				Perspective next = perspective.next();
				ShoulderSurfingHelper.setPerspective(next);
				boolean firstPerson = next.getCameraType().isFirstPerson();
				
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
