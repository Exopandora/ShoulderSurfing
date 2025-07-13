package com.github.exopandora.shouldersurfing.api.callback;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import net.minecraft.world.entity.Entity;

public interface ICameraEntityTransparencyCallback
{
	float getCameraEntityAlpha(IShoulderSurfing instance, Entity cameraEntity, float partialTick);
}
