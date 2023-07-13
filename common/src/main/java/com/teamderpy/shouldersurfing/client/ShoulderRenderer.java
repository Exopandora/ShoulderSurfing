package com.teamderpy.shouldersurfing.client;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.math.Vec2f;
import com.teamderpy.shouldersurfing.mixins.CameraAccessor;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ShoulderRenderer
{
	private static final ShoulderRenderer INSTANCE = new ShoulderRenderer();
	private static final Vector3f VECTOR_NEGATIVE_Y = new Vector3f(0, -1, 0);
	private double cameraDistance;
	private Vec2f lastTranslation = Vec2f.ZERO;
	private Vec2f translation = Vec2f.ZERO;
	private Vec2f projected;
	
	public void offsetCrosshair(PoseStack poseStack, Window window, float partialTicks)
	{
		if(this.projected != null)
		{
			Vec2f scaledDimensions = new Vec2f(window.getGuiScaledWidth(), window.getGuiScaledHeight());
			Vec2f dimensions = new Vec2f(window.getScreenWidth(), window.getScreenHeight());
			Vec2f scale = scaledDimensions.divide(dimensions);
			Vec2f center = dimensions.divide(2); // In actual monitor pixels
			Vec2f projectedOffset = this.projected.subtract(center).scale(scale);
			Vec2f interpolated = projectedOffset.subtract(this.lastTranslation).scale(partialTicks);
			this.translation = this.lastTranslation.add(interpolated);
		}
		
		if(Config.CLIENT.getCrosshairType().isDynamic() && ShoulderInstance.getInstance().doShoulderSurfing())
		{
			poseStack.pushPose();
			poseStack.last().pose().translate(new Vector3f(this.translation.getX(), -this.translation.getY(), 0F));
			this.lastTranslation = this.translation;
		}
		else
		{
			this.lastTranslation = Vec2f.ZERO;
		}
	}
	
	public void clearCrosshairOffset(PoseStack poseStack)
	{
		if(Config.CLIENT.getCrosshairType().isDynamic() && ShoulderInstance.getInstance().doShoulderSurfing() && !Vec2f.ZERO.equals(this.lastTranslation))
		{
			poseStack.popPose();
		}
	}
	
	public void offsetCamera(Camera camera, Level level, double partialTick)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && level != null)
		{
			ShoulderInstance instance = ShoulderInstance.getInstance();
			
			if(Config.CLIENT.doCenterCameraWhenClimbing() && camera.getEntity() instanceof LivingEntity living && living.onClimbable())
			{
				instance.setTargetOffsetX(0);
			}
			else if(camera.getLookVector().angle(VECTOR_NEGATIVE_Y) < Config.CLIENT.getCenterCameraWhenLookingDownAngle() * Mth.DEG_TO_RAD)
			{
				instance.setTargetOffsetX(0);
				instance.setTargetOffsetY(0);
			}
			else
			{
				instance.setTargetOffsetX(Config.CLIENT.getOffsetX());
				instance.setTargetOffsetY(Config.CLIENT.getOffsetY());
				instance.setTargetOffsetZ(Config.CLIENT.getOffsetZ());
			}
			
			CameraAccessor accessor = ((CameraAccessor) camera);
			double x = Mth.lerp(partialTick, camera.getEntity().xo, camera.getEntity().getX());
			double y = Mth.lerp(partialTick, camera.getEntity().yo, camera.getEntity().getY()) + Mth.lerp(partialTick, accessor.getEyeHeightOld(), accessor.getEyeHeight());
			double z = Mth.lerp(partialTick, camera.getEntity().zo, camera.getEntity().getZ());
			accessor.invokeSetPosition(x, y, z);
			double offsetX = Mth.lerp(partialTick, instance.getOffsetXOld(), instance.getOffsetX());
			double offsetY = Mth.lerp(partialTick, instance.getOffsetYOld(), instance.getOffsetY());
			double offsetZ = Mth.lerp(partialTick, instance.getOffsetZOld(), instance.getOffsetZ());
			Vec3 offset = new Vec3(-offsetZ, offsetY, offsetX);
			this.cameraDistance = this.calcCameraDistance(camera, level, accessor.invokeGetMaxZoom(offset.length()));
			Vec3 scaled = offset.normalize().scale(this.cameraDistance);
			accessor.invokeMove(scaled.x, scaled.y, scaled.z);
		}
	}
	
	private double calcCameraDistance(Camera camera, Level level, double distance)
	{
		Vec3 cameraPos = camera.getPosition();
		Vec3 cameraOffset = ShoulderHelper.calcCameraOffset(camera, distance);
		
		for(int i = 0; i < 8; i++)
		{
			Vec3 offset = new Vec3(i & 1, i >> 1 & 1, i >> 2 & 1)
				.scale(2)
				.subtract(1, 1, 1)
				.scale(0.075)
				.yRot(-camera.getYRot() * Mth.DEG_TO_RAD);
			Vec3 from = cameraPos.add(offset);
			Vec3 to = from.add(cameraOffset);
			ClipContext context = new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, camera.getEntity());
			HitResult hitResult = level.clip(context);
			
			if(hitResult != null)
			{
				double newDistance = hitResult.getLocation().distanceTo(cameraPos);
				
				if(newDistance < distance)
				{
					distance = newDistance - 0.2;
				}
			}
		}
		
		return distance;
	}
	
	@SuppressWarnings("resource")
	public void updateDynamicRaytrace(Camera camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float partialTick)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing())
		{
			Minecraft minecraft = Minecraft.getInstance();
			MultiPlayerGameMode gameMode = minecraft.gameMode;
			HitResult hitResult = ShoulderHelper.traceBlocksAndEntities(camera, gameMode, this.getPlayerReach(), ClipContext.Fluid.NONE, partialTick, true, false);
			Vec3 position = hitResult.getLocation().subtract(camera.getPosition());
			this.projected = this.project2D(position, modelViewMatrix, projectionMatrix);
		}
	}
	
	@Nullable
	private Vec2f project2D(Vec3 position, Matrix4f modelView, Matrix4f projection)
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
	
	public boolean skipEntityRendering()
	{
		return Config.CLIENT.keepCameraOutOfHead() && this.cameraDistance < Minecraft.getInstance().getCameraEntity().getBbWidth() * 0.75F && ShoulderInstance.getInstance().doShoulderSurfing();
	}
	
	public double getPlayerReach()
	{
		return Config.CLIENT.useCustomRaytraceDistance() ? Config.CLIENT.getCustomRaytraceDistance() : 0;
	}
	
	public double getCameraDistance()
	{
		return this.cameraDistance;
	}
	
	public static ShoulderRenderer getInstance()
	{
		return INSTANCE;
	}
}
