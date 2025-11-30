package com.github.exopandora.shouldersurfing.compat.plugin;

import com.cobblemon.mod.common.item.PokeBallItem;
import com.cobblemon.mod.common.item.interactive.PokerodItem;
import com.github.exopandora.shouldersurfing.api.callback.IAdaptiveItemCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

import java.util.stream.StreamSupport;

public class CobblemonAdaptiveItemCallback implements IAdaptiveItemCallback
{
	@Override
	public boolean isHoldingAdaptiveItem(Minecraft minecraft, LivingEntity entity)
	{
		return StreamSupport.stream(entity.getHandSlots().spliterator(), false)
			.anyMatch(stack -> stack.getItem() instanceof PokeBallItem || stack.getItem() instanceof PokerodItem);
	}
}
