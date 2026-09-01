package com.github.exopandora.shouldersurfing.api.client.world.phys;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockCollisionPredicate {
	boolean hasNoCollision(BlockState state, BlockGetter level, BlockPos pos);
}
