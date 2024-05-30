package com.github.exopandora.shouldersurfing.api.model;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;

public final class DynamicPickContext extends PickContext
{
	public DynamicPickContext(Camera camera, ClipContext.Fluid fluidContext, Entity entity)
	{
		super(camera, fluidContext, entity);
	}
	
	@Override
	public ClipContext.Block blockContext()
	{
		return ShoulderSurfing.getInstance().isAiming() ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE;
	}
	
	@Override
	public Couple<Vec3> entityTrace(double interactionRange, float partialTick)
	{
		return this.calcRay(this.camera(), interactionRange, partialTick);
	}
	
	@Override
	public Couple<Vec3> blockTrace(double interactionRange, float partialTick)
	{
		return this.calcRay(this.camera(), interactionRange, partialTick);
	}
	
	private Couple<Vec3> calcRay(Camera camera, double interactionRange, float partialTick)
	{
		Vec3 startPos = this.entity().getEyePosition(partialTick);
		Vec3 endPos = startPos.add(new Vec3(camera.getLookVector()).scale(interactionRange));
		return new Couple<Vec3>(startPos, endPos);
	}
}
