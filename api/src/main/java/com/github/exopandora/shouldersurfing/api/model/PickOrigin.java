package com.github.exopandora.shouldersurfing.api.model;

import net.minecraft.util.math.vector.Vector3d;

public enum PickOrigin
{
	PLAYER((cameraPosition, eyePosition, rayTraceStartOffset) -> eyePosition.add(rayTraceStartOffset)),
	CAMERA((cameraPosition, eyePosition, rayTraceStartOffset) -> cameraPosition);
	
	private final IPickOriginFunction pickOriginFunction;
	
	private PickOrigin(IPickOriginFunction pickOriginFunction)
	{
		this.pickOriginFunction = pickOriginFunction;
	}
	
	public Vector3d calc(Vector3d cameraPosition, Vector3d eyePosition, Vector3d rayTraceStartOffset)
	{
		return this.pickOriginFunction.apply(cameraPosition, eyePosition, rayTraceStartOffset);
	}
	
	private interface IPickOriginFunction
	{
		Vector3d apply(Vector3d cameraPosition, Vector3d eyePosition, Vector3d rayTraceStartOffset);
	}
}
