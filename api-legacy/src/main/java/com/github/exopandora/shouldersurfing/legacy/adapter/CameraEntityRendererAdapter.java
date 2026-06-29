package com.github.exopandora.shouldersurfing.legacy.adapter;

import com.github.exopandora.shouldersurfing.api.client.ICameraEntityRenderer;

class CameraEntityRendererAdapter implements ICameraEntityRenderer {
	private final com.github.exopandora.shouldersurfing.api.client.renderer.ICameraEntityRenderer cameraEntityRenderer;
	
	protected CameraEntityRendererAdapter(com.github.exopandora.shouldersurfing.api.client.renderer.ICameraEntityRenderer cameraEntityRenderer) {
		this.cameraEntityRenderer = cameraEntityRenderer;
	}
	
	@Override
	public boolean isRenderingCameraEntity() {
		return this.cameraEntityRenderer.isRenderingCameraEntity();
	}
	
	@Override
	public float getCameraEntityAlpha() {
		return this.cameraEntityRenderer.getCameraEntityAlpha();
	}
	
	@Override
	public int getCameraEntityAlphaAsInt() {
		return this.cameraEntityRenderer.getCameraEntityAlphaAsInt();
	}
}
