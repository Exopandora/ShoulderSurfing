package com.github.exopandora.shouldersurfing.legacy.adapter;

import com.github.exopandora.shouldersurfing.api.client.IClientConfig;
import com.github.exopandora.shouldersurfing.api.model.CrosshairType;
import com.github.exopandora.shouldersurfing.api.model.CrosshairVisibility;
import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.api.model.PickOrigin;
import com.github.exopandora.shouldersurfing.api.model.PickVector;
import com.github.exopandora.shouldersurfing.api.model.TurningMode;
import com.github.exopandora.shouldersurfing.api.model.ViewBobbingMode;

import java.util.List;

class ClientConfigAdapter implements IClientConfig {
	private final com.github.exopandora.shouldersurfing.api.config.IClientConfig config;
	
	protected ClientConfigAdapter(com.github.exopandora.shouldersurfing.api.config.IClientConfig config) {
		this.config = config;
	}
	
	@Override
	public double getOffsetX() {
		return this.config.getCameraConfig().getOffsetX();
	}
	
	@Override
	public double getOffsetY() {
		return this.config.getCameraConfig().getOffsetY();
	}
	
	@Override
	public double getOffsetZ() {
		return this.config.getCameraConfig().getOffsetZ();
	}
	
	@Override
	public List<Double> getOffsetXPresets() {
		return this.config.getCameraConfig().getOffsetXPresets();
	}
	
	@Override
	public List<Double> getOffsetYPresets() {
		return this.config.getCameraConfig().getOffsetYPresets();
	}
	
	@Override
	public List<Double> getOffsetZPresets() {
		return this.config.getCameraConfig().getOffsetZPresets();
	}
	
	@Override
	public double getMinOffsetX() {
		return this.config.getCameraConfig().getMinOffsetX();
	}
	
	@Override
	public double getMinOffsetY() {
		return this.config.getCameraConfig().getMinOffsetY();
	}
	
	@Override
	public double getMinOffsetZ() {
		return this.config.getCameraConfig().getMinOffsetZ();
	}
	
	@Override
	public double getMaxOffsetX() {
		return this.config.getCameraConfig().getMaxOffsetX();
	}
	
	@Override
	public double getMaxOffsetY() {
		return this.config.getCameraConfig().getMaxOffsetY();
	}
	
	@Override
	public double getMaxOffsetZ() {
		return this.config.getCameraConfig().getMaxOffsetZ();
	}
	
	@Override
	public boolean isUnlimitedOffsetX() {
		return this.config.getCameraConfig().isOffsetXUnlimited();
	}
	
	@Override
	public boolean isUnlimitedOffsetY() {
		return this.config.getCameraConfig().isOffsetYUnlimited();
	}
	
	@Override
	public boolean isUnlimitedOffsetZ() {
		return this.config.getCameraConfig().isOffsetZUnlimited();
	}
	
	@Override
	public double getPassengerOffsetXMultiplier() {
		return this.config.getCameraConfig().getPassengerOffsetXMultiplier();
	}
	
	@Override
	public double getPassengerOffsetYMultiplier() {
		return this.config.getCameraConfig().getPassengerOffsetYMultiplier();
	}
	
	@Override
	public double getPassengerOffsetZMultiplier() {
		return this.config.getCameraConfig().getPassengerOffsetZMultiplier();
	}
	
	@Override
	public double getSprintOffsetXMultiplier() {
		return this.config.getCameraConfig().getSprintOffsetXMultiplier();
	}
	
	@Override
	public double getSprintOffsetYMultiplier() {
		return this.config.getCameraConfig().getSprintOffsetYMultiplier();
	}
	
	@Override
	public double getSprintOffsetZMultiplier() {
		return this.config.getCameraConfig().getSprintOffsetZMultiplier();
	}
	
	@Override
	public double getAimingOffsetXMultiplier() {
		return this.config.getCameraConfig().getAimingOffsetXMultiplier();
	}
	
	@Override
	public double getAimingOffsetYMultiplier() {
		return this.config.getCameraConfig().getAimingOffsetYMultiplier();
	}
	
