package com.github.exopandora.shouldersurfing.api.client.event;

import com.github.exopandora.shouldersurfing.api.event.CancellableEvent;
import com.github.exopandora.shouldersurfing.api.math.Vec2f;
import net.minecraft.client.player.LocalPlayer;

/**
 * This event can be used to change the camera rotation of the Shoulder Surfing perspective.
 *
 * @since 5.0.0
 */
public class SetupCameraRotationEvent extends CancellableEvent {
	private final LocalPlayer player;
	private Vec2f result;
	private final Vec2f cameraRotO;
	private final Vec2f dRot;
	private final Vec2f dRotScaled;
	
	public SetupCameraRotationEvent(LocalPlayer player, Vec2f cameraRot, Vec2f cameraRotO, Vec2f dRot, Vec2f dRotScaled) {
		this.player = player;
		this.dRot = dRot;
		this.dRotScaled = dRotScaled;
		this.result = cameraRot;
		this.cameraRotO = cameraRotO;
	}
	
	public LocalPlayer getPlayer() {
		return this.player;
	}
	
	public Vec2f getDeltaRot() {
		return this.dRot;
	}
	
	public Vec2f getDeltaRotScaled() {
		return this.dRotScaled;
	}
	
	public Vec2f getCameraRotO() {
		return this.cameraRotO;
	}
	
	public void setResult(Vec2f rotation) {
		this.result = rotation;
	}
	
	public Vec2f getResult() {
		return this.result;
	}
}
