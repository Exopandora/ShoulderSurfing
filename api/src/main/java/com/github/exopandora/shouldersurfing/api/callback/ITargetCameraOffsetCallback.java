package com.github.exopandora.shouldersurfing.api.callback;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import net.minecraft.world.phys.Vec3;

public interface ITargetCameraOffsetCallback
{
	default Vec3 pre(IShoulderSurfing instance, Vec3 targetOffset, Vec3 defaultOffset)
	{
		return targetOffset;
	}
	
	default Vec3 post(IShoulderSurfing instance, Vec3 targetOffset, Vec3 defaultOffset)
	{
		return targetOffset;
	}
}
