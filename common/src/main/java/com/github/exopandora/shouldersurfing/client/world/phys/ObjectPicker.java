package com.github.exopandora.shouldersurfing.client.world.phys;

import com.github.exopandora.shouldersurfing.api.client.world.phys.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ObjectPicker implements IObjectPicker {
	@Override
	public HitResult pick(PickContext context, double interactionRangeOverride, float partialTick, Player player) {
		var interactionRange = Math.max(IObjectPicker.maxInteractionRange(player), interactionRangeOverride);
		var blockHit = this.pickBlocks(context, interactionRange, partialTick);
		var eyePosition = context.entity().getEyePosition(partialTick);
		if (blockHit.getType() != HitResult.Type.MISS) {
			interactionRange = blockHit.getLocation().distanceTo(eyePosition);
		}
		var entityHit = this.pickEntities(context, interactionRange, partialTick);
		if (entityHit != null) {
			var distance = eyePosition.distanceTo(entityHit.getLocation());
			if (distance < interactionRange || blockHit.getType() != HitResult.Type.MISS) {
				return entityHit;
			}
		}
		return blockHit;
	}
	
	@Override
	public EntityHitResult pickEntities(PickContext context, double interactionRange, float partialTick) {
		var viewVector = new Vec3(context.camera().getLookVector()).scale(interactionRange);
		var aabb = context.entity().getBoundingBox()
			.expandTowards(viewVector)
			.inflate(1.0D, 1.0D, 1.0D);
		var entityRay = context.entityTrace(interactionRange, partialTick);
		var interactionRangeSq = entityRay.left().distanceToSqr(entityRay.right());
		return ProjectileUtil.getEntityHitResult(
			context.entity(), entityRay.left(), entityRay.right(), aabb, context.entityFilter(), interactionRangeSq
		);
	}
	
	@Override
	public BlockHitResult pickBlocks(PickContext context, double interactionRange, float partialTick) {
		return context.entity().level().clip(context.toClipContext(interactionRange, partialTick));
	}
}
