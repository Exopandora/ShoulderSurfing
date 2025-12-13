package com.github.exopandora.shouldersurfing.api.model;

import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public enum PickVector
{
	PLAYER((camera, entity, partialTick) -> entityViewVector(entity, partialTick)),
	CAMERA((camera, entity, partialTick) -> new Vec3(camera.forwardVector()));
	
	private final IPickVectorFunction pickVectorFunction;
	
	private PickVector(IPickVectorFunction pickVectorFunction)
	{
		this.pickVectorFunction = pickVectorFunction;
	}
	
	public Vec3 calc(Camera camera, Entity entity, float partialTick)
	{
		return this.pickVectorFunction.apply(camera, entity, partialTick);
	}
	
	private interface IPickVectorFunction
	{
		Vec3 apply(Camera camera, Entity entity, float partialTick);
	}
	
	private static @NotNull Vec3 entityViewVector(Entity entity, float partialTick)
	{
		float xRot = partialTick == 1.0F ? entity.getXRot() : Mth.rotLerp(partialTick, entity.xRotO, entity.getXRot());
		float yRot = partialTick == 1.0F ? entity.getYRot() : Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
		float xRotDeg = xRot * Mth.DEG_TO_RAD;
		float yRotDegNegated = -yRot * Mth.DEG_TO_RAD;
		float a = Mth.cos(yRotDegNegated);
		float b = Mth.sin(yRotDegNegated);
		float c = Mth.cos(xRotDeg);
		float d = Mth.sin(xRotDeg);
		return new Vec3(b * c, -d, a * c);
	}
}
