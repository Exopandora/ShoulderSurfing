package com.github.exopandora.shouldersurfing.api.client.event;

import com.github.exopandora.shouldersurfing.api.event.CancellableEvent;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * This event can be used to implement custom camera drag.
 *
 * @since 5.1.0
 */
public class ComputeCameraDragEvent extends CancellableEvent {
	private final Camera camera;
	private final Entity cameraEntity;
	private final float partialTick;
	private Vec3 result = Vec3.ZERO;
	
	public ComputeCameraDragEvent(Camera camera, Entity cameraEntity, float partialTick) {
		this.camera = camera;
		this.cameraEntity = cameraEntity;
		this.partialTick = partialTick;
	}
	
	public Camera getCamera() {
		return this.camera;
	}
	
	public Entity getCameraEntity() {
		return this.cameraEntity;
	}
	
	public float getPartialTick() {
		return this.partialTick;
	}
	
	public Vec3 getResult() {
		return this.result;
	}
	
	public void setResult(Vec3 result) {
		this.result = result;
	}
}
