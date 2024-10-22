package com.github.exopandora.shouldersurfing.api.model;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;

public final class ObstructionPickContext extends PickContext
{
	private final Vec3 target;
	
	public ObstructionPickContext(Camera camera, ClipContext.Fluid fluidContext, Entity entity, Vec3 target)
	{
		super(camera, fluidContext, entity);
		this.target = target;
	}
	
	@Override
	public ClipContext.Block blockContext()
	{
		return ShoulderSurfing.getInstance().isAiming() ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE;
	}
	
	@Override
	public Couple<Vec3> entityTrace(double interactionRange, float partialTick)
	{
		return calcRay(partialTick);
	}
	
	@Override
	public Couple<Vec3> blockTrace(double interactionRange, float partialTick)
	{
		return calcRay(partialTick);
	}
	
	private Couple<Vec3> calcRay(float partialTick)
	{
		Vec3 startPos = this.entity().getEyePosition(partialTick);
		return new Couple<Vec3>(startPos, target);
	}
}
