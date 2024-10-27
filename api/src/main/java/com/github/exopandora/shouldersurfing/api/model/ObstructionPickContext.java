package com.github.exopandora.shouldersurfing.api.model;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;

public final class ObstructionPickContext extends PickContext
{
	private final Vector3d endPos;
	
	public ObstructionPickContext(ActiveRenderInfo camera, RayTraceContext.FluidMode fluidContext, Entity entity, Vector3d endPos)
	{
		super(camera, fluidContext, entity);
		this.endPos = endPos;
	}
	
	@Override
	public RayTraceContext.BlockMode blockContext()
	{
		return ShoulderSurfing.getInstance().isAiming() ? RayTraceContext.BlockMode.COLLIDER : RayTraceContext.BlockMode.OUTLINE;
	}
	
	@Override
	public Couple<Vector3d> entityTrace(double interactionRange, float partialTick)
	{
		return this.calcRay(partialTick);
	}
	
	@Override
	public Couple<Vector3d> blockTrace(double interactionRange, float partialTick)
	{
		return this.calcRay(partialTick);
	}
	
	private Couple<Vector3d> calcRay(float partialTick)
	{
		Vector3d startPos = this.entity().getEyePosition(partialTick);
		return new Couple<Vector3d>(startPos, this.endPos);
	}
}
