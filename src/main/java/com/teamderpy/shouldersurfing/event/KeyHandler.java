package com.teamderpy.shouldersurfing.event;

import org.lwjgl.glfw.GLFW;

import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class KeyHandler
{
	private static final String KEY_CATEGORY = "Shoulder Surfing";
	
	public static final KeyBinding KEYBIND_CAMERA_LEFT = new KeyBinding("Camera left", GLFW.GLFW_KEY_LEFT, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_CAMERA_RIGHT = new KeyBinding("Camera right", GLFW.GLFW_KEY_RIGHT, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_CAMERA_IN = new KeyBinding("Camera closer", GLFW.GLFW_KEY_UP, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_CAMERA_OUT = new KeyBinding("Camera farther", GLFW.GLFW_KEY_DOWN, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_CAMERA_UP = new KeyBinding("Camera up", GLFW.GLFW_KEY_PAGE_UP, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_CAMERA_DOWN = new KeyBinding("Camera down", GLFW.GLFW_KEY_PAGE_DOWN, KeyHandler.KEY_CATEGORY);
	
	public static final KeyBinding KEYBIND_SWAP_SHOULDER = new KeyBinding("Swap shoulder", GLFW.GLFW_KEY_O, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_TOGGLE_SHOULDER_SURFING = new KeyBinding("Toggle perspective", InputMappings.UNKNOWN.getValue(), KeyHandler.KEY_CATEGORY);
	
	@SubscribeEvent
	public static void keyInputEvent(KeyInputEvent event)
	{
		if(Minecraft.getInstance() != null && Minecraft.getInstance().screen == null)
		{
			if(KEYBIND_TOGGLE_SHOULDER_SURFING.isDown())
			{
				if(ShoulderSurfing.STATE.doShoulderSurfing())
				{
					ShoulderSurfingHelper.setPerspective(Perspective.FIRST_PERSON);
				}
				else if(Minecraft.getInstance().options.getCameraType() == PointOfView.FIRST_PERSON)
				{
					ShoulderSurfingHelper.setPerspective(Perspective.SHOULDER_SURFING);
				}
			}
			
			if(ShoulderSurfing.STATE.doShoulderSurfing())
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
				boolean firstPerson = next.getPointOfView().isFirstPerson();
				
				if(perspective.getPointOfView().isFirstPerson() != firstPerson)
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
