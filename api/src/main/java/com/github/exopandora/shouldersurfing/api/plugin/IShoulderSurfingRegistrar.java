package com.github.exopandora.shouldersurfing.api.plugin;

import com.github.exopandora.shouldersurfing.api.callback.IAdaptiveItemCallback;
import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface IShoulderSurfingRegistrar
{
	IShoulderSurfingRegistrar registerAdaptiveItemCallback(IAdaptiveItemCallback adaptiveItemCallback);
	
	default IShoulderSurfingRegistrar registerAdaptiveItemCallback(Predicate<ItemStack> predicate)
	{
		return this.registerAdaptiveItemCallback((minecraft, entity) -> predicate.test(entity.getMainHandItem()) || predicate.test(entity.getOffhandItem()));
	}
	
	IShoulderSurfingRegistrar registerTargetCameraOffsetCallback(ITargetCameraOffsetCallback targetCameraOffsetCallback);
}
