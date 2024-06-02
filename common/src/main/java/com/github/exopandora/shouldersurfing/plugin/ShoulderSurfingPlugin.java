package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

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
		String useItemId = BuiltInRegistries.ITEM.getKey(useItem).toString();
		
		if(useItems.stream().map(ShoulderSurfingPlugin::expressionToMatchPredicate).anyMatch(pattern -> pattern.test(useItemId)))
		{
			return true;
		}
		
		for(String useItemProperty : useItemProperties)
		{
			if(ItemProperties.getProperty(useItem, new ResourceLocation(useItemProperty)) != null)
			{
				return true;
			}
		}
		
		List<? extends String> holdItems = Config.CLIENT.getAdaptiveCrosshairHoldItems();
		List<? extends String> holdItemProperties = Config.CLIENT.getAdaptiveCrosshairHoldItemProperties();
		
		for(ItemStack handStack : entity.getHandSlots())
		{
			Item handItem = handStack.getItem();
			String handItemId = BuiltInRegistries.ITEM.getKey(handItem).toString();
			
			if(holdItems.stream().map(ShoulderSurfingPlugin::expressionToMatchPredicate).anyMatch(pattern -> pattern.test(handItemId)))
			{
				return true;
			}
			
			for(String holdItemProperty : holdItemProperties)
			{
				if(ItemProperties.getProperty(handItem, new ResourceLocation(holdItemProperty)) != null)
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static Predicate<String> expressionToMatchPredicate(String expression)
	{
		try
		{
			return Pattern.compile(expression).asMatchPredicate();
		}
		catch(Exception e)
		{
			return expression::equals;
		}
	}
}
