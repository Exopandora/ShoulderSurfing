package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.api.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.api.IShoulderSurfingRegistrar;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class ShoulderSurfingPlugin implements IShoulderSurfingPlugin
{
	@Override
	public void register(IShoulderSurfingRegistrar registrar)
	{
		registrar.registerAdaptiveItemCallback(ShoulderSurfingPlugin::isHoldingAdaptiveItem);
	}
	
	private static boolean isHoldingAdaptiveItem(Minecraft minecraft, LivingEntity entity)
	{
		Item useItem = entity.getUseItem().getItem();
		List<? extends String> useItems = Config.CLIENT.getAdaptiveCrosshairUseItems();
		List<? extends String> useItemProperties = Config.CLIENT.getAdaptiveCrosshairUseItemProperties();
		String useItemId = Registry.ITEM.getKey(useItem).toString();
		
		if(useItems.stream().anyMatch(useItemId::matches))
		{
			return true;
		}
		
		for(String useItemProperty : useItemProperties)
		{
			if(ItemModelsProperties.getProperty(useItem, new ResourceLocation(useItemProperty)) != null)
			{
				return true;
			}
		}
		
		List<? extends String> holdItems = Config.CLIENT.getAdaptiveCrosshairHoldItems();
		List<? extends String> holdItemProperties = Config.CLIENT.getAdaptiveCrosshairHoldItemProperties();
		
		for(ItemStack handStack : entity.getHandSlots())
		{
			Item handItem = handStack.getItem();
			String handItemId = Registry.ITEM.getKey(handItem).toString();
			
			if(holdItems.stream().anyMatch(handItemId::matches))
			{
				return true;
			}
			
			for(String holdItemProperty : holdItemProperties)
			{
				if(ItemModelsProperties.getProperty(handItem, new ResourceLocation(holdItemProperty)) != null)
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
