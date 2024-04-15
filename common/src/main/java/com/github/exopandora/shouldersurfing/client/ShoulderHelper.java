package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.math.Vec2f;
import com.mojang.blaze3d.platform.Window;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ShoulderHelper
{
	public static ShoulderLook shoulderSurfingLook(Camera camera, Entity entity, float partialTick, double distanceSq)
	{
		Vec3 cameraOffset = camera.getPosition().subtract(entity.getEyePosition(partialTick));
		Vec3 headOffset = ShoulderHelper.calcRayTraceHeadOffset(camera, cameraOffset);
		Vec3 cameraPos = camera.getPosition();
		Vec3 viewVector = new Vec3(camera.getLookVector());
		
		if(headOffset.lengthSqr() < distanceSq)
		{
			distanceSq -= headOffset.lengthSqr();
		}
		
		double distance = Math.sqrt(distanceSq) + cameraOffset.distanceTo(headOffset);
		Vec3 traceEnd = cameraPos.add(viewVector.scale(distance));
		return new ShoulderLook(cameraPos, traceEnd, headOffset);
	}
	
	public static Vec3 calcRayTraceHeadOffset(Camera camera, Vec3 cameraOffset)
	{
		Vec3 lookVector = new Vec3(camera.getLookVector());
		return ShoulderHelper.calcPlaneWithLineIntersection(Vec3.ZERO, lookVector, cameraOffset, lookVector);
	}
	
	public static Vec3 calcPlaneWithLineIntersection(Vec3 planePoint, Vec3 planeNormal, Vec3 linePoint, Vec3 lineNormal)
	{
		double distance = (planeNormal.dot(planePoint) - planeNormal.dot(linePoint)) / planeNormal.dot(lineNormal);
		return linePoint.add(lineNormal.scale(distance));
	}
	
	public static @Nullable Vec2f project2D(Vec3 position, Matrix4f modelView, Matrix4f projection)
	{
		Window window = Minecraft.getInstance().getWindow();
		int screenWidth = window.getScreenWidth();
		int screenHeight = window.getScreenHeight();
		
		if(screenWidth == 0 || screenHeight == 0)
		{
			return null;
		}
		
		Vector4f vec = new Vector4f((float) position.x(), (float) position.y(), (float) position.z(), 1.0F);
		vec.transform(modelView);
		vec.transform(projection);
		
		if(vec.w() == 0.0F)
		{
			return null;
		}
		
		float w = (1.0F / vec.w()) * 0.5F;
		float x = (vec.x() * w + 0.5F) * screenWidth;
		float y = (vec.y() * w + 0.5F) * screenHeight;
		float z = vec.z() * w + 0.5F;
		vec.set(x, y, z, w);
		
		if(Float.isInfinite(x) || Float.isInfinite(y) || Float.isNaN(x) || Float.isNaN(y))
		{
			return null;
		}
		
		return new Vec2f(x, y);
	}
	
	public static double angle(Vector3f a, Vector3f b)
	{
		return Math.acos(a.dot(b) / (length(a) * length(b)));
	}
	
	public static double length(Vector3f vec)
	{
		return Mth.sqrt(vec.x() * vec.x() + vec.y() * vec.y() + vec.z() * vec.z());
	}
	
	public record ShoulderLook(Vec3 cameraPos, Vec3 traceEndPos, Vec3 headOffset) {}
}
