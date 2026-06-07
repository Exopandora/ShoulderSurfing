package com.github.exopandora.shouldersurfing.api.client.config;

import com.github.exopandora.shouldersurfing.api.model.TurningMode;

public interface IPlayerConfig
{
	double getHidePlayerWhenLookingUpAngle();
	
	boolean isPlayerTransparencyEnabled();
	
	boolean isPlayerTransparentWhenAiming();
	
	TurningMode getTurningModeWhenUsingItem();
	
	TurningMode getTurningModeWhenAttacking();
	
	TurningMode getTurningModeWhenInteracting();
	
	TurningMode getTurningModeWhenPicking();
	
	int getTurningLockTime();
	
	double getTurningSpeedMultiplier();
	
	boolean isPlayerXRotFollowingCamera();
	
	boolean isPlayerYRotFollowingCamera();
	
	double getPlayerYRotFollowAngleLimit();
}
