package com.github.exopandora.shouldersurfing.api.client;

public interface ICameraEntityRenderer extends com.github.exopandora.shouldersurfing.api.client.renderer.ICameraEntityRenderer {
	@Override
	boolean isRenderingCameraEntity();
	
	@Override
	float getCameraEntityAlpha();
	
	@Override
	int getCameraEntityAlphaAsInt();
}
