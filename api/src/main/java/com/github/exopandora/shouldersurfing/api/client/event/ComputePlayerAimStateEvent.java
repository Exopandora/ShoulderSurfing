package com.github.exopandora.shouldersurfing.api.client.event;

import com.github.exopandora.shouldersurfing.api.event.CancellableEvent;
import net.minecraft.world.entity.LivingEntity;

/**
 * This event can be used to implement custom aim state behavior.
 *
 * @since 5.0.0
 */
public class ComputePlayerAimStateEvent extends CancellableEvent {
	private final LivingEntity entity;
	private boolean result;
	
	public ComputePlayerAimStateEvent(LivingEntity entity) {
		this.entity = entity;
	}
	
	public LivingEntity getEntity() {
		return this.entity;
	}
	
	public boolean getResult() {
		return this.result;
	}
	
	public void setResult(boolean result) {
		this.result = result;
	}
}
