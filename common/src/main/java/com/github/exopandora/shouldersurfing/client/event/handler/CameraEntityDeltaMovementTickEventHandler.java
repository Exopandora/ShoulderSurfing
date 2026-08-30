package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.TickEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.TickEventHandler;
import com.github.exopandora.shouldersurfing.api.util.EntityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public enum CameraEntityDeltaMovementTickEventHandler implements TickEventHandler {
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
	
	public Vec3 getDeltaMovement() {
		return this.deltaMovement;
	}
	
	public Vec3 getDeltaMovementO() {
		return this.deltaMovementO;
	}
}
