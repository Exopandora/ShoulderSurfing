package com.teamderpy.shouldersurfing.util;

import com.teamderpy.shouldersurfing.math.Vec2f;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;

public class ShoulderState
{
	private static boolean enabled;
	private static boolean switchPerspective;
	private static boolean isAiming;
	
	private static Vec2f lastTranslation = Vec2f.ZERO;
	private static Vec2f translation = Vec2f.ZERO;
	private static Vec2f projected = null;
	
	private static double cameraDistance;
	
	public static boolean doSwitchPerspective()
	{
		return ShoulderState.switchPerspective;
	}
	
	public static void setSwitchPerspective(boolean switchPerspective)
	{
		ShoulderState.switchPerspective = switchPerspective;
	}
	
	public static Vec2f getLastTranslation()
	{
		return ShoulderState.lastTranslation;
	}
	
	public static void setLastTranslation(Vec2f lastTranslation)
	{
		ShoulderState.lastTranslation = lastTranslation;
	}
	
	public static Vec2f getTranslation()
	{
		return ShoulderState.translation;
	}
	
	public static void setTranslation(Vec2f translation)
	{
		ShoulderState.translation = translation;
	}
	
	public static Vec2f getProjected()
	{
		return ShoulderState.projected;
	}
	
	public static void setProjected(Vec2f projected)
	{
		ShoulderState.projected = projected;
	}
	
	public static boolean isAiming()
	{
		return ShoulderState.isAiming;
	}
	
	public static void setAiming(boolean isAiming)
	{
		ShoulderState.isAiming = isAiming;
	}
	
	public static double getCameraDistance()
	{
		return ShoulderState.cameraDistance;
	}
	
	public static void setCameraDistance(double cameraDistance)
	{
		ShoulderState.cameraDistance = cameraDistance;
	}
	
	public static boolean isEnabled()
	{
		return ShoulderState.enabled;
	}
	
	public static void setEnabled(boolean enabled)
	{
		ShoulderState.enabled = enabled;
	}
	
	public static boolean doShoulderSurfing()
	{
		return ShoulderState.enabled && CameraType.THIRD_PERSON_BACK.equals(Minecraft.getInstance().options.getCameraType());
	}
}
