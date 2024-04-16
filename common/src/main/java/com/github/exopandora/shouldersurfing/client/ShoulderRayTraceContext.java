package com.github.exopandora.shouldersurfing.client;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

public class ShoulderRayTraceContext
{
	private final Vector3d startPos;
	private final Vector3d endPos;
	
	private ShoulderRayTraceContext(Vector3d startPos, Vector3d endPos)
	{
		this.startPos = startPos;
		this.endPos = endPos;
	}
	
	public Vector3d startPos()
	{
		return this.startPos;
	}
	
	public Vector3d endPos()
	{
		return this.endPos;
	}
	
	public static ShoulderRayTraceContext from(ActiveRenderInfo camera, Entity entity, float partialTick, double distanceSq)
	{
		Vector3d eyePosition = entity.getEyePosition(partialTick);
		Vector3d cameraOffset = camera.getPosition().subtract(eyePosition);
		Vector3d rayTraceStartOffset = ShoulderHelper.calcRayTraceStartOffset(camera, cameraOffset);
		Vector3d cameraPos = camera.getPosition();
		Vector3d viewVector = new Vector3d(camera.getLookVector());
		
		if(rayTraceStartOffset.lengthSqr() < distanceSq)
		{
			distanceSq -= rayTraceStartOffset.lengthSqr();
		}
		
		double distance = Math.sqrt(distanceSq) + cameraOffset.distanceTo(rayTraceStartOffset);
		Vector3d traceEnd = cameraPos.add(viewVector.scale(distance));
		Vector3d traceStart = eyePosition.add(rayTraceStartOffset);
		return new ShoulderRayTraceContext(traceStart, traceEnd);
	}
}
