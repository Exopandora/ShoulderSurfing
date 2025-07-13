package com.github.exopandora.shouldersurfing.api.callback;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

/**
 * This callback can be used to implement custom switching behaviour for the adaptive crosshair for any item held.
 * The final result is calculated from all partial results using a logical OR.
 */
public interface IAdaptiveItemCallback
{
	/**
	 * @param minecraft The Minecraft instance
	 * @param entity The current camera entity
	 * @return <code>true</code> if the dynamic crosshair should be displayed, <code>false</code> if the static crosshair should be displayed
	 */
	boolean isHoldingAdaptiveItem(Minecraft minecraft, LivingEntity entity);
	
	/**
	 * @param items Items to match
	 * @return An IAdaptiveItemCallback instance that returns <code>true</code> whenever the main-hand matches any of the listed items
	 */
	static IAdaptiveItemCallback mainHandMatches(ItemLike... items)
	{
		return (minecraft, entity) -> containsItem(entity.getMainHandItem().getItem(), items);
	}
	
	/**
	 * @param items Items to match
	 * @return An IAdaptiveItemCallback instance that returns <code>true</code> whenever the off-hand matches any of the listed items
	 */
	static IAdaptiveItemCallback offHandMatches(ItemLike... items)
	{
		return (minecraft, entity) -> containsItem(entity.getOffhandItem().getItem(), items);
	}
	
	/**
	 * @param items Items to match
	 * @return An IAdaptiveItemCallback instance that returns <code>true</code> whenever any hand matches any of the listed items
	 */
	static IAdaptiveItemCallback anyHandMatches(ItemLike... items)
	{
		return (minecraft, entity) -> containsItem(entity.getMainHandItem().getItem(), items) || containsItem(entity.getOffhandItem().getItem(), items);
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
