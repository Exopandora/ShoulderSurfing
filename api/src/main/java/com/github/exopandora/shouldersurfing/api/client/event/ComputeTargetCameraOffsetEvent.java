package com.github.exopandora.shouldersurfing.api.client.event;

import com.github.exopandora.shouldersurfing.api.event.CancellableEvent;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

/**
 * This event can be used to implement custom target camera offsets.
 *
 * @since 5.0.0
 */
public class ComputeTargetCameraOffsetEvent extends CancellableEvent {
	private final Vec3 defaultOffset;
	private final Camera camera;
	private final Entity cameraEntity;
	private final BlockGetter level;
	private Vec3 result;
	
	public ComputeTargetCameraOffsetEvent(Vec3 defaultOffset, Camera camera, Entity cameraEntity, BlockGetter level) {
		this.defaultOffset = defaultOffset;
		this.camera = camera;
		this.cameraEntity = cameraEntity;
		this.level = level;
		this.result = defaultOffset;
	}
	
	public Vec3 getDefaultOffset() {
		return this.defaultOffset;
	}
	
	public Camera getCamera() {
		return this.camera;
	}
	
	public Entity getCameraEntity() {
		return this.cameraEntity;
	}
	
	public BlockGetter getLevel() {
		return this.level;
	}
	
	public Vec3 getResult() {
		return this.result;
	}
	
	public void setResult(Vec3 result) {
		this.result = result;
	}
}
