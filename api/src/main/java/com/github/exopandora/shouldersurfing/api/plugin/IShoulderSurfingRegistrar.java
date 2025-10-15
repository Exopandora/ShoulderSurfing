package com.github.exopandora.shouldersurfing.api.plugin;

import com.github.exopandora.shouldersurfing.api.callback.IAdaptiveItemCallback;
import com.github.exopandora.shouldersurfing.api.callback.IAttackStateCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraCouplingCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraEntityTransparencyCallback;
import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface IShoulderSurfingRegistrar
{
	IShoulderSurfingRegistrar registerAdaptiveItemCallback(IAdaptiveItemCallback adaptiveItemCallback);
	
	IShoulderSurfingRegistrar registerCameraCouplingCallback(ICameraCouplingCallback cameraCouplingCallback);
	
	default IShoulderSurfingRegistrar registerAdaptiveItemCallback(Predicate<ItemStack> predicate)
	{
		return this.registerAdaptiveItemCallback((minecraft, entity) -> predicate.test(entity.getMainHandItem()) || predicate.test(entity.getOffhandItem()));
	}
	
	IShoulderSurfingRegistrar registerTargetCameraOffsetCallback(ITargetCameraOffsetCallback targetCameraOffsetCallback);
	
	IShoulderSurfingRegistrar registerCameraEntityTransparencyCallback(ICameraEntityTransparencyCallback cameraEntityTransparencyCallback);
	
	IShoulderSurfingRegistrar registerAttackStateCallback(IAttackStateCallback callback);
}
