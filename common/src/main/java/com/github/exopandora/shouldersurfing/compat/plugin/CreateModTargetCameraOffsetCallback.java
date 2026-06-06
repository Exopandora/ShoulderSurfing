package com.github.exopandora.shouldersurfing.compat.plugin;

import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
import com.simibubi.create.content.trains.CameraDistanceModifier;
import net.minecraft.world.phys.Vec3;

public class CreateModTargetCameraOffsetCallback implements ITargetCameraOffsetCallback
{
	@Override
	public Vec3 getTargetOffset(Context context)
	{
		return context.targetOffset().multiply(1.0D, 1.0D, CameraDistanceModifier.getMultiplier());
	}
}
