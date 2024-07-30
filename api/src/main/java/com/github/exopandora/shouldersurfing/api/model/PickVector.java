package com.github.exopandora.shouldersurfing.api.model;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.NotNull;

public enum PickVector
{
	PLAYER((camera, entity, partialTick) -> entityViewVector(entity, partialTick)),
	CAMERA((camera, entity, partialTick) -> new Vector3d(camera.getLookVector()));
	
	private final IPickVectorFunction pickVectorFunction;
	
	private PickVector(IPickVectorFunction pickVectorFunction)
	{
		this.pickVectorFunction = pickVectorFunction;
	}
	
	public Vector3d calc(ActiveRenderInfo camera, Entity entity, float partialTick)
	{
		return this.pickVectorFunction.apply(camera, entity, partialTick);
	}
	
	private interface IPickVectorFunction
	{
		Vector3d apply(ActiveRenderInfo camera, Entity entity, float partialTick);
	}
	
	private static final float DEG_TO_RAD = (float) (Math.PI / 180F);
	
	private static @NotNull Vector3d entityViewVector(Entity entity, float partialTick)
	{
		float xRot = partialTick == 1.0F ? entity.xRot : MathHelper.rotLerp(partialTick, entity.xRotO, entity.xRot);
		float yRot = partialTick == 1.0F ? entity.yRot : MathHelper.rotLerp(partialTick, entity.yRotO, entity.yRot);
		float xRotDeg = xRot * DEG_TO_RAD;
		float yRotDegNegated = -yRot * DEG_TO_RAD;
		float a = MathHelper.cos(yRotDegNegated);
		float b = MathHelper.sin(yRotDegNegated);
		float c = MathHelper.cos(xRotDeg);
		float d = MathHelper.sin(xRotDeg);
		return new Vector3d(b * c, -d, a * c);
	}
}
