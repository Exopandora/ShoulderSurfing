package com.teamderpy.shouldersurfing.api.callback;

import java.util.stream.StreamSupport;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;

public interface IAdaptiveItemCallback
{
	boolean isHoldingAdaptiveItem(Minecraft minecraft, LivingEntity entity);
	
	static IAdaptiveItemCallback mainHandMatches(IItemProvider... items)
	{
		return (minecraft, entity) -> containsItem(entity.getMainHandItem().getItem(), items);
	}
	
	static IAdaptiveItemCallback offHandMatches(IItemProvider... items)
	{
		return (minecraft, entity) -> containsItem(entity.getOffhandItem().getItem(), items);
	}
	
	static IAdaptiveItemCallback anyHandMatches(IItemProvider... items)
	{
		return (minecraft, entity) -> StreamSupport.stream(entity.getHandSlots().spliterator(), false)
			.anyMatch(stack -> containsItem(stack.getItem(), items));
	}
	
	static boolean containsItem(Item itemToFind, IItemProvider... items)
	{
		for(IItemProvider item : items)
		{
			if(itemToFind.equals(item.asItem()))
			{
				return true;
			}
		}
		
		return false;
	}
}
