package com.teamderpy.shouldersurfing.api.callback;

import java.util.stream.StreamSupport;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;

public interface IAdaptiveItemCallback
{
	boolean isHoldingAdaptiveItem(Minecraft minecraft, EntityLivingBase entity);
	
	public static IAdaptiveItemCallback mainHandMatches(Item... items)
	{
		return (minecraft, entity) -> entity.getHeldItemMainhand() != null && containsItem(entity.getHeldItemMainhand().getItem(), items);
	}
	
	public static IAdaptiveItemCallback offHandMatches(Item... items)
	{
		return (minecraft, entity) -> entity.getHeldItemOffhand() != null && containsItem(entity.getHeldItemOffhand().getItem(), items);
	}
	
	public static IAdaptiveItemCallback anyHandMatches(Item... items)
	{
		return (minecraft, entity) -> StreamSupport.stream(entity.getHeldEquipment().spliterator(), false)
			.anyMatch(stack -> stack != null && containsItem(stack.getItem(), items));
	}
	
	static boolean containsItem(Item itemToFind, Item... items)
	{
		for(Item item : items)
		{
			if(itemToFind.equals(item))
			{
				return true;
			}
		}
		
		return false;
	}
}
