package com.github.exopandora.shouldersurfing.api.model;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public final class DynamicPickContext extends PickContext
{
	private final PickVector pickVector;
	
	public DynamicPickContext(Camera camera, ClipContext.Fluid fluidContext, Entity entity, Predicate<Entity> entityFilter, PickVector pickVector)
	{
		super(camera, fluidContext, entity, entityFilter);
		this.pickVector = pickVector;
	}
	
	@Override
	public ClipContext.Block blockContext()
	{
		return ShoulderSurfing.getInstance().isAiming() ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE;
	}
	
	@Override
	public Couple<Vec3> entityTrace(double interactionRange, float partialTick)
	{
		return calcRay(this.camera(), this.entity(), interactionRange, partialTick, this.pickVector);
	}
	
	@Override
	public Couple<Vec3> blockTrace(double interactionRange, float partialTick)
	{
		return calcRay(this.camera(), this.entity(), interactionRange, partialTick, this.pickVector);
	}
	
	private static Couple<Vec3> calcRay(Camera camera, Entity entity, double interactionRange, float partialTick, PickVector pickVector)
	{
		Vec3 startPos = entity.getEyePosition(partialTick);
		Vec3 viewVector = pickVector.calc(camera, entity, partialTick);
		Vec3 endPos = startPos.add(viewVector.scale(interactionRange));
		return new Couple<Vec3>(startPos, endPos);
	}
}
