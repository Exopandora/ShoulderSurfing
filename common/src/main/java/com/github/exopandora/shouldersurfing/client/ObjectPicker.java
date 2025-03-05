package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.client.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.model.Couple;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ObjectPicker implements IObjectPicker
{
	@Override
	public HitResult pick(PickContext context, double interactionRangeOverride, float partialTick, Player player)
	{
		double interactionRange = Math.max(IObjectPicker.maxInteractionRange(player), interactionRangeOverride);
		HitResult blockHit = this.pickBlocks(context, interactionRange, partialTick);
		Vec3 eyePosition = context.entity().getEyePosition(partialTick);
		
		if(blockHit.getType() != HitResult.Type.MISS)
		{
			interactionRange = blockHit.getLocation().distanceTo(eyePosition);
		}
		
		EntityHitResult entityHit = this.pickEntities(context, interactionRange, partialTick);
		
		if(entityHit != null)
		{
			double distance = eyePosition.distanceTo(entityHit.getLocation());
			
			if(distance < interactionRange || blockHit.getType() != HitResult.Type.MISS)
			{
				return entityHit;
			}
		}
		
		return blockHit;
	}
	
	@Override
	public EntityHitResult pickEntities(PickContext context, double interactionRange, float partialTick)
	{
		Vec3 viewVector = new Vec3(context.camera().getLookVector()).scale(interactionRange);
		AABB aabb = context.entity().getBoundingBox()
			.expandTowards(viewVector)
			.inflate(1.0D, 1.0D, 1.0D);
		Couple<Vec3> entityRay = context.entityTrace(interactionRange, partialTick);
		double interactionRangeSq = entityRay.left().distanceToSqr(entityRay.right());
		return ProjectileUtil.getEntityHitResult(context.entity(), entityRay.left(), entityRay.right(), aabb, context.entityFilter(), interactionRangeSq);
	}
	
	@Override
	public BlockHitResult pickBlocks(PickContext context, double interactionRange, float partialTick)
	{
		return context.entity().level().clip(context.toClipContext(interactionRange, partialTick));
	}
}
