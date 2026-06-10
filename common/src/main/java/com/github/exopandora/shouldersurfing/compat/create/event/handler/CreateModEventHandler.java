package com.github.exopandora.shouldersurfing.compat.create.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.ComputeTargetCameraOffsetEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeTargetCameraOffsetEventHandler;
import com.simibubi.create.content.trains.CameraDistanceModifier;

public class CreateModEventHandler implements ComputeTargetCameraOffsetEventHandler {
	@Override
	public void handle(ComputeTargetCameraOffsetEvent event) {
		event.setResult(event.getResult().multiply(1.0D, 1.0D, CameraDistanceModifier.getMultiplier()));
	}
}
