package com.github.exopandora.shouldersurfing.api.client;

import com.github.exopandora.shouldersurfing.api.model.Perspective;

public interface IShoulderSurfing
{
	IShoulderSurfingCamera getCamera();
	
	ICameraEntityRenderer getCameraEntityRenderer();
	
	ICrosshairRenderer getCrosshairRenderer();
	
	IObjectPicker getObjectPicker();
	
	IClientConfig getClientConfig();
	
	boolean isShoulderSurfing();
	
	boolean isAiming();
	
	boolean isCameraDecoupled();
	
	boolean isFreeLooking();
	
	void changePerspective(Perspective perspective);
	
	void togglePerspective();
	
	void swapShoulder();
	
	void resetState();
}
