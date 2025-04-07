package com.github.exopandora.shouldersurfing.api.client;

import com.github.exopandora.shouldersurfing.api.model.CrosshairType;
import com.github.exopandora.shouldersurfing.api.model.CrosshairVisibility;
import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.api.model.PickOrigin;
import com.github.exopandora.shouldersurfing.api.model.PickVector;
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
	
	double getFallFlyingOffsetXMultiplier();
	double getFallFlyingOffsetYMultiplier();
	double getFallFlyingOffsetZMultiplier();
	
	default Vec3 getFallFlyingMultipliers()
	{
		return new Vec3(this.getFallFlyingOffsetXMultiplier(), this.getFallFlyingOffsetYMultiplier(), this.getFallFlyingOffsetZMultiplier());
	}
	
	double getClimbingOffsetXMultiplier();
	double getClimbingOffsetYMultiplier();
	double getClimbingOffsetZMultiplier();
	
	default Vec3 getClimbingMultipliers()
	{
		return new Vec3(this.getClimbingOffsetXMultiplier(), this.getClimbingOffsetYMultiplier(), this.getClimbingOffsetZMultiplier());
	}
	
	double getPassengerOffsetXModifier();
	double getPassengerOffsetYModifier();
	double getPassengerOffsetZModifier();
	
	default Vec3 getPassengerOffsetModifiers()
	{
		return new Vec3(this.getPassengerOffsetXModifier(), this.getPassengerOffsetYModifier(), this.getPassengerOffsetZModifier());
	}
	
	double getSprintOffsetXModifier();
	double getSprintOffsetYModifier();
	double getSprintOffsetZModifier();
	
	default Vec3 getSprintOffsetModifiers()
	{
		return new Vec3(this.getSprintOffsetXModifier(), this.getSprintOffsetYModifier(), this.getSprintOffsetZModifier());
	}
	
	double getAimingOffsetXModifier();
	double getAimingOffsetYModifier();
	double getAimingOffsetZModifier();
	
	default Vec3 getAimingOffsetModifiers()
	{
		return new Vec3(this.getAimingOffsetXModifier(), this.getAimingOffsetYModifier(), this.getAimingOffsetZModifier());
	}
	
	double getFallFlyingOffsetXModifier();
	double getFallFlyingOffsetYModifier();
	double getFallFlyingOffsetZModifier();
	
	default Vec3 getFallFlyingOffsetModifiers()
	{
		return new Vec3(this.getFallFlyingOffsetXModifier(), this.getFallFlyingOffsetYModifier(), this.getFallFlyingOffsetZModifier());
	}
	
	double getClimbingOffsetXModifier();
	double getClimbingOffsetYModifier();
	double getClimbingOffsetZModifier();
	
	default Vec3 getClimbingOffsetModifiers()
	{
		return new Vec3(this.getClimbingOffsetXModifier(), this.getClimbingOffsetYModifier(), this.getClimbingOffsetZModifier());
	}
	
	CrosshairVisibility getCrosshairVisibility(Perspective perspective);
	
	boolean useCustomRaytraceDistance();
	
	double keepCameraOutOfHeadMultiplier();
	
	boolean replaceDefaultPerspective();
	
	@Deprecated(forRemoval = true)
	default boolean skipThirdPersonFront()
	{
		return this.isThirdPersonFrontEnabled();
	}
	
	boolean isFirstPersonEnabled();
	boolean isThirdPersonFrontEnabled();
	boolean isThirdPersonBackEnabled();
	
	Perspective getDefaultPerspective();
	
	CrosshairType getCrosshairType();
	
	boolean doRememberLastPerspective();
	
	double getCameraStepSize();
	
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
	
	PickOrigin getEntityPickOrigin();
	PickOrigin getBlockPickOrigin();
	
	PickVector getPickVector();
	
	boolean isCameraDecoupled();
	
	boolean doOrientCameraOnTeleport();
	
	boolean isFovOverrideEnabled();
	
	float getFovOverride();
	
	double getCustomRaytraceDistance();
	
	List<? extends String> getAdaptiveCrosshairHoldItems();
	List<? extends String> getAdaptiveCrosshairUseItems();
	List<? extends String> getAdaptiveCrosshairHoldItemProperties();
	List<? extends String> getAdaptiveCrosshairUseItemProperties();
	
	boolean getShowObstructionCrosshair();
	boolean showObstructionIndicatorWhenAiming();
	int getObstructionIndicatorMinDistanceToCrosshair();
	double getObstructionIndicatorMaxDistanceToObstruction();
	
	boolean doCenterPlayerSounds();
	
	boolean shouldPlayerXRotFollowCamera();
	boolean shouldPlayerYRotFollowCamera();
	
	double getPlayerYRotFollowAngleLimit();
	
	boolean getEpicFightDecoupledCameraLockOn();
}
