package com.github.exopandora.shouldersurfing.plugin.callbacks;

import com.github.exopandora.shouldersurfing.api.callback.IAdaptiveItemCallback;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class AdaptiveItemCallback implements IAdaptiveItemCallback
{
	@Override
	public boolean isHoldingAdaptiveItem(Minecraft minecraft, LivingEntity entity)
	{
		ItemStack useStack = entity.getUseItem();
		List<? extends String> useItems = Config.CLIENT.getAdaptiveCrosshairUseItems();
		List<? extends String> useItemComponents = Config.CLIENT.getAdaptiveCrosshairUseItemComponents();
		List<? extends String> useItemDefaultComponents = Config.CLIENT.getAdaptiveCrosshairUseItemDefaultComponents();
		List<? extends String> useItemAnimations = Config.CLIENT.getAdaptiveCrosshairUseItemAnimations();
		
		if(isAdaptiveItemStack(useStack, useItems, useItemComponents, useItemDefaultComponents, useItemAnimations))
		{
			return true;
		}
		
		List<? extends String> holdItems = Config.CLIENT.getAdaptiveCrosshairHoldItems();
		List<? extends String> holdItemComponents = Config.CLIENT.getAdaptiveCrosshairHoldItemComponents();
		List<? extends String> holdDefaultComponents = Config.CLIENT.getAdaptiveCrosshairHoldItemDefaultComponents();
		List<? extends String> holdItemAnimations = Config.CLIENT.getAdaptiveCrosshairHoldItemAnimations();
		ItemStack[] handItems = {entity.getMainHandItem(), entity.getOffhandItem()};
		
		for(ItemStack handStack : handItems)
		{
			if(isAdaptiveItemStack(handStack, holdItems, holdItemComponents, holdDefaultComponents, holdItemAnimations))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isAdaptiveItemStack(ItemStack stack, List<? extends String> expressions, List<? extends String> componentIds, List<? extends String> defaultComponentIds, List<? extends String> itemAnimations)
	{
		String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
		
		if(expressions.stream().map(AdaptiveItemCallback::expressionToMatchPredicate).anyMatch(pattern -> pattern.test(itemId)))
		{
			return true;
		}
		
		if(!stack.getComponentsPatch().isEmpty())
		{
			DataComponentPatch patch = stack.getComponentsPatch();
			
			for(String componentId : componentIds)
			{
				Optional<DataComponentType<?>> type = BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(Identifier.tryParse(componentId));
				
				if(type.isEmpty())
				{
					continue;
				}
				
				Optional<?> component = patch.get(type.get());
				
				if(component != null && component.isPresent())
				{
					return true;
				}
			}
		}
		
		if(!stack.getComponents().isEmpty())
		{
			DataComponentMap components = stack.getComponents();
			
			for(String defaultComponentId : defaultComponentIds)
			{
				Optional<DataComponentType<?>> type = BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(Identifier.tryParse(defaultComponentId));
				
				if(type.isEmpty())
				{
					continue;
				}
				
				if(components.get(type.get()) != null)
				{
					return true;
				}
			}
		}
		
		String useAnimation = stack.getUseAnimation().getSerializedName();
		
		for(String itemAnimation : itemAnimations)
		{
			if(itemAnimation.equals(useAnimation))
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
