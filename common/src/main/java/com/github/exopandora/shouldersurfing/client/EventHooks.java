package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.client.event.ComputeCameraCouplingEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeCameraEntityTransparencyEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeCameraSwayEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerAimStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerAttackStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerInteractionStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerPickStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerRideBoatStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerUseItemStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeTargetCameraOffsetEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeTemporaryFirstPersonStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ForceVanillaPlayerInputEvent;
import com.github.exopandora.shouldersurfing.api.client.event.SetupCameraRotationEvent;
import com.github.exopandora.shouldersurfing.api.client.event.TickEvent;
import com.github.exopandora.shouldersurfing.api.math.Vec2f;
import net.minecraft.client.Camera;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public class EventHooks {
	public static boolean isUsingItem(LivingEntity cameraEntity) {
		var event = new ComputePlayerUseItemStateEvent(cameraEntity);
		event.setResult(false);
		return ShoulderSurfing.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static boolean isInteracting(LivingEntity cameraEntity) {
		var event = new ComputePlayerInteractionStateEvent(cameraEntity);
		return ShoulderSurfing.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static boolean isAttacking(LivingEntity cameraEntity) {
		var event = new ComputePlayerAttackStateEvent(cameraEntity);
		return ShoulderSurfing.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static boolean isPicking(LivingEntity cameraEntity) {
		var event = new ComputePlayerPickStateEvent(cameraEntity);
		return ShoulderSurfing.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static boolean isRidingBoat(LivingEntity cameraEntity, Entity vehicle) {
		var event = new ComputePlayerRideBoatStateEvent(cameraEntity, vehicle);
		return ShoulderSurfing.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static boolean isAiming(LivingEntity entity) {
		var event = new ComputePlayerAimStateEvent(entity);
		return ShoulderSurfing.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static boolean isForcingCoupledCamera() {
		var event = new ComputeCameraCouplingEvent();
		return ShoulderSurfing.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static Vec2f setupCameraRotation(LocalPlayer player, Vec2f cameraRot, Vec2f cameraRotO, Vec2f dRot, Vec2f dRotScaled) {
		var event = new SetupCameraRotationEvent(player, cameraRot, cameraRotO, dRot, dRotScaled);
		return ShoulderSurfing.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static boolean isForcingVanillaPlayerInput(Entity cameraEntity) {
		var event = new ForceVanillaPlayerInputEvent(cameraEntity);
		return ShoulderSurfing.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static float getCameraEntityAlpha(Entity entity, float partialTick) {
		var event = new ComputeCameraEntityTransparencyEvent(entity, partialTick);
		return ShoulderSurfing.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static Vec3 getTargetOffset(Vec3 defaultOffset, Camera camera, Entity cameraEntity, BlockGetter level) {
		var event = new ComputeTargetCameraOffsetEvent(defaultOffset, camera, cameraEntity, level);
		return ShoulderSurfing.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static boolean isTemporaryFirstPerson() {
		var event = new ComputeTemporaryFirstPersonStateEvent();
		return ShoulderSurfing.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static Vec2f getCameraSway(Entity cameraEntity, float partialTick) {
		var event = new ComputeCameraSwayEvent(cameraEntity, partialTick);
		return ShoulderSurfing.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static void tick() {
		var event = new TickEvent();
		ShoulderSurfing.getInstance().getEventBus().fire(event);
	}
}
