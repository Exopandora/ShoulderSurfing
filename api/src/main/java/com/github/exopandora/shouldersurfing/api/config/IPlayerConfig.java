package com.github.exopandora.shouldersurfing.api.config;

import com.github.exopandora.shouldersurfing.api.client.TurningMode;

public interface IPlayerConfig {
	double getHidePlayerWhenLookingUpAngle();
	
	boolean isPlayerTransparencyEnabled();
	
	boolean isPlayerTransparentWhenAiming();
	
	TurningMode getTurningModeWhenUsingItem();
	
	TurningMode getTurningModeWhenAttacking();
	
	TurningMode getTurningModeWhenInteracting();
	
	TurningMode getTurningModeWhenPicking();
	
	int getTurningLockTime();
	
	double getTurningSpeedMultiplier();
	
	boolean isPlayerXRotTurningWithCamera();
	
	boolean isPlayerYRotTurningWithCamera();
	
	double getPlayerYRotTurnAngleLimit();
}
