package com.github.exopandora.shouldersurfing.api.plugin;

import com.github.exopandora.shouldersurfing.api.callback.*;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface IShoulderSurfingRegistrar {
	int DEFAULT_PRIORITY = 1000;
	
	default IShoulderSurfingRegistrar registerAdaptiveItemCallback(IAdaptiveItemCallback callback) {
		return this.registerAdaptiveItemCallback(DEFAULT_PRIORITY, callback);
	}
	
	default IShoulderSurfingRegistrar registerAdaptiveItemCallback(Predicate<ItemStack> predicate) {
		return this.registerAdaptiveItemCallback(predicate, DEFAULT_PRIORITY);
	}
	
	default IShoulderSurfingRegistrar registerAdaptiveItemCallback(Predicate<ItemStack> predicate, int priority) {
		return this.registerAdaptiveItemCallback(priority, (minecraft, entity) -> predicate.test(entity.getMainHandItem()) || predicate.test(entity.getOffhandItem()));
	}
	
	IShoulderSurfingRegistrar registerAdaptiveItemCallback(int priority, IAdaptiveItemCallback callback);
	
	default IShoulderSurfingRegistrar registerCameraCouplingCallback(ICameraCouplingCallback callback) {
		return this.registerCameraCouplingCallback(DEFAULT_PRIORITY, callback);
	}
	
	IShoulderSurfingRegistrar registerCameraCouplingCallback(int priority, ICameraCouplingCallback callback);
	
	default IShoulderSurfingRegistrar registerTargetCameraOffsetCallback(ITargetCameraOffsetCallback callback) {
		return this.registerTargetCameraOffsetCallback(DEFAULT_PRIORITY, callback);
	}
	
	IShoulderSurfingRegistrar registerTargetCameraOffsetCallback(int priority, ITargetCameraOffsetCallback callback);
	
	default IShoulderSurfingRegistrar registerCameraEntityTransparencyCallback(ICameraEntityTransparencyCallback callback) {
		return this.registerCameraEntityTransparencyCallback(DEFAULT_PRIORITY, callback);
	}
	
	IShoulderSurfingRegistrar registerCameraEntityTransparencyCallback(int priority, ICameraEntityTransparencyCallback callback);
	
	default IShoulderSurfingRegistrar registerPlayerStateCallback(IPlayerStateCallback callback) {
		return this.registerPlayerStateCallback(DEFAULT_PRIORITY, callback);
	}
	
	IShoulderSurfingRegistrar registerPlayerStateCallback(int priority, IPlayerStateCallback callback);
	
	default IShoulderSurfingRegistrar registerCameraRotationSetupCallback(ICameraRotationSetupCallback callback) {
		return this.registerCameraRotationSetupCallback(DEFAULT_PRIORITY, callback);
	}
	
	IShoulderSurfingRegistrar registerCameraRotationSetupCallback(int priority, ICameraRotationSetupCallback callback);
	
	default IShoulderSurfingRegistrar registerPlayerInputCallback(IPlayerInputCallback callback) {
		return this.registerPlayerInputCallback(DEFAULT_PRIORITY, callback);
	}
	
	IShoulderSurfingRegistrar registerPlayerInputCallback(int priority, IPlayerInputCallback callback);
}
