package com.github.exopandora.shouldersurfing.plugin.callbacks;

import com.github.exopandora.shouldersurfing.api.callback.IAdaptiveItemCallback;
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

public class AdaptiveItemCallback implements IAdaptiveItemCallback
{
	@Override
	public boolean isHoldingAdaptiveItem(Minecraft minecraft, LivingEntity entity)
	{
		Item useItem = entity.getUseItem().getItem();
		List<? extends String> useItems = Config.CLIENT.getAdaptiveCrosshairUseItems();
		List<? extends String> useItemProperties = Config.CLIENT.getAdaptiveCrosshairUseItemProperties();
		
		if(isAdaptiveItemStack(useItem, useItems, useItemProperties))
		{
			return true;
		}
		
		List<? extends String> holdItems = Config.CLIENT.getAdaptiveCrosshairHoldItems();
		List<? extends String> holdItemProperties = Config.CLIENT.getAdaptiveCrosshairHoldItemProperties();
		ItemStack[] handItems = {entity.getMainHandItem(), entity.getOffhandItem()};
		
		for(ItemStack handStack : handItems)
		{
			if(isAdaptiveItemStack(handStack.getItem(), holdItems, holdItemProperties))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isAdaptiveItemStack(Item item, List<? extends String> expressions, List<? extends String> itemProperties)
	{
		String itemId = BuiltInRegistries.ITEM.getKey(item).toString();
		
		if(expressions.stream().map(AdaptiveItemCallback::expressionToMatchPredicate).anyMatch(pattern -> pattern.test(itemId)))
		{
			return true;
		}
		
		for(String itemProperty : itemProperties)
		{
			if(ItemProperties.getProperty(item, new ResourceLocation(itemProperty)) != null)
			{
				return true;
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
