package com.github.exopandora.shouldersurfing.api.model;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;

public final class DynamicPickContext extends PickContext
{
	public DynamicPickContext(ActiveRenderInfo camera, RayTraceContext.FluidMode fluidContext, Entity entity)
	{
		super(camera, fluidContext, entity);
	}
	
	@Override
	public RayTraceContext.BlockMode blockContext()
	{
		return ShoulderSurfing.getInstance().isAiming() ? RayTraceContext.BlockMode.COLLIDER : RayTraceContext.BlockMode.OUTLINE;
	}
	
	@Override
	public Couple<Vector3d> entityTrace(double interactionRange, float partialTick)
	{
		return this.calcRay(this.camera(), interactionRange, partialTick);
	}
	
	@Override
	public Couple<Vector3d> blockTrace(double interactionRange, float partialTick)
	{
		return this.calcRay(this.camera(), interactionRange, partialTick);
	}
	
	private Couple<Vector3d> calcRay(ActiveRenderInfo camera, double interactionRange, float partialTick)
	{
		Vector3d startPos = this.entity().getEyePosition(partialTick);
		Vector3d endPos = startPos.add(new Vector3d(camera.getLookVector()).scale(interactionRange));
		return new Couple<Vector3d>(startPos, endPos);
	}
}
