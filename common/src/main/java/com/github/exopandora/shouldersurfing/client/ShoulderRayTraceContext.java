package com.github.exopandora.shouldersurfing.client;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public record ShoulderRayTraceContext(Vec3 startPos, Vec3 endPos)
{
	public static ShoulderRayTraceContext from(Camera camera, Entity entity, float partialTick, double distanceSq)
	{
		Vec3 eyePosition = entity.getEyePosition(partialTick);
		Vec3 cameraOffset = camera.getPosition().subtract(eyePosition);
		Vec3 rayTraceStartOffset = ShoulderHelper.calcRayTraceStartOffset(camera, cameraOffset);
		Vec3 cameraPos = camera.getPosition();
		Vec3 viewVector = new Vec3(camera.getLookVector());
		
		if(rayTraceStartOffset.lengthSqr() < distanceSq)
		{
			distanceSq -= rayTraceStartOffset.lengthSqr();
		}
		
		double distance = Math.sqrt(distanceSq) + cameraOffset.distanceTo(rayTraceStartOffset);
		Vec3 traceEnd = cameraPos.add(viewVector.scale(distance));
		Vec3 traceStart = eyePosition.add(rayTraceStartOffset);
		return new ShoulderRayTraceContext(traceStart, traceEnd);
	}
}
