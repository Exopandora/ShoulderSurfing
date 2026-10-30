package com.github.exopandora.shouldersurfing.api.config;

import com.github.exopandora.shouldersurfing.api.client.gui.MapOverlayAnchor;

public interface IGuiConfig {
	boolean isMapOverlayEnabled();
	
	MapOverlayAnchor getMapOverlayAnchor();
	
	int getMapOverlayScale();
	
	int getMapOverlayInsetTop();
	
	int getMapOverlayInsetRight();
	
	int getMapOverlayInsetBottom();
	
	int getMapOverlayInsetLeft();
}
