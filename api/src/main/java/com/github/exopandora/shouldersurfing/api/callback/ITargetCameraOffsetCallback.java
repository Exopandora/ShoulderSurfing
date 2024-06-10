package com.github.exopandora.shouldersurfing.api.callback;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import net.minecraft.util.math.vector.Vector3d;

public interface ITargetCameraOffsetCallback
{
	default Vector3d pre(IShoulderSurfing instance, Vector3d targetOffset, Vector3d defaultOffset)
	{
		return targetOffset;
	}
	
	default Vector3d post(IShoulderSurfing instance, Vector3d targetOffset, Vector3d defaultOffset)
	{
		return targetOffset;
	}
}
