package com.github.exopandora.shouldersurfing.api.client.world.phys;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.function.Predicate;

import static net.minecraft.world.level.BlockGetter.traverseBlocks;

public interface IObjectPicker {
	HitResult pick(PickContext context, double interactionRange, float partialTick, Player player);
	
	EntityHitResult pickEntities(PickContext context, double interactionRange, float partialTick);
	
	BlockHitResult pickBlocks(PickContext context, double interactionRange, float partialTick);
	
	static double maxInteractionRange(Player player) {
		return Math.max(player.blockInteractionRange(), player.entityInteractionRange());
	}
	
	static BlockHitResult clip(BlockGetter level, ClipContext clipContext, Predicate<BlockState> blockStateFilter) {
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
				if (!blockStateFilter.test(blockState)) {
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
