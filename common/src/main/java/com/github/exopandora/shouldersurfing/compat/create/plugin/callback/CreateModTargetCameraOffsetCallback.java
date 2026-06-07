package com.github.exopandora.shouldersurfing.compat.create.plugin.callback;

import com.github.exopandora.shouldersurfing.api.plugin.callback.ITargetCameraOffsetCallback;
import com.simibubi.create.content.trains.CameraDistanceModifier;
import net.minecraft.world.phys.Vec3;

public class CreateModTargetCameraOffsetCallback implements ITargetCameraOffsetCallback {
	@Override
	public Vec3 getTargetOffset(GetTagetCameraOffsetContext context) {
		return context.targetOffset().multiply(1.0D, 1.0D, CameraDistanceModifier.getMultiplier());
	}
}
