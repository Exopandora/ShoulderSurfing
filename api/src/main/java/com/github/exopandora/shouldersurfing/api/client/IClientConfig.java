package com.github.exopandora.shouldersurfing.api.client;

import com.github.exopandora.shouldersurfing.api.model.CrosshairType;
import com.github.exopandora.shouldersurfing.api.model.CrosshairVisibility;
import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.api.model.PickOrigin;
import com.github.exopandora.shouldersurfing.api.model.PickVector;
import com.github.exopandora.shouldersurfing.api.model.TurningMode;
import net.minecraft.util.math.vector.Vector3d;

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
	
	default Vector3d getPassengerOffsetMultipliers()
	{
		return new Vector3d(this.getPassengerOffsetXMultiplier(), this.getPassengerOffsetYMultiplier(), this.getPassengerOffsetZMultiplier());
	}
	
	double getSprintOffsetXMultiplier();
	double getSprintOffsetYMultiplier();
	double getSprintOffsetZMultiplier();
	
	default Vector3d getSprintOffsetMultipliers()
	{
		return new Vector3d(this.getSprintOffsetXMultiplier(), this.getSprintOffsetYMultiplier(), this.getSprintOffsetZMultiplier());
	}
	
	double getAimingOffsetXMultiplier();
	double getAimingOffsetYMultiplier();
	double getAimingOffsetZMultiplier();
	
	default Vector3d getAimingOffsetMultipliers()
	{
		return new Vector3d(this.getAimingOffsetXMultiplier(), this.getAimingOffsetYMultiplier(), this.getAimingOffsetZMultiplier());
	}
	
	double getFallFlyingOffsetXMultiplier();
	double getFallFlyingOffsetYMultiplier();
	double getFallFlyingOffsetZMultiplier();
	
	default Vector3d getFallFlyingMultipliers()
	{
		return new Vector3d(this.getFallFlyingOffsetXMultiplier(), this.getFallFlyingOffsetYMultiplier(), this.getFallFlyingOffsetZMultiplier());
	}
	
	double getClimbingOffsetXMultiplier();
	double getClimbingOffsetYMultiplier();
	double getClimbingOffsetZMultiplier();
	
	default Vector3d getClimbingMultipliers()
	{
		return new Vector3d(this.getClimbingOffsetXMultiplier(), this.getClimbingOffsetYMultiplier(), this.getClimbingOffsetZMultiplier());
	}
	
	double getPassengerOffsetXModifier();
	double getPassengerOffsetYModifier();
	double getPassengerOffsetZModifier();
	
	default Vector3d getPassengerOffsetModifiers()
	{
		return new Vector3d(this.getPassengerOffsetXModifier(), this.getPassengerOffsetYModifier(), this.getPassengerOffsetZModifier());
	}
	
	double getSprintOffsetXModifier();
	double getSprintOffsetYModifier();
	double getSprintOffsetZModifier();
	
	default Vector3d getSprintOffsetModifiers()
	{
		return new Vector3d(this.getSprintOffsetXModifier(), this.getSprintOffsetYModifier(), this.getSprintOffsetZModifier());
	}
	
	double getAimingOffsetXModifier();
	double getAimingOffsetYModifier();
	double getAimingOffsetZModifier();
	
	default Vector3d getAimingOffsetModifiers()
	{
		return new Vector3d(this.getAimingOffsetXModifier(), this.getAimingOffsetYModifier(), this.getAimingOffsetZModifier());
	}
	
	double getFallFlyingOffsetXModifier();
	double getFallFlyingOffsetYModifier();
	double getFallFlyingOffsetZModifier();
	
	default Vector3d getFallFlyingOffsetModifiers()
	{
		return new Vector3d(this.getFallFlyingOffsetXModifier(), this.getFallFlyingOffsetYModifier(), this.getFallFlyingOffsetZModifier());
	}
	
	double getClimbingOffsetXModifier();
	double getClimbingOffsetYModifier();
	double getClimbingOffsetZModifier();
	
	default Vector3d getClimbingOffsetModifiers()
	{
		return new Vector3d(this.getClimbingOffsetXModifier(), this.getClimbingOffsetYModifier(), this.getClimbingOffsetZModifier());
	}
	
	CrosshairVisibility getCrosshairVisibility(Perspective perspective);
	
	boolean useCustomRaytraceDistance();
	
	double keepCameraOutOfHeadMultiplier();
	
	boolean replaceDefaultPerspective();
	
	@Deprecated
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
}
