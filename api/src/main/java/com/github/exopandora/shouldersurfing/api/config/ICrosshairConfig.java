package com.github.exopandora.shouldersurfing.api.config;

import com.github.exopandora.shouldersurfing.api.client.CrosshairType;
import com.github.exopandora.shouldersurfing.api.client.CrosshairVisibility;
import com.github.exopandora.shouldersurfing.api.client.Perspective;

import java.util.List;

public interface ICrosshairConfig {
	CrosshairVisibility getCrosshairVisibility(Perspective perspective);
	
	CrosshairType getCrosshairType();
	
	List<? extends String> getAdaptiveCrosshairHoldItems();
	
	List<? extends String> getAdaptiveCrosshairUseItems();
	
	List<? extends String> getAdaptiveCrosshairHoldItemAnimations();
	
	List<? extends String> getAdaptiveCrosshairUseItemAnimations();
	
	List<? extends String> getAdaptiveCrosshairHoldItemDefaultComponents();
	
	List<? extends String> getAdaptiveCrosshairUseItemDefaultComponents();
	
	List<? extends String> getAdaptiveCrosshairHoldItemComponents();
	
	List<? extends String> getAdaptiveCrosshairUseItemComponents();
	
	boolean isObstructionIndicatorEnabled();
	
	boolean isObstructionIndicatorOnlyShownWhenAiming();
	
	int getObstructionIndicatorMinDistanceToCrosshair();
	
	double getObstructionIndicatorMaxDistanceToObstruction();
}
