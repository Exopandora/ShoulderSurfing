package com.github.exopandora.shouldersurfing.api.model;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;

public final class DynamicPickContext extends PickContext
{
	private final PickVector pickVector;
	
	public DynamicPickContext(ActiveRenderInfo camera, RayTraceContext.FluidMode fluidContext, Entity entity, PickVector pickVector)
	{
		super(camera, fluidContext, entity);
		this.pickVector = pickVector;
	}
	
	@Override
	public RayTraceContext.BlockMode blockContext()
	{
		return ShoulderSurfing.getInstance().isAiming() ? RayTraceContext.BlockMode.COLLIDER : RayTraceContext.BlockMode.OUTLINE;
	}
	
	@Override
	public Couple<Vector3d> entityTrace(double interactionRange, float partialTick)
	{
		return calcRay(this.camera(), this.entity(), interactionRange, partialTick, this.pickVector);
	}
	
	@Override
	public Couple<Vector3d> blockTrace(double interactionRange, float partialTick)
	{
		return calcRay(this.camera(), this.entity(), interactionRange, partialTick, this.pickVector);
	}
	
	private Couple<Vector3d> calcRay(ActiveRenderInfo camera, Entity entity, double interactionRange, float partialTick, PickVector pickVector)
	{
		Vector3d startPos = this.entity().getEyePosition(partialTick);
		Vector3d viewVector = pickVector.calc(camera, entity, partialTick);
		Vector3d endPos = startPos.add(viewVector.scale(interactionRange));
		return new Couple<Vector3d>(startPos, endPos);
	}
}
