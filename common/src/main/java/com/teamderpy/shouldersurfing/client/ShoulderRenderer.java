package com.teamderpy.shouldersurfing.client;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.math.Vec2f;
import com.teamderpy.shouldersurfing.mixins.ActiveRenderInfoAccessor;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.world.World;

public class ShoulderRenderer
{
	private static final ShoulderRenderer INSTANCE = new ShoulderRenderer();
	private double cameraDistance;
	private Vec2f lastTranslation = Vec2f.ZERO;
	private Vec2f translation = Vec2f.ZERO;
	private Vec2f projected;
	
	public void offsetCrosshair(MatrixStack poseStack, MainWindow window, float partialTicks)
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
	
	public void clearCrosshairOffset(MatrixStack poseStack)
	{
		if(Config.CLIENT.getCrosshairType().isDynamic() && ShoulderInstance.getInstance().doShoulderSurfing() && !Vec2f.ZERO.equals(this.lastTranslation))
		{
			poseStack.popPose();
		}
	}
	
	public void offsetCamera(ActiveRenderInfo camera, World level, double partialTick)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && level != null)
		{
			ShoulderInstance instance = ShoulderInstance.getInstance();
			
			if(Config.CLIENT.doCenterCameraWhenClimbing() && camera.getEntity() instanceof LivingEntity && ((LivingEntity) camera.getEntity()).onClimbable())
			{
				instance.setTargetOffsetX(0);
			}
			else
			{
				instance.setTargetOffsetX(Config.CLIENT.getOffsetX());
				instance.setTargetOffsetY(Config.CLIENT.getOffsetY());
				instance.setTargetOffsetZ(Config.CLIENT.getOffsetZ());
			}
			
			ActiveRenderInfoAccessor accessor = ((ActiveRenderInfoAccessor) camera);
			double x = MathHelper.lerp(partialTick, camera.getEntity().xo, camera.getEntity().getX());
			double y = MathHelper.lerp(partialTick, camera.getEntity().yo, camera.getEntity().getY()) + MathHelper.lerp(partialTick, accessor.getEyeHeightOld(), accessor.getEyeHeight());
			double z = MathHelper.lerp(partialTick, camera.getEntity().zo, camera.getEntity().getZ());
			accessor.invokeSetPosition(x, y, z);
			double offsetX = MathHelper.lerp(partialTick, instance.getOffsetXOld(), instance.getOffsetX());
			double offsetY = MathHelper.lerp(partialTick, instance.getOffsetYOld(), instance.getOffsetY());
			double offsetZ = MathHelper.lerp(partialTick, instance.getOffsetZOld(), instance.getOffsetZ());
			Vector3d offset = new Vector3d(-offsetZ, offsetY, offsetX);
			this.cameraDistance = this.calcCameraDistance(camera, level, accessor.invokeGetMaxZoom(offset.length()));
			Vector3d scaled = offset.normalize().scale(this.cameraDistance);
			accessor.invokeMove(scaled.x, scaled.y, scaled.z);
		}
	}
	
	private double calcCameraDistance(ActiveRenderInfo camera, World level, double distance)
	{
		Vector3d cameraPos = camera.getPosition();
		Vector3d cameraOffset = ShoulderHelper.calcCameraOffset(camera, distance);
		
		for(int i = 0; i < 8; i++)
		{
			Vector3d offset = new Vector3d(i & 1, i >> 1 & 1, i >> 2 & 1)
				.scale(2)
				.subtract(1, 1, 1)
				.scale(0.075);
			Vector3d from = cameraPos.add(offset);
			Vector3d to = from.add(cameraOffset);
			RayTraceContext context = new RayTraceContext(from, to, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, camera.getEntity());
			RayTraceResult hitResult = level.clip(context);
			
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
	
	public void updateDynamicRaytrace(ActiveRenderInfo camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float partialTick)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing())
		{
			Minecraft minecraft = Minecraft.getInstance();
			PlayerController gameMode = minecraft.gameMode;
			RayTraceResult hitResult = ShoulderHelper.traceBlocksAndEntities(camera, gameMode, this.getPlayerReach(), RayTraceContext.FluidMode.NONE, partialTick, true, false);
			Vector3d position = hitResult.getLocation().subtract(camera.getPosition());
			this.projected = this.project2D(position, modelViewMatrix, projectionMatrix);
		}
	}
	
	@Nullable
	private Vec2f project2D(Vector3d position, Matrix4f modelView, Matrix4f projection)
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
