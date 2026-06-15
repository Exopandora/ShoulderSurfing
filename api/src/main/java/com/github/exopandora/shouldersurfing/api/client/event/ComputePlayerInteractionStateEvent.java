package com.github.exopandora.shouldersurfing.api.client.event;

import com.github.exopandora.shouldersurfing.api.event.CancellableEvent;
import net.minecraft.world.entity.LivingEntity;

/**
 * This event can be used to implement custom interaction state behavior.
 *
 * @since 5.0.0
 */
public class ComputePlayerInteractionStateEvent extends CancellableEvent {
	private final LivingEntity cameraEntity;
	private boolean result;
	
	public ComputePlayerInteractionStateEvent(LivingEntity cameraEntity) {
		this.cameraEntity = cameraEntity;
	}
	
	public LivingEntity getCameraEntity() {
		return this.cameraEntity;
	}
	
	public boolean getResult() {
		return this.result;
	}
	
	public void setResult(boolean result) {
		this.result = result;
	}
}
