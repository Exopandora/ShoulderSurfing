package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.math.Vec2f;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import org.jetbrains.annotations.Nullable;

public class ShoulderHelper
{
	public static final float DEG_TO_RAD = (float) (Math.PI / 180F);
	public static final float RAD_TO_DEG = (float) (180F / Math.PI);
	
	public static Vector3d calcRayTraceStartOffset(ActiveRenderInfo camera, Vector3d cameraOffset)
	{
		Vector3d lookVector = new Vector3d(camera.getLookVector());
		double distance = (lookVector.dot(Vector3d.ZERO) - lookVector.dot(cameraOffset)) / lookVector.dot(lookVector);
		return cameraOffset.add(lookVector.scale(distance));
	}
	
	public static @Nullable Vec2f project2D(Vector3d position, Matrix4f modelView, Matrix4f projection)
	{
		MainWindow window = Minecraft.getInstance().getWindow();
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
		return MathHelper.sqrt(vec.x() * vec.x() + vec.y() * vec.y() + vec.z() * vec.z());
	}
}
