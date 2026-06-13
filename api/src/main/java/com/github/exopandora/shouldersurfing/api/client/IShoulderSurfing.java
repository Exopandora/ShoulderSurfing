package com.github.exopandora.shouldersurfing.api.client;

import com.github.exopandora.shouldersurfing.api.client.renderer.ICameraEntityRenderer;
import com.github.exopandora.shouldersurfing.api.client.renderer.ICrosshairRenderer;
import com.github.exopandora.shouldersurfing.api.client.world.phys.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.config.IClientConfig;

import java.util.ServiceLoader;

public interface IShoulderSurfing {
	IShoulderSurfing INSTANCE = ServiceLoader.load(IShoulderSurfing.class).findFirst().orElseThrow();
	
	IShoulderSurfingCamera getCamera();
	
	ICameraEntityRenderer getCameraEntityRenderer();
	
	ICrosshairRenderer getCrosshairRenderer();
	
	IObjectPicker getObjectPicker();
	
	IClientConfig getClientConfig();
	
	boolean isShoulderSurfing();
	
	boolean isAiming();
	
	boolean isCameraDecoupled();
	
	boolean isFreeLooking();
	
	boolean isTemporaryFirstPerson();
	
	void changePerspective(Perspective perspective);
	
	void togglePerspective();
	
	void toggleCameraCoupling();
	
	void swapShoulder();
	
	boolean isLookFollowingCrosshairTarget();
	
	void resetState();
	
	static IShoulderSurfing getInstance() {
		return INSTANCE;
	}
}
