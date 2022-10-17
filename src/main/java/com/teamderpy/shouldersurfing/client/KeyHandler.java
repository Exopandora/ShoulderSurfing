package com.teamderpy.shouldersurfing.client;

import org.lwjgl.input.Keyboard;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyHandler
{
	private static final String KEY_CATEGORY = "Shoulder Surfing";
	
	public static final KeyBinding KEYBIND_CAMERA_LEFT = new KeyBinding("Camera left", Keyboard.KEY_LEFT, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_CAMERA_RIGHT = new KeyBinding("Camera right", Keyboard.KEY_RIGHT, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_CAMERA_IN = new KeyBinding("Camera closer", Keyboard.KEY_UP, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_CAMERA_OUT = new KeyBinding("Camera farther", Keyboard.KEY_DOWN, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_CAMERA_UP = new KeyBinding("Camera up", Keyboard.KEY_PRIOR, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_CAMERA_DOWN = new KeyBinding("Camera down", Keyboard.KEY_NEXT, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_SWAP_SHOULDER = new KeyBinding("Swap shoulder", Keyboard.KEY_O, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_TOGGLE_SHOULDER_SURFING = new KeyBinding("Toggle perspective", Keyboard.KEY_NONE, KeyHandler.KEY_CATEGORY);
	
	public static void onKeyInput()
	{
		if(Minecraft.getMinecraft() != null && Minecraft.getMinecraft().currentScreen == null)
		{
			ShoulderInstance shoulderInstance = ShoulderInstance.getInstance();
			
			if(KEYBIND_TOGGLE_SHOULDER_SURFING.isPressed())
			{
				if(shoulderInstance.doShoulderSurfing())
				{
					shoulderInstance.changePerspective(Perspective.FIRST_PERSON);
				}
				else if(Minecraft.getMinecraft().gameSettings.thirdPersonView == Perspective.FIRST_PERSON.getPointOfView())
				{
					shoulderInstance.changePerspective(Perspective.SHOULDER_SURFING);
				}
			}
			
			if(ShoulderInstance.getInstance().doShoulderSurfing())
			{
				if(KEYBIND_CAMERA_LEFT.isPressed())
				{
					Config.CLIENT.adjustCameraLeft();
				}
				
				if(KEYBIND_CAMERA_RIGHT.isPressed())
				{
					Config.CLIENT.adjustCameraRight();
				}
				
				if(KEYBIND_CAMERA_OUT.isPressed())
				{
					Config.CLIENT.adjustCameraOut();
				}
				
				if(KEYBIND_CAMERA_IN.isPressed())
				{
					Config.CLIENT.adjustCameraIn();
				}
				
				if(KEYBIND_CAMERA_UP.isPressed())
				{
					Config.CLIENT.adjustCameraUp();
				}
				
				if(KEYBIND_CAMERA_DOWN.isPressed())
				{
					Config.CLIENT.adjustCameraDown();
				}
				
				if(KEYBIND_SWAP_SHOULDER.isPressed())
				{
					Config.CLIENT.swapShoulder();
				}
			}
			
			if(Minecraft.getMinecraft().gameSettings.keyBindTogglePerspective.isPressed())
			{
				Perspective perspective = Perspective.current();
				Perspective next = perspective.next();
				shoulderInstance.changePerspective(next);
				
				if(Config.CLIENT.doRememberLastPerspective())
				{
					Config.CLIENT.setDefaultPerspective(next);
				}
			}
		}
	}
}
