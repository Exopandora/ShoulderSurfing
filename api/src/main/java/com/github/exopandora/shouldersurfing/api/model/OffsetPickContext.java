package com.github.exopandora.shouldersurfing.api.model;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiFunction;

public final class OffsetPickContext extends PickContext
{
	public OffsetPickContext(Camera camera, ClipContext.Fluid fluidContext, Entity entity)
	{
		super(camera, fluidContext, entity);
	}
	
	@Override
	public ClipContext.Block blockContext()
	{
		IShoulderSurfing instance = ShoulderSurfing.getInstance();
		
		if(instance.isAiming() || instance.getCrosshairRenderer().isCrosshairDynamic(this.entity()))
		{
			return ClipContext.Block.COLLIDER;
		}
		
		return ClipContext.Block.OUTLINE;
	}
	
	@Override
	public Couple<Vec3> entityTrace(double interactionRange, float partialTick)
	{
		return this.calcRay(this.camera(), interactionRange, partialTick, Vec3::add);
	}
	
	@Override
	public Couple<Vec3> blockTrace(double interactionRange, float partialTick)
	{
		return this.calcRay(this.camera(), interactionRange, partialTick, (eyePosition, rayTraceStartOffset) -> this.camera().getPosition());
	}
	
	private Couple<Vec3> calcRay(Camera camera, double interactionRange, float partialTick, BiFunction<Vec3, Vec3, Vec3> startPosFunc)
	{
		Vec3 eyePosition = this.entity().getEyePosition(partialTick);
		Vec3 cameraPos = camera.getPosition();
		Vec3 cameraOffset = cameraPos.subtract(eyePosition);
		Vec3 renderOffset = ShoulderSurfing.getInstance().getCamera().getRenderOffset();
		Vec3 rayTraceStartOffset = new Vec3(camera.getLeftVector()).scale(renderOffset.x())
			.add(new Vec3(camera.getUpVector()).scale(renderOffset.y()));
		Vec3 viewVector = new Vec3(camera.getLookVector());
		double interactionRangeSq = Mth.square(interactionRange);
		
		if(rayTraceStartOffset.lengthSqr() < interactionRangeSq)
		{
			interactionRange = Math.sqrt(interactionRangeSq - rayTraceStartOffset.lengthSqr());
		}
		
		double distance = interactionRange + cameraOffset.distanceTo(rayTraceStartOffset);
		Vec3 startPos = startPosFunc.apply(eyePosition, rayTraceStartOffset);
		Vec3 endPos = cameraPos.add(viewVector.scale(distance));
		return new Couple<Vec3>(startPos, endPos);
	}
}
