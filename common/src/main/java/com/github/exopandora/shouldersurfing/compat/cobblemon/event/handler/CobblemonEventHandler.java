package com.github.exopandora.shouldersurfing.compat.cobblemon.event.handler;

import com.cobblemon.mod.common.item.PokeBallItem;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerAimStateEvent;
import net.minecraft.world.item.ItemStack;

public enum CobblemonEventHandler {
	INSTANCE;
	
	public void computePlayerAimState(ComputePlayerAimStateEvent event) {
		if (isAdaptiveItemStack(event.getEntity().getMainHandItem()) || isAdaptiveItemStack(event.getEntity().getOffhandItem())) {
			event.setResult(true);
		}
	}
	
	private static boolean isAdaptiveItemStack(ItemStack stack) {
		return stack.getItem() instanceof PokeBallItem;
	}
}