	@Override
	public double getAimingOffsetZMultiplier() {
		return this.config.getCameraConfig().getAimingOffsetZMultiplier();
	}
	
	@Override
	public double getFallFlyingOffsetXMultiplier() {
		return this.config.getCameraConfig().getFallFlyingOffsetXMultiplier();
	}
	
	@Override
	public double getFallFlyingOffsetYMultiplier() {
		return this.config.getCameraConfig().getFallFlyingOffsetYMultiplier();
	}
	
	@Override
	public double getFallFlyingOffsetZMultiplier() {
		return this.config.getCameraConfig().getFallFlyingOffsetZMultiplier();
	}
	
	@Override
	public double getClimbingOffsetXMultiplier() {
		return this.config.getCameraConfig().getClimbingOffsetXMultiplier();
	}
	
	@Override
	public double getClimbingOffsetYMultiplier() {
		return this.config.getCameraConfig().getClimbingOffsetYMultiplier();
	}
	
	@Override
	public double getClimbingOffsetZMultiplier() {
		return this.config.getCameraConfig().getClimbingOffsetZMultiplier();
	}
	
	@Override
	public double getPassengerOffsetXModifier() {
		return this.config.getCameraConfig().getPassengerOffsetXModifier();
	}
	
	@Override
	public double getPassengerOffsetYModifier() {
		return this.config.getCameraConfig().getPassengerOffsetYModifier();
	}
	
	@Override
	public double getPassengerOffsetZModifier() {
		return this.config.getCameraConfig().getPassengerOffsetZModifier();
	}
	
	@Override
	public double getSprintOffsetXModifier() {
		return this.config.getCameraConfig().getSprintOffsetXModifier();
	}
	
	@Override
	public double getSprintOffsetYModifier() {
		return this.config.getCameraConfig().getSprintOffsetYModifier();
	}
	
	@Override
	public double getSprintOffsetZModifier() {
		return this.config.getCameraConfig().getSprintOffsetZModifier();
	}
	
	@Override
	public double getAimingOffsetXModifier() {
		return this.config.getCameraConfig().getAimingOffsetXModifier();
	}
	
	@Override
	public double getAimingOffsetYModifier() {
		return this.config.getCameraConfig().getAimingOffsetYModifier();
	}
	
	@Override
	public double getAimingOffsetZModifier() {
		return this.config.getCameraConfig().getAimingOffsetZModifier();
	}
	
	@Override
	public double getFallFlyingOffsetXModifier() {
		return this.config.getCameraConfig().getFallFlyingOffsetXModifier();
	}
	
	@Override
	public double getFallFlyingOffsetYModifier() {
		return this.config.getCameraConfig().getFallFlyingOffsetYModifier();
	}
	
	@Override
	public double getFallFlyingOffsetZModifier() {
		return this.config.getCameraConfig().getFallFlyingOffsetZModifier();
	}
	
	@Override
	public double getClimbingOffsetXModifier() {
		return this.config.getCameraConfig().getClimbingOffsetXModifier();
	}
	
	@Override
	public double getClimbingOffsetYModifier() {
		return this.config.getCameraConfig().getClimbingOffsetYModifier();
	}
	
	@Override
	public double getClimbingOffsetZModifier() {
		return this.config.getCameraConfig().getClimbingOffsetZModifier();
	}
	
	@Override
	public CrosshairVisibility getCrosshairVisibility(Perspective perspective) {
		return CrosshairVisibility.fromNewApi(this.config.getCrosshairConfig().getCrosshairVisibility(perspective.toNewApi()));
	}
	
	@Override
	public boolean useCustomRaytraceDistance() {
		return this.config.getObjectPickerConfig().isCustomRaytraceDistanceEnabled();
	}
	
	@Override
	public double keepCameraOutOfHeadMultiplier() {
		return this.config.getCameraConfig().keepCameraOutOfHeadMultiplier();
	}
	
	@Override
	public boolean replaceDefaultPerspective() {
		return this.config.getPerspectiveConfig().isThirdPersonReplaced();
	}
	
	@Override
	public boolean isFirstPersonEnabled() {
		return this.config.getPerspectiveConfig().isFirstPersonEnabled();
	}
	
