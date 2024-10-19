package com.github.exopandora.shouldersurfing.api.model;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;

public final class HybridPickContext
extends DynamicPickContext
{
	private final Vec3 endPos;

	public HybridPickContext(Camera camera, ClipContext.Fluid fluidContext, Entity entity, Vec3 endPos)
	{
		super(camera, fluidContext, entity);
		this.endPos = endPos;
	}

	@Override
	protected Couple<Vec3> calcRay(Camera camera, Entity entity, double interactionRange, float partialTick, PickVector pickVector)
	{
		Vec3 startPos = entity.getEyePosition(partialTick);
		return new Couple<Vec3>(startPos, endPos);
	}
}
