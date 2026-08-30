package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeCameraSwayEvent;
import com.github.exopandora.shouldersurfing.api.client.event.TickEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraSwayEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.TickEventHandler;
import com.github.exopandora.shouldersurfing.api.util.EntityHelper;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public enum ComputeCameraSwayEventHandlerImpl implements ComputeCameraSwayEventHandler, TickEventHandler {
	INSTANCE;
	
	private Vec3 deltaMovement = Vec3.ZERO;
	private Vec3 deltaMovementO = Vec3.ZERO;
	
	@Override
	public void handle(TickEvent event) {
		var cameraEntity = Minecraft.getInstance().getCameraEntity();
		if (cameraEntity != null) {
			this.deltaMovementO = this.deltaMovement;
			this.deltaMovement = EntityHelper.getDeltaMovementWithoutGravity(cameraEntity);
		} else {
			this.deltaMovement = Vec3.ZERO;
			this.deltaMovementO = Vec3.ZERO;
		}
	}
	
	@Override
	public void handle(ComputeCameraSwayEvent event) {
		var camera = IShoulderSurfing.getInstance().getCamera();
		var deltaMovementLerped = this.deltaMovementO.lerp(this.deltaMovement, event.getPartialTick())
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
