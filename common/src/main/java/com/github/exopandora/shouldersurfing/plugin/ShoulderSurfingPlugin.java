package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;
import com.github.exopandora.shouldersurfing.compat.Mods;
import com.github.exopandora.shouldersurfing.compat.plugin.CreateModTargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
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
		
		if(Mods.CREATE.isLoaded())
		{
			try
			{
				ShoulderSurfingCommon.LOGGER.info("Registering compatibility callback for create mod");
				registrar.registerTargetCameraOffsetCallback(new CreateModTargetCameraOffsetCallback());
			}
			catch(Throwable t)
			{
				ShoulderSurfingCommon.LOGGER.error("Failed to load compatibility callback for create mod", t);
			}
		}
	}
	
	private static boolean isHoldingAdaptiveItem(Minecraft minecraft, LivingEntity entity)
	{
		ItemStack useStack = entity.getUseItem();
		List<? extends String> useItems = Config.CLIENT.getAdaptiveCrosshairUseItems();
		List<? extends String> useItemProperties = Config.CLIENT.getAdaptiveCrosshairUseItemProperties();
		String useItemId = BuiltInRegistries.ITEM.getKey(useStack.getItem()).toString();
		
		if(useItems.stream().map(ShoulderSurfingPlugin::expressionToMatchPredicate).anyMatch(pattern -> pattern.test(useItemId)))
		{
			return true;
		}
		
		for(String useItemProperty : useItemProperties)
		{
			if(ItemProperties.getProperty(useStack, ResourceLocation.parse(useItemProperty)) != null)
			{
				return true;
			}
		}
		
		List<? extends String> holdItems = Config.CLIENT.getAdaptiveCrosshairHoldItems();
		List<? extends String> holdItemProperties = Config.CLIENT.getAdaptiveCrosshairHoldItemProperties();
		
		for(ItemStack handStack : entity.getHandSlots())
		{
			String handItemId = BuiltInRegistries.ITEM.getKey(handStack.getItem()).toString();
			
			if(holdItems.stream().map(ShoulderSurfingPlugin::expressionToMatchPredicate).anyMatch(pattern -> pattern.test(handItemId)))
			{
				return true;
			}
			
			for(String holdItemProperty : holdItemProperties)
			{
				if(ItemProperties.getProperty(handStack, ResourceLocation.parse(holdItemProperty)) != null)
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
