package com.github.exopandora.shouldersurfing.api.config;

import com.github.exopandora.shouldersurfing.api.client.CameraDistanceAttributeMode;
import com.github.exopandora.shouldersurfing.api.client.ViewBobbingMode;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ICameraConfig {
	double getOffsetX();
	
	double getOffsetY();
	
	double getOffsetZ();
	
	default @NotNull Vec3 getOffset() {
		return new Vec3(this.getOffsetX(), this.getOffsetY(), this.getOffsetZ());
	}
	
	List<Double> getOffsetXPresets();
	
	List<Double> getOffsetYPresets();
	
	List<Double> getOffsetZPresets();
	
	double getMinOffsetX();
	
	double getMinOffsetY();
	
	double getMinOffsetZ();
	
	default @NotNull Vec3 getMinOffset() {
		return new Vec3(this.getMinOffsetX(), this.getMinOffsetY(), this.getMinOffsetZ());
	}
	
	double getMaxOffsetX();
	
	double getMaxOffsetY();
	
	double getMaxOffsetZ();
	
	default @NotNull Vec3 getMaxOffset() {
		return new Vec3(this.getMaxOffsetX(), this.getMaxOffsetY(), this.getMaxOffsetZ());
	}
	
	boolean isOffsetXUnlimited();
	
	boolean isOffsetYUnlimited();
	
	boolean isOffsetZUnlimited();
	
	double getPassengerOffsetXMultiplier();
	
	double getPassengerOffsetYMultiplier();
	
	double getPassengerOffsetZMultiplier();
	
	default @NotNull Vec3 getPassengerOffsetMultipliers() {
		return new Vec3(
			this.getPassengerOffsetXMultiplier(),
			this.getPassengerOffsetYMultiplier(),
			this.getPassengerOffsetZMultiplier()
		);
	}
	
	double getSprintOffsetXMultiplier();
	
	double getSprintOffsetYMultiplier();
	
	double getSprintOffsetZMultiplier();
	
	default @NotNull Vec3 getSprintOffsetMultipliers() {
		return new Vec3(
			this.getSprintOffsetXMultiplier(),
			this.getSprintOffsetYMultiplier(),
			this.getSprintOffsetZMultiplier()
		);
	}
	
	double getAimingOffsetXMultiplier();
	
	double getAimingOffsetYMultiplier();
	
	double getAimingOffsetZMultiplier();
	
	default @NotNull Vec3 getAimingOffsetMultipliers() {
		return new Vec3(
			this.getAimingOffsetXMultiplier(),
			this.getAimingOffsetYMultiplier(),
			this.getAimingOffsetZMultiplier()
		);
	}
	
	double getFallFlyingOffsetXMultiplier();
	
	double getFallFlyingOffsetYMultiplier();
	
	double getFallFlyingOffsetZMultiplier();
	
	default @NotNull Vec3 getFallFlyingMultipliers() {
		return new Vec3(
			this.getFallFlyingOffsetXMultiplier(),
			this.getFallFlyingOffsetYMultiplier(),
			this.getFallFlyingOffsetZMultiplier()
		);
	}
	
	double getClimbingOffsetXMultiplier();
	
	double getClimbingOffsetYMultiplier();
	
	double getClimbingOffsetZMultiplier();
	
	default @NotNull Vec3 getClimbingMultipliers() {
		return new Vec3(
			this.getClimbingOffsetXMultiplier(),
			this.getClimbingOffsetYMultiplier(),
			this.getClimbingOffsetZMultiplier()
		);
	}
	
	double getPassengerOffsetXModifier();
	
	double getPassengerOffsetYModifier();
	
	double getPassengerOffsetZModifier();
	
	default @NotNull Vec3 getPassengerOffsetModifiers() {
		return new Vec3(
			this.getPassengerOffsetXModifier(),
			this.getPassengerOffsetYModifier(),
			this.getPassengerOffsetZModifier()
		);
	}
	
	double getSprintOffsetXModifier();
	
	double getSprintOffsetYModifier();
	
	double getSprintOffsetZModifier();
	
	default @NotNull Vec3 getSprintOffsetModifiers() {
		return new Vec3(
			this.getSprintOffsetXModifier(),
			this.getSprintOffsetYModifier(),
			this.getSprintOffsetZModifier()
		);
	}
	
	double getAimingOffsetXModifier();
	
	double getAimingOffsetYModifier();
	
	double getAimingOffsetZModifier();
	
	default @NotNull Vec3 getAimingOffsetModifiers() {
		return new Vec3(
			this.getAimingOffsetXModifier(),
			this.getAimingOffsetYModifier(),
			this.getAimingOffsetZModifier()
		);
	}
	
	double getFallFlyingOffsetXModifier();
	
	double getFallFlyingOffsetYModifier();
	
	double getFallFlyingOffsetZModifier();
	
	default @NotNull Vec3 getFallFlyingOffsetModifiers() {
		return new Vec3(
			this.getFallFlyingOffsetXModifier(),
			this.getFallFlyingOffsetYModifier(),
			this.getFallFlyingOffsetZModifier()
		);
	}
	
	double getClimbingOffsetXModifier();
	
	double getClimbingOffsetYModifier();
	
	double getClimbingOffsetZModifier();
	
	default @NotNull Vec3 getClimbingOffsetModifiers() {
		return new Vec3(
			this.getClimbingOffsetXModifier(),
			this.getClimbingOffsetYModifier(),
			this.getClimbingOffsetZModifier()
		);
	}
	
	CameraDistanceAttributeMode getCameraDistanceAttributeMode();
	
	double keepCameraOutOfHeadMultiplier();
	
	double getCameraStepSize();
	
	double getCameraTransitionSpeedMultiplier();
	
	double getCenterCameraWhenLookingDownAngle();
	
	boolean isOffsetDynamic();
	
	boolean isCameraDecoupled();
	
	boolean isCameraOrientedOnTeleport();
	
	boolean isFovOverrideEnabled();
	
	float getFovOverride();
	
	ViewBobbingMode getViewBobbingMode();
	
	boolean isCameraTurningWithPlayer();
	
	int getCameraTurningWithPlayerDelay();
	
	double getCameraDragXMultiplier();
	
	double getCameraDragYMultiplier();
	
	double getCameraDragZMultiplier();
	
	default @NotNull Vec3 getCameraDragMultipliers() {
		return new Vec3(
			this.getCameraDragXMultiplier(),
			this.getCameraDragYMultiplier(),
			this.getCameraDragZMultiplier()
		);
	}
	
	double getCameraSwayXMaxAngle();
	
	double getCameraSwayZMaxAngle();
	
	double getCameraSwayXMaxVelocity();
	
	double getCameraSwayZMaxVelocity();
}
