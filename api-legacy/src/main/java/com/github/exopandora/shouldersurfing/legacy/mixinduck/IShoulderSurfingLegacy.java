package com.github.exopandora.shouldersurfing.legacy.mixinduck;

import com.github.exopandora.shouldersurfing.api.client.ICameraEntityRenderer;
import com.github.exopandora.shouldersurfing.api.client.IClientConfig;
import com.github.exopandora.shouldersurfing.api.client.ICrosshairRenderer;
import com.github.exopandora.shouldersurfing.api.client.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.model.Perspective;

public interface IShoulderSurfingLegacy {
	default IShoulderSurfingCamera getCamera() {
		throw new AssertionError();
	}
	
	default ICameraEntityRenderer getCameraEntityRenderer() {
		throw new AssertionError();
	}
	
	default ICrosshairRenderer getCrosshairRenderer() {
		throw new AssertionError();
	}
	
	default IObjectPicker getObjectPicker() {
		throw new AssertionError();
	}
	
	default IClientConfig getClientConfig() {
		throw new AssertionError();
	}
	
	default void changePerspective(Perspective perspective) {
		throw new AssertionError();
	}
}
