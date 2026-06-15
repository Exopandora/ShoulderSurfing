package com.github.exopandora.shouldersurfing.api.client.event;

import com.github.exopandora.shouldersurfing.api.event.CancellableEvent;
import net.minecraft.world.entity.Entity;

/**
 * This event can be used to force vanilla movement inputs.
 * This can be useful when movement is computed server side via xxa, yya and zza fields.
 *
 * @since 5.0.0
 */
public class ForceVanillaPlayerInputEvent extends CancellableEvent {
	private final Entity cameraEntity;
	private boolean result;
	
	public ForceVanillaPlayerInputEvent(Entity cameraEntity) {
		this.cameraEntity = cameraEntity;
	}
	
	public Entity getCameraEntity() {
		return this.cameraEntity;
	}
	
	public boolean getResult() {
		return this.result;
	}
	
	public void setResult(boolean result) {
		this.result = result;
	}
}
