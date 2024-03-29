package com.teamderpy.shouldersurfing.api;

import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import com.teamderpy.shouldersurfing.api.callback.IAdaptiveItemCallback;

import net.minecraft.world.item.ItemStack;

public interface IShoulderSurfingRegistrar
{
	IShoulderSurfingRegistrar registerAdaptiveItemCallback(IAdaptiveItemCallback adaptiveItemCallback);
	
	default IShoulderSurfingRegistrar registerAdaptiveItemCallback(Predicate<ItemStack> predicate)
	{
		return this.registerAdaptiveItemCallback((minecraft, entity) ->
		{
			return StreamSupport.stream(entity.getHandSlots().spliterator(), false).anyMatch(predicate::test);
		});
	}
}
