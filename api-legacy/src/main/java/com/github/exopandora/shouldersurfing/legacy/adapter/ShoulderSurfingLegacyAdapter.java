package com.github.exopandora.shouldersurfing.legacy.adapter;

import com.github.exopandora.shouldersurfing.api.client.ICameraEntityRenderer;
import com.github.exopandora.shouldersurfing.api.client.IClientConfig;
import com.github.exopandora.shouldersurfing.api.client.ICrosshairRenderer;
import com.github.exopandora.shouldersurfing.api.client.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.legacy.mixinduck.IShoulderSurfingLegacy;

public class ShoulderSurfingLegacyAdapter implements IShoulderSurfingLegacy {
	@Override
	public IShoulderSurfingCamera getCamera() {
		return IShoulderSurfing.getInstance().getCamera();
	}
	
	@Override
	public ICameraEntityRenderer getCameraEntityRenderer() {
		return new CameraEntityRendererAdapter(IShoulderSurfing.getInstance().getCameraEntityRenderer());
	}
	
	@Override
	public ICrosshairRenderer getCrosshairRenderer() {
		return new CrosshairRendererAdapter(IShoulderSurfing.getInstance().getCrosshairRenderer());
	}
	
	@Override
	public IObjectPicker getObjectPicker() {
		return new ObjectPickerAdapter(IShoulderSurfing.getInstance().getObjectPicker());
	}
	
	@Override
	public IClientConfig getClientConfig() {
		return new ClientConfigAdapter(IShoulderSurfing.getInstance().getClientConfig());
	}
	
	@Override
	public void changePerspective(Perspective perspective) {
		IShoulderSurfing.getInstance().changePerspective(perspective.toNewApi());
	}
}
