package com.teamderpy.shouldersurfing.plugin;

import com.teamderpy.shouldersurfing.api.IShoulderSurfingPlugin;
import com.teamderpy.shouldersurfing.api.IShoulderSurfingRegistrar;
import com.teamderpy.shouldersurfing.api.callback.IAdaptiveItemCallback;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class AdaptiveItemCallbackPlugin implements IShoulderSurfingPlugin
{
	@Override
	public void register(IShoulderSurfingRegistrar registrar)
	{
		// Register simple ItemStack predicate
		registrar.registerAdaptiveItemCallback(itemStack -> itemStack.is(Items.APPLE));
		// Register another one
		registrar.registerAdaptiveItemCallback(itemStack -> itemStack.is(Items.GOLDEN_APPLE));
		
		// Register a full callback as inner class
		registrar.registerAdaptiveItemCallback(new IAdaptiveItemCallback()
		{
			@Override
			public boolean isHoldingAdaptiveItem(Minecraft minecraft, LivingEntity entity)
			{
				for(ItemStack stack : entity.getHandSlots())
				{
					if(stack.is(Items.ENCHANTED_GOLDEN_APPLE))
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
				if(stack.is(Items.NETHERITE_BLOCK))
				{
					return true;
				}
			}
			return false;
		});
		
		// Use static helper functions to create callback
		registrar.registerAdaptiveItemCallback(IAdaptiveItemCallback.anyHandMatches(Blocks.DIRT, Items.GRASS_BLOCK));
		registrar.registerAdaptiveItemCallback(IAdaptiveItemCallback.mainHandMatches(Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.DIRT_PATH));
		registrar.registerAdaptiveItemCallback(IAdaptiveItemCallback.offHandMatches(Blocks.PODZOL, Blocks.MYCELIUM));
	}
}
