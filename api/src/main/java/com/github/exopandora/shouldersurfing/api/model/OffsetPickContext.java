package com.github.exopandora.shouldersurfing.api.model;

import com.github.exopandora.shouldersurfing.api.accessors.ActiveRenderInfoAccessor;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.BiFunction;

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
		return this.calcRay(this.camera(), interactionRange, partialTick, Vector3d::add);
	}
	
	@Override
	public Couple<Vector3d> blockTrace(double interactionRange, float partialTick)
	{
		return this.calcRay(this.camera(), interactionRange, partialTick, (eyePosition, rayTraceStartOffset) -> this.camera().getPosition());
	}
	
	private Couple<Vector3d> calcRay(ActiveRenderInfo camera, double interactionRange, float partialTick, BiFunction<Vector3d, Vector3d, Vector3d> startPosFunc)
	{
		Vector3d eyePosition = this.entity().getEyePosition(partialTick);
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
		Vector3d startPos = startPosFunc.apply(eyePosition, rayTraceStartOffset);
		Vector3d endPos = cameraPos.add(viewVector.scale(distance));
		return new Couple<Vector3d>(startPos, endPos);
	}
}
