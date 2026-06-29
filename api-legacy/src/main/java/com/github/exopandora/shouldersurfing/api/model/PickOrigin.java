package com.github.exopandora.shouldersurfing.api.model;

import net.minecraft.world.phys.Vec3;

public enum PickOrigin {
	PLAYER((cameraPosition, eyePosition, rayTraceStartOffset) -> eyePosition.add(rayTraceStartOffset)),
	CAMERA((cameraPosition, eyePosition, rayTraceStartOffset) -> cameraPosition);
	
	private final IPickOriginFunction pickOriginFunction;
	
	PickOrigin(IPickOriginFunction pickOriginFunction) {
		this.pickOriginFunction = pickOriginFunction;
	}
	
	public Vec3 calc(Vec3 cameraPosition, Vec3 eyePosition, Vec3 rayTraceStartOffset) {
		return this.pickOriginFunction.apply(cameraPosition, eyePosition, rayTraceStartOffset);
	}
	
	private interface IPickOriginFunction {
		Vec3 apply(Vec3 cameraPosition, Vec3 eyePosition, Vec3 rayTraceStartOffset);
	}
	
	public com.github.exopandora.shouldersurfing.api.client.world.phys.PickOrigin toNewApi() {
		return switch (this) {
			case PLAYER -> com.github.exopandora.shouldersurfing.api.client.world.phys.PickOrigin.PLAYER;
			case CAMERA -> com.github.exopandora.shouldersurfing.api.client.world.phys.PickOrigin.CAMERA;
		};
	}
	
	public static PickOrigin fromNewApi(com.github.exopandora.shouldersurfing.api.client.world.phys.PickOrigin pickOrigin) {
		return switch (pickOrigin) {
			case PLAYER -> PLAYER;
			case CAMERA -> CAMERA;
		};
	}
}