	@Override
	public boolean isThirdPersonFrontEnabled() {
		return this.config.getPerspectiveConfig().isThirdPersonFrontEnabled();
	}
	
	@Override
	public boolean isThirdPersonBackEnabled() {
		return this.config.getPerspectiveConfig().isThirdPersonBackEnabled();
	}
	
	@Override
	public Perspective getDefaultPerspective() {
		return Perspective.fromNewApi(this.config.getPerspectiveConfig().getDefaultPerspective());
	}
	
	@Override
	public CrosshairType getCrosshairType() {
		return CrosshairType.fromNewApi(this.config.getCrosshairConfig().getCrosshairType());
	}
	
	@Override
	public boolean doRememberLastPerspective() {
		return this.config.getPerspectiveConfig().isPerspectivePersistent();
	}
	
	@Override
	public double getCameraStepSize() {
		return this.config.getCameraConfig().getCameraStepSize();
	}
	
	@Override
	public double getCameraTransitionSpeedMultiplier() {
		return this.config.getCameraConfig().getCameraTransitionSpeedMultiplier();
	}
	
	@Override
	public double getCenterCameraWhenLookingDownAngle() {
		return this.config.getCameraConfig().getCenterCameraWhenLookingDownAngle();
	}
	
	@Override
	public double getHidePlayerWhenLookingUpAngle() {
		return this.config.getPlayerConfig().getHidePlayerWhenLookingUpAngle();
	}
	
	@Override
	public boolean doDynamicallyAdjustOffsets() {
		return this.config.getCameraConfig().isOffsetDynamic();
	}
	
	@Override
	public boolean isPlayerTransparencyEnabled() {
		return this.config.getPlayerConfig().isPlayerTransparencyEnabled();
	}
	
	@Override
	public boolean turnPlayerTransparentWhenAiming() {
		return this.config.getPlayerConfig().isPlayerTransparentWhenAiming();
	}
	
	@Override
	public TurningMode getTurningModeWhenUsingItem() {
		return TurningMode.fromNewApi(this.config.getPlayerConfig().getTurningModeWhenUsingItem());
	}
	
	@Override
	public TurningMode getTurningModeWhenAttacking() {
		return TurningMode.fromNewApi(this.config.getPlayerConfig().getTurningModeWhenAttacking());
	}
	
	@Override
	public TurningMode getTurningModeWhenInteracting() {
		return TurningMode.fromNewApi(this.config.getPlayerConfig().getTurningModeWhenInteracting());
	}
	
	@Override
	public TurningMode getTurningModeWhenPicking() {
		return TurningMode.fromNewApi(this.config.getPlayerConfig().getTurningModeWhenPicking());
	}
	
	@Override
	public int getTurningLockTime() {
		return this.config.getPlayerConfig().getTurningLockTime();
	}
	
	@Override
	public double getTurningSpeedMultiplier() {
		return this.config.getPlayerConfig().getTurningSpeedMultiplier();
	}
	
	@Override
	public PickOrigin getEntityPickOrigin() {
		return PickOrigin.fromNewApi(this.config.getObjectPickerConfig().getEntityPickOrigin());
	}
	
	@Override
	public PickOrigin getBlockPickOrigin() {
		return PickOrigin.fromNewApi(this.config.getObjectPickerConfig().getBlockPickOrigin());
	}
	
	@Override
	public PickVector getPickVector() {
		return PickVector.fromNewApi(this.config.getObjectPickerConfig().getPickVector());
	}
	
	@Override
	public boolean isCameraDecoupled() {
		return this.config.getCameraConfig().isCameraDecoupled();
	}
	
	@Override
	public boolean doOrientCameraOnTeleport() {
		return this.config.getCameraConfig().isCameraOrientedOnTeleport();
	}
	
	@Override
	public boolean isFovOverrideEnabled() {
		return this.config.getCameraConfig().isFovOverrideEnabled();
	}
	
	@Override
	public float getFovOverride() {
		return this.config.getCameraConfig().getFovOverride();
	}
	
	@Override
	public ViewBobbingMode getViewBobbingMode() {
		return ViewBobbingMode.fromNewApi(this.config.getCameraConfig().getViewBobbingMode());
	}
	
