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

@ShoulderSurfingPlugin
public class AdaptiveItemCallbackPlugin implements IShoulderSurfingPlugin
{
	@Override
	public void register(IShoulderSurfingRegistrar registrar)
	{
		// Register simple ItemStack predicate
		registrar.registerAdaptiveItemCallback(itemStack -> itemStack.getItem().equals(Items.apple));
		// Register another one
		registrar.registerAdaptiveItemCallback(itemStack -> itemStack.getItem().equals(Items.golden_apple));
		
		// Register a full callback as inner class
		registrar.registerAdaptiveItemCallback(new IAdaptiveItemCallback()
		{
			@Override
			public boolean isHoldingAdaptiveItem(Minecraft minecraft, EntityLivingBase entity)
			{
				return entity.getHeldItem() != null && entity.getHeldItem().getItem().equals(Items.gold_ingot);
			}
		});
		
		// Register a full callback as lambda
		registrar.registerAdaptiveItemCallback((minecraft, entity) ->
		{
			return entity.getHeldItem() != null && entity.getHeldItem().getItem().equals(Items.nether_star);
		});
		
		// Use static helper function to create callback
		registrar.registerAdaptiveItemCallback(IAdaptiveItemCallback.handMatches(Item.getItemFromBlock(Blocks.dirt), Item.getItemFromBlock(Blocks.grass)));
	}
}
