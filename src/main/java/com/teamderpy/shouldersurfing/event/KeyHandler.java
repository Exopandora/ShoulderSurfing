package com.teamderpy.shouldersurfing.event;

import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class KeyHandler
{
	@SubscribeEvent
	public static void keyInputEvent(KeyInputEvent event)
	{
		if(Minecraft.getInstance() != null && Minecraft.getInstance().currentScreen == null)
		{
			if(Minecraft.getInstance().gameSettings.thirdPersonView == Config.CLIENT.getShoulderSurfing3ppId())
			{
				if(ShoulderSurfing.KEYBIND_ROTATE_CAMERA_LEFT.isKeyDown())
				{
					Config.CLIENT.adjustCameraLeft();
				}
				
				if(ShoulderSurfing.KEYBIND_ROTATE_CAMERA_RIGHT.isKeyDown())
				{
					Config.CLIENT.adjustCameraRight();
				}
				
				if(ShoulderSurfing.KEYBIND_ZOOM_CAMERA_IN.isKeyDown())
				{
					Config.CLIENT.adjustCameraIn();
				}
				
				if(ShoulderSurfing.KEYBIND_ZOOM_CAMERA_OUT.isKeyDown())
				{
					Config.CLIENT.adjustCameraOut();
				}
				
				if(ShoulderSurfing.KEYBIND_SWAP_SHOULDER.isPressed())
				{
					Config.CLIENT.swapShoulder();
				}
			}
		}
	}
}
