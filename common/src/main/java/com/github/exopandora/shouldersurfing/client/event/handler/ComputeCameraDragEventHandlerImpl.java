package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.ComputeCameraDragEvent;
import com.github.exopandora.shouldersurfing.api.client.event.TickEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraDragEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.TickEventHandler;
import com.github.exopandora.shouldersurfing.api.util.EntityHelper;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public enum ComputeCameraDragEventHandlerImpl implements ComputeCameraDragEventHandler, TickEventHandler {
	INSTANCE;
	
	private Vec3 deltaMovement = Vec3.ZERO;
	private Vec3 deltaMovementO = Vec3.ZERO;
	
	@Override
	public void handle(TickEvent event) {
		var cameraEntity = Minecraft.getInstance().cameraEntity;
		if (cameraEntity != null) {
			this.deltaMovementO = this.deltaMovement;
			this.deltaMovement = EntityHelper.getDeltaMovementWithoutGravity(cameraEntity);
		} else {
			this.deltaMovement = Vec3.ZERO;
			this.deltaMovementO = Vec3.ZERO;
		}
	}
	
	@Override
	public void handle(ComputeCameraDragEvent event) {
		var deltaMovementLerped = this.deltaMovementO.lerp(this.deltaMovement, event.getPartialTick())
			.multiply(Config.CLIENT.getCameraConfig().getCameraDragMultipliers())
			.yRot(event.getCamera().getYRot() * Mth.DEG_TO_RAD)
			.xRot(event.getCamera().getXRot() * Mth.DEG_TO_RAD);
		var drag = new Vec3(-deltaMovementLerped.x, -deltaMovementLerped.y, deltaMovementLerped.z);
		event.setResult(event.getResult().add(drag));
	}
}
