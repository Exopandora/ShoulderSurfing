package com.github.exopandora.shouldersurfing.api.client;

import com.github.exopandora.shouldersurfing.api.model.Perspective;

public interface IShoulderSurfing
{
	IShoulderSurfingCamera getCamera();
	
	ICameraEntityRenderer getCameraEntityRenderer();
	
	ICrosshairRenderer getCrosshairRenderer();
	
	IObjectPicker getObjectPicker();
	
	boolean isShoulderSurfing();
	
	boolean isAiming();
	
	boolean isFreeLooking();
	
	void changePerspective(Perspective perspective);
	
	void togglePerspective();
	
	void swapShoulder();
	
	void resetState();
}
