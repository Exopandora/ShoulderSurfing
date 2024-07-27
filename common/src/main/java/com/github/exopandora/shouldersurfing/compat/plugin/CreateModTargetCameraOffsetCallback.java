package com.github.exopandora.shouldersurfing.compat.plugin;

import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.simibubi.create.content.trains.CameraDistanceModifier;
import net.minecraft.world.phys.Vec3;

public class CreateModTargetCameraOffsetCallback implements ITargetCameraOffsetCallback
{
	@Override
	public Vec3 post(IShoulderSurfing instance, Vec3 targetOffset, Vec3 defaultOffset)
	{
		return targetOffset.multiply(1.0D, 1.0D, CameraDistanceModifier.getMultiplier());
	}
}
