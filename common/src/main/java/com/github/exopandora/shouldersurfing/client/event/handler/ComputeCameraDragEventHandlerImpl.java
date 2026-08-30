package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.ComputeCameraDragEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraDragEventHandler;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public enum ComputeCameraDragEventHandlerImpl implements ComputeCameraDragEventHandler {
	INSTANCE;
	
	@Override
	public void handle(ComputeCameraDragEvent event) {
		var tickEventHandler = CameraEntityDeltaMovementTickEventHandler.INSTANCE;
		var deltaMovementLerped = tickEventHandler.getDeltaMovementO().lerp(tickEventHandler.getDeltaMovement(), event.getPartialTick())
			.multiply(Config.CLIENT.getCameraConfig().getCameraDragMultipliers())
			.yRot(event.getCamera().yRot() * Mth.DEG_TO_RAD)
			.xRot(event.getCamera().xRot() * Mth.DEG_TO_RAD);
		var drag = new Vec3(-deltaMovementLerped.x, -deltaMovementLerped.y, deltaMovementLerped.z);
		event.setResult(event.getResult().add(drag));
	}
}
