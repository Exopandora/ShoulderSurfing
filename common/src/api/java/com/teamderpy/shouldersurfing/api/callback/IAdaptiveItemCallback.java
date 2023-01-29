package com.teamderpy.shouldersurfing.api.callback;

import java.util.stream.StreamSupport;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public interface IAdaptiveItemCallback
{
	boolean isHoldingAdaptiveItem(Minecraft minecraft, LivingEntity entity);
	
	public static IAdaptiveItemCallback mainHandMatches(ItemLike... items)
	{
		return (minecraft, entity) -> containsItem(entity.getMainHandItem().getItem(), items);
	}
	
	public static IAdaptiveItemCallback offHandMatches(ItemLike... items)
	{
		return (minecraft, entity) -> containsItem(entity.getOffhandItem().getItem(), items);
	}
	
	public static IAdaptiveItemCallback anyHandMatches(ItemLike... items)
	{
		return (minecraft, entity) -> StreamSupport.stream(entity.getHandSlots().spliterator(), false)
			.anyMatch(stack -> containsItem(stack.getItem(), items));
	}
	
	private static boolean containsItem(Item itemToFind, ItemLike... items)
	{
		for(ItemLike item : items)
		{
			if(itemToFind.equals(item.asItem()))
			{
				return true;
			}
		}
		
		return false;
	}
}
