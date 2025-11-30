package com.github.exopandora.shouldersurfing.compat.plugin;

import com.cobblemon.mod.common.item.PokeBallItem;
import com.cobblemon.mod.common.item.interactive.PokerodItem;
import com.github.exopandora.shouldersurfing.api.callback.IAdaptiveItemCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CobblemonAdaptiveItemCallback implements IAdaptiveItemCallback
{
	@Override
	public boolean isHoldingAdaptiveItem(Minecraft minecraft, LivingEntity entity)
	{
		return isAdaptiveItemStack(entity.getMainHandItem()) || isAdaptiveItemStack(entity.getOffhandItem());
	}
	
	private boolean isAdaptiveItemStack(ItemStack stack)
	{
		return stack.getItem() instanceof PokeBallItem || stack.getItem() instanceof PokerodItem;
	}
}
