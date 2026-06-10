package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.SetupCameraRotationEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.SetupCameraRotationEventHandler;
import com.github.exopandora.shouldersurfing.api.util.EntityHelper;

public class SetupCameraRotationEventHandlerImpl implements SetupCameraRotationEventHandler {
	@Override
	public void handle(SetupCameraRotationEvent event) {
		if (event.getPlayer().isPassenger()) {
			event.setResult(EntityHelper.applyPassengerRotationConstraints(event.getPlayer(), event.getResult(), event.getCameraRotO()));
		}
	}
}
