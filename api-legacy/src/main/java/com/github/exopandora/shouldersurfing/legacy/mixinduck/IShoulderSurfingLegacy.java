package com.github.exopandora.shouldersurfing.legacy.mixinduck;

import com.github.exopandora.shouldersurfing.api.client.ICameraEntityRenderer;
import com.github.exopandora.shouldersurfing.api.client.IClientConfig;
import com.github.exopandora.shouldersurfing.api.client.ICrosshairRenderer;
import com.github.exopandora.shouldersurfing.api.client.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.model.Perspective;

public interface IShoulderSurfingLegacy {
	default IShoulderSurfingCamera getCamera() {
		throw new AssertionError("Not implemented");
	}
	
	default ICameraEntityRenderer getCameraEntityRenderer() {
		throw new AssertionError("Not implemented");
	}
	
	default ICrosshairRenderer getCrosshairRenderer() {
		throw new AssertionError("Not implemented");
	}
	
	default IObjectPicker getObjectPicker() {
		throw new AssertionError("Not implemented");
	}
	
	default IClientConfig getClientConfig() {
		throw new AssertionError("Not implemented");
	}
	
	default void changePerspective(Perspective perspective) {
		throw new AssertionError("Not implemented");
	}
}
