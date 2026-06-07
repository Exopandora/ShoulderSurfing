package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.callback.*;
import com.github.exopandora.shouldersurfing.api.callback.ICameraRotationSetupCallback.CameraRotationSetupContext;
import com.github.exopandora.shouldersurfing.api.callback.ICameraRotationSetupCallback.CameraRotationSetupResult;
import com.github.exopandora.shouldersurfing.api.callback.IPlayerInputCallback.IsForcingVanillaMovementInputContext;
import com.github.exopandora.shouldersurfing.api.callback.IPlayerStateCallback.*;
import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback.GetTagetCameraOffsetContext;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.plugin.ShoulderSurfingRegistrar;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class CallbackHelper {
	protected static boolean isUsingItem(LivingEntity cameraEntity, Minecraft minecraft) {
		final IsUsingContext context = new IsUsingContext(minecraft, cameraEntity);
		for (final IPlayerStateCallback callback : getPlayerStateCallbacks()) {
			final IPlayerStateCallback.Result result = callback.isUsingItem(context);
			if (result == IPlayerStateCallback.Result.TRUE) {
				return true;
			} else if (result == IPlayerStateCallback.Result.FALSE) {
				return false;
			}
		}
		return cameraEntity.isUsingItem() && !cameraEntity.getUseItem().has(DataComponents.FOOD) || cameraEntity instanceof Player player && player.isScoping();
	}
	
	protected static boolean isInteracting(LivingEntity cameraEntity, Minecraft minecraft) {
		final IsInteractingContext context = new IsInteractingContext(minecraft, cameraEntity);
		for (final IPlayerStateCallback callback : getPlayerStateCallbacks()) {
			final IPlayerStateCallback.Result result = callback.isInteracting(context);
			if (result == IPlayerStateCallback.Result.TRUE) {
				return true;
			} else if (result == IPlayerStateCallback.Result.FALSE) {
				return false;
			}
		}
		return minecraft.options.keyUse.isDown() && !cameraEntity.isUsingItem();
	}
	
	protected static boolean isAttacking(LivingEntity cameraEntity, Minecraft minecraft) {
		final IsAttackingContext context = new IsAttackingContext(minecraft, cameraEntity);
		for (final IPlayerStateCallback callback : getPlayerStateCallbacks()) {
			final IPlayerStateCallback.Result result = callback.isAttacking(context);
			if (result == IPlayerStateCallback.Result.TRUE) {
				return true;
			} else if (result == IPlayerStateCallback.Result.FALSE) {
				return false;
			}
		}
		return minecraft.options.keyAttack.isDown();
	}
	
	protected static boolean isPicking(LivingEntity cameraEntity, Minecraft minecraft) {
		final IsPickingContext context = new IsPickingContext(minecraft, cameraEntity);
		for (final IPlayerStateCallback callback : getPlayerStateCallbacks()) {
			final IPlayerStateCallback.Result result = callback.isPicking(context);
			if (result == IPlayerStateCallback.Result.TRUE) {
				return true;
			} else if (result == IPlayerStateCallback.Result.FALSE) {
				return false;
			}
		}
		return minecraft.options.keyPickItem.isDown();
	}
	
	protected static boolean isRidingBoat(Minecraft minecraft, @Nullable Entity entity) {
		if (!(entity instanceof LivingEntity)) {
			return false;
		}
		Entity vehicle = entity.getVehicle();
		if (vehicle == null) {
			return false;
		}
		final IsRidingBoatContext context = new IsRidingBoatContext(minecraft, entity, vehicle);
		for (final IPlayerStateCallback callback : ShoulderSurfingRegistrar.getInstance().getPlayerStateCallbacks()) {
			final IPlayerStateCallback.Result result = callback.isRidingBoat(context);
			if (result == IPlayerStateCallback.Result.TRUE) {
				return true;
			} else if (result == IPlayerStateCallback.Result.FALSE) {
				return false;
			}
		}
		return vehicle instanceof AbstractBoat;
	}
	
	protected static boolean isHoldingAdaptiveItem(Minecraft minecraft, Entity entity) {
		if (entity instanceof LivingEntity living) {
			for (IAdaptiveItemCallback callback : ShoulderSurfingRegistrar.getInstance().getAdaptiveItemCallbacks()) {
				if (callback.isHoldingAdaptiveItem(minecraft, living)) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected static boolean isForcingCoupledCamera(Minecraft minecraft) {
		for (ICameraCouplingCallback callback : ShoulderSurfingRegistrar.getInstance().getCameraCouplingCallbacks()) {
			if (callback.isForcingCameraCoupling(minecraft)) {
				return true;
			}
		}
		return false;
	}
	
	protected static CameraRotationSetupResult fireCameraRotationSetupCallbackPre(
		LocalPlayer player, double yRotDelta, double xRotDelta, float yRot, float xRot
	) {
		final CameraRotationSetupContext context = new CameraRotationSetupContext(player, xRotDelta, yRotDelta);
		final CameraRotationSetupResult result = new CameraRotationSetupResult(xRot, yRot);
		for (ICameraRotationSetupCallback callback : ShoulderSurfingRegistrar.getInstance().getSetupCameraRotationCallbacks()) {
			callback.pre(context, result);
		}
		return result;
	}
	
	protected static CameraRotationSetupResult fireCameraRotationSetupCallbackPost(
		LocalPlayer player, double yRotDelta, double xRotDelta, float yRot, float xRot
	) {
		final CameraRotationSetupContext context = new CameraRotationSetupContext(player, xRotDelta, yRotDelta);
		final CameraRotationSetupResult result = new CameraRotationSetupResult(xRot, yRot);
		for (ICameraRotationSetupCallback callback : ShoulderSurfingRegistrar.getInstance().getSetupCameraRotationCallbacks()) {
			callback.post(context, result);
		}
		return result;
	}
	
	protected static boolean isForcingVanillaMovementInput(Minecraft minecraft, Entity cameraEntity) {
		final IsForcingVanillaMovementInputContext context = new IsForcingVanillaMovementInputContext(minecraft, cameraEntity);
		for (IPlayerInputCallback callback : ShoulderSurfingRegistrar.getInstance().getPlayerInputCallbacks()) {
			if (callback.isForcingVanillaMovementInput(context)) {
				return true;
			}
		}
		return false;
	}
	
	protected static float getCameraEntityAlpha(IShoulderSurfing instance, Entity entity, float alpha, float partialTick) {
		float result = alpha;
		for (ICameraEntityTransparencyCallback callback : ShoulderSurfingRegistrar.getInstance().getCameraEntityTransparencyCallbacks()) {
			result = Math.min(Mth.clamp(callback.getCameraEntityAlpha(instance, entity, partialTick), 0.0F, 1.0F), result);
		}
		return result;
	}
	
	protected static Vec3 getTargetOffset(
		IShoulderSurfing instance, Vec3 defaultOffset, Camera camera, Entity cameraEntity, BlockGetter level
	) {
		Vec3 targetOffset = defaultOffset;
		for (ITargetCameraOffsetCallback callback : ShoulderSurfingRegistrar.getInstance().getTargetCameraOffsetCallbacks()) {
			GetTagetCameraOffsetContext context = new GetTagetCameraOffsetContext(
				instance, targetOffset, defaultOffset, camera, cameraEntity, level
			);
			targetOffset = callback.getTargetOffset(context);
		}
		return targetOffset;
	}
	
	private static List<IPlayerStateCallback> getPlayerStateCallbacks() {
		return ShoulderSurfingRegistrar.getInstance().getPlayerStateCallbacks();
	}
}
