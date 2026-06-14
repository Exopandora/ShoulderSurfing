package com.github.exopandora.shouldersurfing.api.client.event;

import com.github.exopandora.shouldersurfing.api.event.CancellableEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * This event can be used to implement custom boat riding state behavior.
 *
 * @since 5.0.0
 */
public class ComputePlayerRideBoatStateEvent extends CancellableEvent {
	private final LivingEntity cameraEntity;
	private final Entity vehicle;
	private boolean result;
	
	public ComputePlayerRideBoatStateEvent(LivingEntity cameraEntity, Entity vehicle) {
		this.cameraEntity = cameraEntity;
		this.vehicle = vehicle;
	}
	
	public LivingEntity getCameraEntity() {
		return this.cameraEntity;
	}
	
	public Entity getVehicle() {
		return this.vehicle;
	}
	
	public boolean getResult() {
		return this.result;
	}
	
	public void setResult(boolean result) {
		this.result = result;
	}
}
