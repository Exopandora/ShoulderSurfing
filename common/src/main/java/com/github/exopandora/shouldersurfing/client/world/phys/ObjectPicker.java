package com.github.exopandora.shouldersurfing.client.world.phys;

import com.github.exopandora.shouldersurfing.api.client.world.phys.BlockCollisionPredicate;
import com.github.exopandora.shouldersurfing.api.client.world.phys.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.level.BlockGetter.traverseBlocks;

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
		var viewVector = new Vec3(context.camera().forwardVector()).scale(interactionRange);
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
	
	@Override
	public BlockHitResult clip(BlockGetter level, ClipContext clipContext, BlockCollisionPredicate blockCollisionPredicate) {
		return traverseBlocks(
			clipContext.getFrom(),
			clipContext.getTo(),
			clipContext,
			(context, pos) -> {
				var blockState = level.getBlockState(pos);
				var fluidState = level.getFluidState(pos);
				var from = context.getFrom();
				var to = context.getTo();
				var blockShape = context.getBlockShape(blockState, level, pos);
				var blockResult = level.clipWithInteractionOverride(from, to, pos, blockShape, blockState);
				if (blockCollisionPredicate.hasNoCollision(blockState, level, pos)) {
					blockResult = null;
				}
				var fluidShape = context.getFluidShape(fluidState, level, pos);
				var liquidResult = fluidShape.clip(from, to, pos);
				var blockDistanceSquared = blockResult == null
					? Double.MAX_VALUE
					: context.getFrom().distanceToSqr(blockResult.getLocation());
				var liquidDistanceSquared = liquidResult == null
					? Double.MAX_VALUE
					: context.getFrom().distanceToSqr(liquidResult.getLocation());
				return blockDistanceSquared <= liquidDistanceSquared ? blockResult : liquidResult;
			},
			context -> BlockHitResult.miss(
				context.getTo(),
				Direction.getApproximateNearest(context.getFrom().subtract(context.getTo())),
				BlockPos.containing(context.getTo())
			)
		);
	}
}
