package com.github.exopandora.shouldersurfing.api.model;

import com.github.exopandora.shouldersurfing.api.accessors.ActiveRenderInfoAccessor;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;

public final class OffsetPickContext extends PickContext
{
	public OffsetPickContext(ActiveRenderInfo camera, RayTraceContext.FluidMode fluidContext, Entity entity)
	{
		super(camera, fluidContext, entity);
	}
	
	@Override
	public RayTraceContext.BlockMode blockContext()
	{
		IShoulderSurfing instance = ShoulderSurfing.getInstance();
		
		if(instance.isAiming() || instance.getCrosshairRenderer().isCrosshairDynamic(this.entity()))
		{
			return RayTraceContext.BlockMode.COLLIDER;
		}
		
		return RayTraceContext.BlockMode.OUTLINE;
	}
	
	@Override
	public Couple<Vector3d> entityTrace(double interactionRange, float partialTick)
	{
		return calcRay(this.camera(), this.entity(), interactionRange, partialTick, ShoulderSurfing.getInstance().getClientConfig().getEntityPickOrigin());
	}
	
	@Override
	public Couple<Vector3d> blockTrace(double interactionRange, float partialTick)
	{
		return calcRay(this.camera(), this.entity(), interactionRange, partialTick, ShoulderSurfing.getInstance().getClientConfig().getBlockPickOrigin());
	}
	
	private Couple<Vector3d> calcRay(ActiveRenderInfo camera, Entity entity, double interactionRange, float partialTick, PickOrigin pickOrigin)
	{
		Vector3d eyePosition = entity.getEyePosition(partialTick);
		Vector3d cameraPos = camera.getPosition();
		Vector3d cameraOffset = cameraPos.subtract(eyePosition);
		Vector3d renderOffset = ShoulderSurfing.getInstance().getCamera().getRenderOffset();
		Vector3d rayTraceStartOffset = new Vector3d(((ActiveRenderInfoAccessor) camera).getLeft()).scale(renderOffset.x())
			.add(new Vector3d(camera.getUpVector()).scale(renderOffset.y()));
		Vector3d viewVector = new Vector3d(camera.getLookVector());
		double interactionRangeSq = interactionRange * interactionRange;
		
		if(rayTraceStartOffset.lengthSqr() < interactionRangeSq)
		{
			interactionRange = Math.sqrt(interactionRangeSq - rayTraceStartOffset.lengthSqr());
		}
		
		double distance = interactionRange + cameraOffset.distanceTo(rayTraceStartOffset);
		Vector3d startPos = pickOrigin.calc(cameraPos, eyePosition, rayTraceStartOffset);
		Vector3d endPos = cameraPos.add(viewVector.scale(distance));
		return new Couple<Vector3d>(startPos, endPos);
	}
}
