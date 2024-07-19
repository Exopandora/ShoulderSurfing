package com.github.exopandora.shouldersurfing.compat.plugin;

import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;

public class CreateModTargetCameraOffsetCallback implements ITargetCameraOffsetCallback
{
	private final Method getMultiplierMethod;
	
	public CreateModTargetCameraOffsetCallback() throws ClassNotFoundException, NoSuchMethodException, SecurityException
	{
		Class<?> cameraDistanceModifierClass = Class.forName("com.simibubi.create.content.trains.CameraDistanceModifier");
		this.getMultiplierMethod = cameraDistanceModifierClass.getDeclaredMethod("getMultiplier");
	}
	
	@Override
	public Vec3 post(IShoulderSurfing instance, Vec3 targetOffset, Vec3 defaultOffset)
	{
		try
		{
			return targetOffset.multiply(1.0D, 1.0D, (float) this.getMultiplierMethod.invoke(null));
		}
		catch(Exception e)
		{
			return targetOffset;
		}
	}
}
