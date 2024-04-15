package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.math.Vec2f;
import com.mojang.blaze3d.platform.Window;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ShoulderHelper
{
	public static Vec3 calcRayTraceStartOffset(Camera camera, Vec3 cameraOffset)
	{
		Vec3 lookVector = new Vec3(camera.getLookVector());
		double distance = (lookVector.dot(Vec3.ZERO) - lookVector.dot(cameraOffset)) / lookVector.dot(lookVector);
		return cameraOffset.add(lookVector.scale(distance));
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
}
