package com.teamderpy.shouldersurfing.api.callback;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;

public interface IAdaptiveItemCallback
{
	boolean isHoldingAdaptiveItem(Minecraft minecraft, EntityLivingBase entity);
	
	public static IAdaptiveItemCallback handMatches(Item... items)
	{
		return (minecraft, entity) -> entity.getHeldItem() != null && containsItem(entity.getHeldItem().getItem(), items);
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
