package com.teamderpy.shouldersurfing.event;

import org.lwjgl.glfw.GLFW;

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
	public static final KeyBinding KEYBIND_TOGGLE_SHOULDER_SURFING = new KeyBinding("Toggle perspective", InputMappings.INPUT_INVALID.getKeyCode(), KeyHandler.KEY_CATEGORY);
	
	@SubscribeEvent
	public static void keyInputEvent(KeyInputEvent event)
	{
		if(Minecraft.getInstance() != null && Minecraft.getInstance().currentScreen == null)
		{
			if(KEYBIND_TOGGLE_SHOULDER_SURFING.isKeyDown())
			{
				if(ShoulderSurfingHelper.doShoulderSurfing())
				{
					ShoulderSurfingHelper.setPerspective(Perspective.FIRST_PERSON);
				}
				else if(Minecraft.getInstance().gameSettings.func_243230_g() == PointOfView.FIRST_PERSON)
				{
					ShoulderSurfingHelper.setPerspective(Perspective.SHOULDER_SURFING);
				}
			}
			
			if(ShoulderSurfingHelper.doShoulderSurfing())
			{
				if(KEYBIND_CAMERA_LEFT.isKeyDown())
				{
					Config.CLIENT.adjustCameraLeft();
				}
				
				if(KEYBIND_CAMERA_RIGHT.isKeyDown())
				{
					Config.CLIENT.adjustCameraRight();
				}
				
				if(KEYBIND_CAMERA_OUT.isKeyDown())
				{
					Config.CLIENT.adjustCameraOut();
				}
				
				if(KEYBIND_CAMERA_IN.isKeyDown())
				{
					Config.CLIENT.adjustCameraIn();
				}
				
				if(KEYBIND_CAMERA_UP.isKeyDown())
				{
					Config.CLIENT.adjustCameraUp();
				}
				
				if(KEYBIND_CAMERA_DOWN.isKeyDown())
				{
					Config.CLIENT.adjustCameraDown();
				}
				
				if(KEYBIND_SWAP_SHOULDER.isKeyDown())
				{
					Config.CLIENT.swapShoulder();
				}
			}
			
			if(Minecraft.getInstance().gameSettings.keyBindTogglePerspective.isPressed())
			{
				Perspective perspective = Perspective.of(Minecraft.getInstance().gameSettings.func_243230_g(), ShoulderSurfingHelper.doShoulderSurfing());
				Perspective next = perspective.next();
				ShoulderSurfingHelper.setPerspective(next);
				
				if(perspective.getPointOfView().func_243192_a() != Minecraft.getInstance().gameSettings.func_243230_g().func_243192_a())
				{
					Minecraft.getInstance().gameRenderer.loadEntityShader(Minecraft.getInstance().gameSettings.func_243230_g().func_243192_a() ? Minecraft.getInstance().getRenderViewEntity() : null);
				}
				
				if(Config.CLIENT.doRememberLastPerspective())
				{
					Config.CLIENT.setDefaultPerspective(next);
				}
			}
		}
	}
}
