package com.github.exopandora.shouldersurfing.api.client;

import com.github.exopandora.shouldersurfing.api.model.CrosshairType;
import com.github.exopandora.shouldersurfing.api.model.CrosshairVisibility;
import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.api.model.TurningMode;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface IClientConfig
{
	double getOffsetX();
	double getOffsetY();
	double getOffsetZ();
	
	double getMinOffsetX();
	double getMinOffsetY();
	double getMinOffsetZ();
	
	double getMaxOffsetX();
	double getMaxOffsetY();
	double getMaxOffsetZ();
	
	boolean isUnlimitedOffsetX();
	boolean isUnlimitedOffsetY();
	boolean isUnlimitedOffsetZ();
	
	double getPassengerOffsetXMultiplier();
	double getPassengerOffsetYMultiplier();
	double getPassengerOffsetZMultiplier();
	
	default Vec3 getPassengerOffsetMultipliers()
	{
		return new Vec3(this.getPassengerOffsetXMultiplier(), this.getPassengerOffsetYMultiplier(), this.getPassengerOffsetZMultiplier());
	}
	
	double getSprintOffsetXMultiplier();
	double getSprintOffsetYMultiplier();
	double getSprintOffsetZMultiplier();
	
	default Vec3 getSprintOffsetMultipliers()
	{
		return new Vec3(this.getSprintOffsetXMultiplier(), this.getSprintOffsetYMultiplier(), this.getSprintOffsetZMultiplier());
	}
	
	double getAimingOffsetXMultiplier();
	double getAimingOffsetYMultiplier();
	double getAimingOffsetZMultiplier();
	
	default Vec3 getAimingOffsetMultipliers()
	{
		return new Vec3(this.getAimingOffsetXMultiplier(), this.getAimingOffsetYMultiplier(), this.getAimingOffsetZMultiplier());
	}
	
	double getFallFlyingOffsetXModifier();
	double getFallFlyingOffsetYModifier();
	double getFallFlyingOffsetZModifier();
	
	default Vec3 getFallFlyingOffsetModifiers()
	{
		return new Vec3(this.getFallFlyingOffsetXModifier(), this.getFallFlyingOffsetYModifier(), this.getFallFlyingOffsetZModifier());
	}
	
	CrosshairVisibility getCrosshairVisibility(Perspective perspective);
	
	boolean useCustomRaytraceDistance();
	
	double keepCameraOutOfHeadMultiplier();
	
	boolean replaceDefaultPerspective();
	
	boolean skipThirdPersonFront();
	
	Perspective getDefaultPerspective();
	
	CrosshairType getCrosshairType();
	
	boolean doRememberLastPerspective();
	
	double getCameraStepSize();
	
	boolean doCenterCameraWhenClimbing();
	boolean doCenterCameraWhenFallFlying();
	
	double getCameraTransitionSpeedMultiplier();
	
	double getCenterCameraWhenLookingDownAngle();
	
	double getHidePlayerWhenLookingUpAngle();
	
	boolean doDynamicallyAdjustOffsets();
	
	boolean isPlayerTransparencyEnabled();
	
	TurningMode getTurningModeWhenUsingItem();
	TurningMode getTurningModeWhenAttacking();
	TurningMode getTurningModeWhenInteracting();
	TurningMode getTurningModeWhenPicking();
	
	int getTurningLockTime();
	
	boolean isCameraDecoupled();
	
	double getCustomRaytraceDistance();
	
	List<? extends String> getAdaptiveCrosshairHoldItems();
	List<? extends String> getAdaptiveCrosshairUseItems();
	List<? extends String> getAdaptiveCrosshairHoldItemProperties();
	List<? extends String> getAdaptiveCrosshairUseItemProperties();
	
	boolean doCenterPlayerSounds();
	
	boolean doSyncPlayerXRotWithInputs();
}
