package com.github.exopandora.shouldersurfing.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public class BlockHelper {
	public static boolean isPillarPart(BlockGetter level, BlockPos pos) {
		return level.getBlockState(pos.north()).getCollisionShape(level, pos).isEmpty()
			&& level.getBlockState(pos.east()).getCollisionShape(level, pos).isEmpty()
			&& level.getBlockState(pos.south()).getCollisionShape(level, pos).isEmpty()
			&& level.getBlockState(pos.west()).getCollisionShape(level, pos).isEmpty();
	}
}
