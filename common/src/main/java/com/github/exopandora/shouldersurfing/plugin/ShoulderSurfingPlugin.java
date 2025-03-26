package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;
import com.github.exopandora.shouldersurfing.compat.Mods;
import com.github.exopandora.shouldersurfing.compat.plugin.CreateModTargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
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
		String useItemId = BuiltInRegistries.ITEM.getKey(useStack.getItem()).toString();
		
		if(useItems.stream().map(ShoulderSurfingPlugin::expressionToMatchPredicate).anyMatch(pattern -> pattern.test(useItemId)))
		{
			return true;
		}
		
		if(!useStack.getComponentsPatch().isEmpty())
		{
			DataComponentPatch patch = useStack.getComponentsPatch();
			
			for(String componentId : Config.CLIENT.getAdaptiveCrosshairUseItemComponents())
			{
				Optional<DataComponentType<?>> type = BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(ResourceLocation.tryParse(componentId));
				
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
		
		String useAnimation = useStack.getUseAnimation().getSerializedName();
		
		for(String useItemAnimation : Config.CLIENT.getAdaptiveCrosshairUseItemAnimations())
		{
			if(useItemAnimation.equals(useAnimation))
			{
				return true;
			}
		}
		
		List<? extends String> holdItems = Config.CLIENT.getAdaptiveCrosshairHoldItems();
		List<? extends String> holdItemAnimations = Config.CLIENT.getAdaptiveCrosshairHoldItemAnimations();
		ItemStack[] handItems = {entity.getMainHandItem(), entity.getOffhandItem()};
		
		for(ItemStack handStack : handItems)
		{
			String handItemId = BuiltInRegistries.ITEM.getKey(handStack.getItem()).toString();
			
			if(holdItems.stream().map(ShoulderSurfingPlugin::expressionToMatchPredicate).anyMatch(pattern -> pattern.test(handItemId)))
			{
				return true;
			}
			
			if(!handStack.getComponentsPatch().isEmpty())
			{
				DataComponentPatch patch = handStack.getComponentsPatch();
				
				for(String componentId : Config.CLIENT.getAdaptiveCrosshairHoldItemComponents())
				{
					Optional<DataComponentType<?>> type = BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(ResourceLocation.tryParse(componentId));
					
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
			
			String handItemUseAnimation = handStack.getUseAnimation().getSerializedName();
			
			for(String holdItemAnimation : holdItemAnimations)
			{
				if(handItemUseAnimation.equals(holdItemAnimation))
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