	@Override
	public boolean getFollowPlayerRotations() {
		return this.config.getCameraConfig().isCameraTurningWithPlayer();
	}
	
	@Override
	public int getFollowPlayerRotationsDelay() {
		return this.config.getCameraConfig().getCameraTurningWithPlayerDelay();
	}
	
	@Override
	public double getCameraDragXMultiplier() {
		return this.config.getCameraConfig().getCameraDragXMultiplier();
	}
	
	@Override
	public double getCameraDragYMultiplier() {
		return this.config.getCameraConfig().getCameraDragYMultiplier();
	}
	
	@Override
	public double getCameraDragZMultiplier() {
		return this.config.getCameraConfig().getCameraDragZMultiplier();
	}
	
	@Override
	public double getCameraSwayXMaxAngle() {
		return this.config.getCameraConfig().getCameraSwayXMaxAngle();
	}
	
	@Override
	public double getCameraSwayZMaxAngle() {
		return this.config.getCameraConfig().getCameraSwayZMaxAngle();
	}
	
	@Override
	public double getCameraSwayXMaxVelocity() {
		return this.config.getCameraConfig().getCameraSwayXMaxVelocity();
	}
	
	@Override
	public double getCameraSwayZMaxVelocity() {
		return this.config.getCameraConfig().getCameraSwayZMaxVelocity();
	}
	
	@Override
	public double getCustomRaytraceDistance() {
		return this.config.getObjectPickerConfig().getCustomRaytraceDistance();
	}
	
	@Override
	public List<? extends String> getAdaptiveCrosshairHoldItems() {
		return this.config.getCrosshairConfig().getAdaptiveCrosshairHoldItems();
	}
	
	@Override
	public List<? extends String> getAdaptiveCrosshairUseItems() {
		return this.config.getCrosshairConfig().getAdaptiveCrosshairUseItems();
	}
	
	@Override
	public List<? extends String> getAdaptiveCrosshairHoldItemProperties() {
		return this.config.getCrosshairConfig().getAdaptiveCrosshairHoldItemProperties();
	}
	
	@Override
	public List<? extends String> getAdaptiveCrosshairUseItemProperties() {
		return this.config.getCrosshairConfig().getAdaptiveCrosshairUseItemProperties();
	}
	
	@Override
	public boolean getShowObstructionCrosshair() {
		return this.config.getCrosshairConfig().isObstructionIndicatorEnabled();
	}
	
	@Override
	public boolean showObstructionIndicatorWhenAiming() {
		return this.config.getCrosshairConfig().isObstructionIndicatorOnlyShownWhenAiming();
	}
	
	@Override
	public int getObstructionIndicatorMinDistanceToCrosshair() {
		return this.config.getCrosshairConfig().getObstructionIndicatorMinDistanceToCrosshair();
	}
	
	@Override
	public double getObstructionIndicatorMaxDistanceToObstruction() {
		return this.config.getCrosshairConfig().getObstructionIndicatorMaxDistanceToObstruction();
	}
	
	@Override
	public boolean doCenterPlayerSounds() {
		return this.config.getAudioConfig().isPlayerSoundCentered();
	}
	
	@Override
	public boolean shouldPlayerXRotFollowCamera() {
		return this.config.getPlayerConfig().isPlayerXRotTurningWithCamera();
	}
	
	@Override
	public boolean shouldPlayerYRotFollowCamera() {
		return this.config.getPlayerConfig().isPlayerYRotTurningWithCamera();
	}
	
	@Override
	public double getPlayerYRotFollowAngleLimit() {
		return this.config.getPlayerConfig().getPlayerYRotTurnAngleLimit();
	}
	
	@Override
	public List<? extends String> getCuriosAdaptiveCrosshairItems() {
		return this.config.getIntegrationsConfig().getCuriosAdaptiveCrosshairItems();
	}
	
	@Override
	public List<? extends String> getCuriosAdaptiveCrosshairItemProperties() {
		return this.config.getIntegrationsConfig().getCuriosAdaptiveCrosshairItemProperties();
	}
}
