package com.github.exopandora.shouldersurfing.legacy.adapter;

import com.github.exopandora.shouldersurfing.api.callback.IAdaptiveItemCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraCouplingCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraEntityTransparencyCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraRotationSetupCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraRotationSetupCallback.CameraRotationSetupContext;
import com.github.exopandora.shouldersurfing.api.callback.ICameraRotationSetupCallback.CameraRotationSetupResult;
import com.github.exopandora.shouldersurfing.api.callback.IPlayerInputCallback;
import com.github.exopandora.shouldersurfing.api.callback.IPlayerInputCallback.IsForcingVanillaMovementInputContext;
import com.github.exopandora.shouldersurfing.api.callback.IPlayerStateCallback;
import com.github.exopandora.shouldersurfing.api.callback.IPlayerStateCallback.IsAttackingContext;
import com.github.exopandora.shouldersurfing.api.callback.IPlayerStateCallback.IsInteractingContext;
import com.github.exopandora.shouldersurfing.api.callback.IPlayerStateCallback.IsPickingContext;
import com.github.exopandora.shouldersurfing.api.callback.IPlayerStateCallback.IsRidingBoatContext;
import com.github.exopandora.shouldersurfing.api.callback.IPlayerStateCallback.IsUsingContext;
import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraCouplingEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraEntityTransparencyEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerAimStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerAttackStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerInteractionStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerRideBoatStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeTargetCameraOffsetEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ForceVanillaPlayerInputEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.SetupCameraRotationEventHandler;
import com.github.exopandora.shouldersurfing.api.event.IEventBus;
import com.github.exopandora.shouldersurfing.api.math.Vec2f;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;
import net.minecraft.client.Minecraft;

public class ShoulderSurfingRegistrarAdapter implements IShoulderSurfingRegistrar {
	private final IEventBus eventBus;
	
	public ShoulderSurfingRegistrarAdapter(IEventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	@Override
	public IShoulderSurfingRegistrar registerAdaptiveItemCallback(IAdaptiveItemCallback adaptiveItemCallback) {
		this.eventBus.register((ComputePlayerAimStateEventHandler) event -> {
			if (!event.getResult()) {
				event.setResult(adaptiveItemCallback.isHoldingAdaptiveItem(Minecraft.getInstance(), event.getEntity()));
			}
		});
		return this;
	}
	
	@Override
	public IShoulderSurfingRegistrar registerCameraCouplingCallback(ICameraCouplingCallback cameraCouplingCallback) {
		this.eventBus.register((ComputeCameraCouplingEventHandler) event -> {
			if (!event.getResult()) {
				event.setResult(cameraCouplingCallback.isForcingCameraCoupling(Minecraft.getInstance()));
			}
		});
		return this;
	}
	
	@Override
	public IShoulderSurfingRegistrar registerTargetCameraOffsetCallback(ITargetCameraOffsetCallback targetCameraOffsetCallback) {
		this.eventBus.register(500, (ComputeTargetCameraOffsetEventHandler) event ->
			event.setResult(targetCameraOffsetCallback.pre(ShoulderSurfing.getInstance(), event.getResult(), event.getDefaultOffset()))
		);
		this.eventBus.register(1500, (ComputeTargetCameraOffsetEventHandler) event ->
			event.setResult(targetCameraOffsetCallback.post(ShoulderSurfing.getInstance(), event.getResult(), event.getDefaultOffset()))
		);
		return this;
	}
	
	@Override
	public IShoulderSurfingRegistrar registerCameraEntityTransparencyCallback(ICameraEntityTransparencyCallback cameraEntityTransparencyCallback) {
		this.eventBus.register((ComputeCameraEntityTransparencyEventHandler) event ->
			event.setResult(cameraEntityTransparencyCallback.getCameraEntityAlpha(
				ShoulderSurfing.getInstance(), event.getCameraEntity(), event.getPartialTick()
			))
		);
		return this;
	}
	
	@Override
	public IShoulderSurfingRegistrar registerPlayerStateCallback(IPlayerStateCallback callback) {
		this.eventBus.register((ComputePlayerAttackStateEventHandler) event -> {
			IsAttackingContext context = new IsAttackingContext(Minecraft.getInstance());
			switch (callback.isAttacking(context)) {
				case TRUE -> event.setResult(true);
				case FALSE -> {
					event.setResult(false);
					event.cancel();
				}
			}
		});
		this.eventBus.register((ComputePlayerInteractionStateEventHandler) event -> {
			IsInteractingContext context = new IsInteractingContext(Minecraft.getInstance(), event.getCameraEntity());
			switch (callback.isInteracting(context)) {
				case TRUE -> event.setResult(true);
				case FALSE -> {
					event.setResult(false);
					event.cancel();
				}
			}
		});
		this.eventBus.register((ComputePlayerInteractionStateEventHandler) event -> {
			IsPickingContext context = new IsPickingContext(Minecraft.getInstance());
			switch (callback.isPicking(context)) {
				case TRUE -> event.setResult(true);
				case FALSE -> {
					event.setResult(false);
					event.cancel();
				}
			}
		});
		this.eventBus.register((ComputePlayerInteractionStateEventHandler) event -> {
			IsUsingContext context = new IsUsingContext(Minecraft.getInstance(), event.getCameraEntity());
			switch (callback.isUsingItem(context)) {
				case TRUE -> event.setResult(true);
				case FALSE -> {
					event.setResult(false);
					event.cancel();
				}
			}
		});
		this.eventBus.register((ComputePlayerRideBoatStateEventHandler) event -> {
			IsRidingBoatContext context = new IsRidingBoatContext(Minecraft.getInstance(), event.getCameraEntity(), event.getVehicle());
			switch (callback.isRidingBoat(context)) {
				case TRUE -> event.setResult(true);
				case FALSE -> {
					event.setResult(false);
					event.cancel();
				}
			}
		});
		return this;
	}
	
	@Override
	public IShoulderSurfingRegistrar registerCameraRotationSetupCallback(ICameraRotationSetupCallback callback) {
		this.eventBus.register(500, (SetupCameraRotationEventHandler) event -> {
			CameraRotationSetupResult result = new CameraRotationSetupResult(event.getResult().x(), event.getResult().x());
			CameraRotationSetupContext context = new CameraRotationSetupContext(
				event.getPlayer(), event.getDeltaRot().x(), event.getDeltaRot().y()
			);
			callback.pre(context, result);
			event.setResult(new Vec2f(result.getXRot(), result.getYRot()));
		});
		this.eventBus.register(1500, (SetupCameraRotationEventHandler) event -> {
			CameraRotationSetupResult result = new CameraRotationSetupResult(event.getResult().x(), event.getResult().x());
			CameraRotationSetupContext context = new CameraRotationSetupContext(
				event.getPlayer(), event.getDeltaRot().x(), event.getDeltaRot().y()
			);
			callback.post(context, result);
			event.setResult(new Vec2f(result.getXRot(), result.getYRot()));
		});
		return this;
	}
	
	@Override
	public IShoulderSurfingRegistrar registerPlayerInputCallback(IPlayerInputCallback callback) {
		this.eventBus.register((ForceVanillaPlayerInputEventHandler) event -> {
			if (!event.getResult()) {
				IsForcingVanillaMovementInputContext context = new IsForcingVanillaMovementInputContext(
					Minecraft.getInstance(), event.getCameraEntity()
				);
				event.setResult(callback.isForcingVanillaMovementInput(context));
			}
		});
		return this;
	}
}
