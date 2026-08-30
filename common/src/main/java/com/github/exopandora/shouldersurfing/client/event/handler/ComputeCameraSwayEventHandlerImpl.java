package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeCameraSwayEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraSwayEventHandler;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.util.Mth;

public enum ComputeCameraSwayEventHandlerImpl implements ComputeCameraSwayEventHandler {
	INSTANCE;
	
	@Override
	public void handle(ComputeCameraSwayEvent event) {
		var tickEventHandler = CameraEntityDeltaMovementTickEventHandler.INSTANCE;
		var camera = ShoulderSurfing.getInstance().getCamera();
		var deltaMovementLerped = tickEventHandler.getDeltaMovementO().lerp(tickEventHandler.getDeltaMovement(), event.getPartialTick())
			.yRot(camera.getYRot() * Mth.DEG_TO_RAD)
			.xRot(camera.getXRot() * Mth.DEG_TO_RAD);
		var cameraConfig = Config.CLIENT.getCameraConfig();
		var maxVelocityX = cameraConfig.getCameraSwayXMaxVelocity() / 20;
		var maxVelocityZ = cameraConfig.getCameraSwayZMaxVelocity() / 20;
		var maxAngleX = cameraConfig.getCameraSwayXMaxAngle();
		var maxAngleZ = cameraConfig.getCameraSwayZMaxAngle();
		var swayX = Math.min(Math.abs(deltaMovementLerped.y), maxVelocityX) / maxVelocityX * maxAngleX * Math.signum(deltaMovementLerped.y);
		var swayZ = Math.min(Math.abs(deltaMovementLerped.x), maxVelocityZ) / maxVelocityZ * maxAngleZ * Math.signum(deltaMovementLerped.x);
		event.setResult(event.getResult().add((float) swayX, (float) swayZ));
	}
}
