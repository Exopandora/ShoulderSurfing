package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.client.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.model.Couple;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class ObjectPicker implements IObjectPicker
{
	@Override
	public RayTraceResult pick(PickContext context, double interactionRangeOverride, float partialTick, PlayerController gameMode)
	{
		double interactionRange = Math.max(gameMode.getPickRange(), interactionRangeOverride);
		RayTraceResult blockHit = this.pickBlocks(context, interactionRange, partialTick);
		Vector3d eyePosition = context.entity().getEyePosition(partialTick);
		
		if(gameMode.hasFarPickRange())
		{
			interactionRange = Math.max(interactionRange, 6.0D);
		}
		
		if(blockHit.getType() != RayTraceResult.Type.MISS)
		{
			interactionRange = blockHit.getLocation().distanceTo(eyePosition);
		}
		
		EntityRayTraceResult entityHit = this.pickEntities(context, interactionRange, partialTick);
		
		if(entityHit != null)
		{
			double distance = eyePosition.distanceTo(entityHit.getLocation());
			
			if(distance < interactionRange || blockHit.getType() != RayTraceResult.Type.MISS)
			{
				return entityHit;
			}
		}
		
		return blockHit;
	}
	
	@Override
	public EntityRayTraceResult pickEntities(PickContext context, double interactionRange, float partialTick)
	{
		Vector3d viewVector = new Vector3d(context.camera().getLookVector()).scale(interactionRange);
		AxisAlignedBB aabb = context.entity().getBoundingBox()
			.expandTowards(viewVector)
			.inflate(1.0D, 1.0D, 1.0D);
		Couple<Vector3d> entityRay = context.entityTrace(interactionRange, partialTick);
		double interactionRangeSq = entityRay.left().distanceToSqr(entityRay.right());
		return ProjectileHelper.getEntityHitResult(context.entity(), entityRay.left(), entityRay.right(), aabb, context.entityFilter(), interactionRangeSq);
	}
	
	@Override
	public BlockRayTraceResult pickBlocks(PickContext context, double interactionRange, float partialTick)
	{
		return context.entity().level.clip(context.toClipContext(interactionRange, partialTick));
	}
}
