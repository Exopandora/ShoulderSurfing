package com.github.exopandora.shouldersurfing.api.client.world.phys;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public interface IObjectPicker {
	HitResult pick(PickContext context, double interactionRange, float partialTick, Player player);
	
	EntityHitResult pickEntities(PickContext context, double interactionRange, float partialTick);
	
	BlockHitResult pickBlocks(PickContext context, double interactionRange, float partialTick);
	
	static double maxInteractionRange(Player player) {
		return Math.max(player.blockInteractionRange(), player.entityInteractionRange());
	}
}
