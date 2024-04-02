package com.github.exopandora.shouldersurfing.testplugin;

import com.github.exopandora.shouldersurfing.api.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.api.IShoulderSurfingRegistrar;
import com.github.exopandora.shouldersurfing.api.callback.IAdaptiveItemCallback;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AdaptiveItemCallbackPlugin implements IShoulderSurfingPlugin
{
	@Override
	public void register(IShoulderSurfingRegistrar registrar)
	{
		// Register simple ItemStack predicate
		registrar.registerAdaptiveItemCallback(itemStack -> itemStack.getItem().equals(Items.APPLE));
		// Register another one
		registrar.registerAdaptiveItemCallback(itemStack -> itemStack.getItem().equals(Items.GOLDEN_APPLE));
		
		// Register a full callback as inner class
		registrar.registerAdaptiveItemCallback(new IAdaptiveItemCallback()
		{
			@Override
			public boolean isHoldingAdaptiveItem(Minecraft minecraft, LivingEntity entity)
			{
				for(ItemStack stack : entity.getHandSlots())
				{
					if(stack.getItem().equals(Items.ENCHANTED_GOLDEN_APPLE))
					{
						return true;
					}
				}
				return false;
			}
		});
		
		// Register a full callback as lambda
		registrar.registerAdaptiveItemCallback((minecraft, entity) ->
		{
			for(ItemStack stack : entity.getHandSlots())
			{
				if(stack.getItem().equals(Items.NETHERITE_BLOCK))
				{
					return true;
				}
			}
			return false;
		});
		
		// Use static helper functions to create callback
		registrar.registerAdaptiveItemCallback(IAdaptiveItemCallback.anyHandMatches(Blocks.DIRT, Items.GRASS_BLOCK));
		registrar.registerAdaptiveItemCallback(IAdaptiveItemCallback.mainHandMatches(Blocks.COARSE_DIRT));
		registrar.registerAdaptiveItemCallback(IAdaptiveItemCallback.offHandMatches(Blocks.PODZOL, Blocks.MYCELIUM));
	}
}
