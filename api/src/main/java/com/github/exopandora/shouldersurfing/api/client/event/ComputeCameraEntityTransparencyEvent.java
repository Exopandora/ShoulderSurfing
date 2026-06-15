package com.github.exopandora.shouldersurfing.api.client.event;

import com.github.exopandora.shouldersurfing.api.event.CancellableEvent;
import net.minecraft.world.entity.Entity;

/**
 * This event can be used to implement custom camera entity transparency rules.
 *
 * @since 5.0.0
 */
public class ComputeCameraEntityTransparencyEvent extends CancellableEvent {
	private final Entity cameraEntity;
	private final float partialTick;
	private float result = 1.0F;
	
	public ComputeCameraEntityTransparencyEvent(Entity cameraEntity, float partialTick) {
		this.cameraEntity = cameraEntity;
		this.partialTick = partialTick;
	}
	
	public Entity getCameraEntity() {
		return this.cameraEntity;
	}
	
	public float getPartialTick() {
		return this.partialTick;
	}
	
	public float getResult() {
		return this.result;
	}
	
	public void setResult(float result) {
		this.result = result;
	}
}
