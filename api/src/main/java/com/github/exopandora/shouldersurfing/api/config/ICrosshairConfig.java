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
	
	List<? extends String> getAdaptiveCrosshairHoldItemProperties();
	
	List<? extends String> getAdaptiveCrosshairUseItemProperties();
	
	boolean isObstructionIndicatorEnabled();
	
	boolean isObstructionIndicatorOnlyShownWhenAiming();
	
	int getObstructionIndicatorMinDistanceToCrosshair();
	
	double getObstructionIndicatorMaxDistanceToObstruction();
}
