package com.github.exopandora.shouldersurfing.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;

public class BlockHelper {
	public static boolean isPillarPart(BlockGetter level, BlockPos pos) {
		return level.getBlockState(pos.north()).getBlock() == Blocks.AIR
			&& level.getBlockState(pos.east()).getBlock() == Blocks.AIR
			&& level.getBlockState(pos.south()).getBlock() == Blocks.AIR
			&& level.getBlockState(pos.west()).getBlock() == Blocks.AIR;
	}
}
