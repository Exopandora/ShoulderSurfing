package com.teamderpy.shouldersurfing.api;

import java.util.function.Predicate;

import com.teamderpy.shouldersurfing.api.callback.IAdaptiveItemCallback;

import net.minecraft.item.ItemStack;

public interface IShoulderSurfingRegistrar
{
	IShoulderSurfingRegistrar registerAdaptiveItemCallback(IAdaptiveItemCallback adaptiveItemCallback);
	
	default IShoulderSurfingRegistrar registerAdaptiveItemCallback(Predicate<ItemStack> predicate)
	{
		return this.registerAdaptiveItemCallback((minecraft, entity) -> entity.getHeldItem() != null && predicate.test(entity.getHeldItem()));
	}
}
