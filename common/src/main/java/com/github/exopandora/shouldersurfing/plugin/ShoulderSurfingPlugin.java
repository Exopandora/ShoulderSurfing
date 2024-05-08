package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.api.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.api.IShoulderSurfingRegistrar;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

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
		ItemStack useStack = entity.getUseItem();
		List<? extends String> useItems = Config.CLIENT.getAdaptiveCrosshairUseItems();
		List<? extends String> useItemProperties = Config.CLIENT.getAdaptiveCrosshairUseItemProperties();
		String useItemId = BuiltInRegistries.ITEM.getKey(useStack.getItem()).toString();
		
		if(useItems.stream().anyMatch(useItemId::matches))
		{
			return true;
		}
		
		for(String useItemProperty : useItemProperties)
		{
			if(ItemProperties.getProperty(useStack, new ResourceLocation(useItemProperty)) != null)
			{
				return true;
			}
		}
		
		List<? extends String> holdItems = Config.CLIENT.getAdaptiveCrosshairHoldItems();
		List<? extends String> holdItemProperties = Config.CLIENT.getAdaptiveCrosshairHoldItemProperties();
		
		for(ItemStack handStack : entity.getHandSlots())
		{
			String handItemId = BuiltInRegistries.ITEM.getKey(handStack.getItem()).toString();
			
			if(holdItems.stream().anyMatch(handItemId::matches))
			{
				return true;
			}
			
			for(String holdItemProperty : holdItemProperties)
			{
				if(ItemProperties.getProperty(handStack, new ResourceLocation(holdItemProperty)) != null)
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
