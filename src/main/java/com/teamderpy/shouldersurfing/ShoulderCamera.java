package com.teamderpy.shouldersurfing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ShoulderCamera
{
	/**
	 * The number of degrees to rotate the camera
	 */
	public static float SHOULDER_ROTATION;
	
	/**
	 * How much the camera view distance should change
	 */
	public static float SHOULDER_ZOOM_MOD = 0.70F;
	
	public static void adjustCameraLeft()
	{
		if(ShoulderSettings.IS_ROTATION_UNLIMITED || SHOULDER_ROTATION < ShoulderSettings.ROTATION_MAXIMUM)
		{
			SHOULDER_ROTATION += 0.5F;
		}
	}
	
	public static void adjustCameraRight()
	{
		if(ShoulderSettings.IS_ROTATION_UNLIMITED || SHOULDER_ROTATION > ShoulderSettings.ROTATION_MINIMUM)
		{
			SHOULDER_ROTATION -= 0.5F;
		}
	}
	
	public static void adjustCameraIn()
	{
		if(ShoulderSettings.IS_ZOOM_UNLIMITED || SHOULDER_ZOOM_MOD < ShoulderSettings.ZOOM_MAXIMUM)
		{
			SHOULDER_ZOOM_MOD += 0.01F;
		}
	}
	
	public static void adjustCameraOut()
	{
		if(ShoulderSettings.IS_ZOOM_UNLIMITED || SHOULDER_ZOOM_MOD > ShoulderSettings.ZOOM_MINIMUM)
		{
			SHOULDER_ZOOM_MOD -= 0.01F;
		}
	}
	
	public static void swapShoulder()
	{
		SHOULDER_ROTATION = -SHOULDER_ROTATION;
	}
}
