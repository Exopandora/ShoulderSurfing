package com.github.exopandora.shouldersurfing.api.client.event;

import com.github.exopandora.shouldersurfing.api.event.CancellableEvent;
import com.github.exopandora.shouldersurfing.api.math.Vec2f;
import net.minecraft.world.entity.Entity;

/**
 * This event can be used to implement custom camera sway.
 *
 * @since 5.1.0
 */
public class ComputeCameraSwayEvent extends CancellableEvent {
	private final Entity cameraEntity;
	private final float partialTick;
	private Vec2f result = Vec2f.ZERO;
	
	public ComputeCameraSwayEvent(Entity cameraEntity, float partialTick) {
		this.cameraEntity = cameraEntity;
		this.partialTick = partialTick;
	}
	
	public Entity getCameraEntity() {
		return this.cameraEntity;
	}
	
	public float getPartialTick() {
		return this.partialTick;
	}
	
	public Vec2f getResult() {
		return this.result;
	}
	
	public void setResult(Vec2f result) {
		this.result = result;
	}
}
