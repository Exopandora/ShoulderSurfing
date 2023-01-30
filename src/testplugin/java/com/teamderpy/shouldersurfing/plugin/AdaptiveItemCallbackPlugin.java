package com.teamderpy.shouldersurfing.plugin;

import com.teamderpy.shouldersurfing.api.IShoulderSurfingPlugin;
import com.teamderpy.shouldersurfing.api.IShoulderSurfingRegistrar;
import com.teamderpy.shouldersurfing.api.ShoulderSurfingPlugin;
import com.teamderpy.shouldersurfing.api.callback.IAdaptiveItemCallback;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@ShoulderSurfingPlugin
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
			public boolean isHoldingAdaptiveItem(Minecraft minecraft, EntityLivingBase entity)
			{
				for(ItemStack stack : entity.getHeldEquipment())
				{
					if(stack != null && stack.getItem().equals(Items.GOLD_INGOT))
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
			for(ItemStack stack : entity.getHeldEquipment())
			{
				if(stack != null && stack.getItem().equals(Items.NETHER_STAR))
				{
					return true;
				}
			}
			
			return false;
		});
		
		// Use static helper functions to create callback
		registrar.registerAdaptiveItemCallback(IAdaptiveItemCallback.anyHandMatches(Item.getItemFromBlock(Blocks.DIRT), Item.getItemFromBlock(Blocks.GRASS)));
		registrar.registerAdaptiveItemCallback(IAdaptiveItemCallback.mainHandMatches(Item.getItemFromBlock(Blocks.PRISMARINE), Items.PRISMARINE_SHARD));
		registrar.registerAdaptiveItemCallback(IAdaptiveItemCallback.offHandMatches(Item.getItemFromBlock(Blocks.GLASS), Item.getItemFromBlock(Blocks.GLASS_PANE)));
	}
}
