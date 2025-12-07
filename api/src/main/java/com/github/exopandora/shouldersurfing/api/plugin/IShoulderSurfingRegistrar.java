package com.github.exopandora.shouldersurfing.api.plugin;

import com.github.exopandora.shouldersurfing.api.callback.IAdaptiveItemCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraCouplingCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraEntityTransparencyCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraRotationSetupCallback;
import com.github.exopandora.shouldersurfing.api.callback.IPlayerInputCallback;
import com.github.exopandora.shouldersurfing.api.callback.IPlayerStateCallback;
import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public interface IShoulderSurfingRegistrar
{
	IShoulderSurfingRegistrar registerAdaptiveItemCallback(IAdaptiveItemCallback adaptiveItemCallback);
	
	IShoulderSurfingRegistrar registerCameraCouplingCallback(ICameraCouplingCallback cameraCouplingCallback);
	
	default IShoulderSurfingRegistrar registerAdaptiveItemCallback(Predicate<ItemStack> predicate)
	{
		return this.registerAdaptiveItemCallback((minecraft, entity) -> StreamSupport.stream(entity.getHandSlots().spliterator(), false).anyMatch(predicate));
	}
	
	IShoulderSurfingRegistrar registerTargetCameraOffsetCallback(ITargetCameraOffsetCallback targetCameraOffsetCallback);
	
	IShoulderSurfingRegistrar registerCameraEntityTransparencyCallback(ICameraEntityTransparencyCallback cameraEntityTransparencyCallback);
	
	IShoulderSurfingRegistrar registerPlayerStateCallback(IPlayerStateCallback callback);
	
	IShoulderSurfingRegistrar registerCameraRotationSetupCallback(ICameraRotationSetupCallback callback);
	
	IShoulderSurfingRegistrar registerPlayerInputCallback(IPlayerInputCallback callback);
}
