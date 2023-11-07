package com.teamderpy.shouldersurfing.client;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.math.Vec2f;
import com.teamderpy.shouldersurfing.mixins.ActiveRenderInfoAccessor;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.world.World;

public class ShoulderRenderer
{
	private static final ShoulderRenderer INSTANCE = new ShoulderRenderer();
	private static final Vector3f VECTOR_NEGATIVE_Y = new Vector3f(0, -1, 0);
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
	
	public void offsetCamera(ActiveRenderInfo camera, World level, float partialTick)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && level != null)
		{
			ShoulderInstance instance = ShoulderInstance.getInstance();
			double targetXOffset = Config.CLIENT.getOffsetX();
			double targetYOffset = Config.CLIENT.getOffsetY();
			double targetZOffset = Config.CLIENT.getOffsetZ();
			
			if(camera.getEntity().isPassenger())
			{
				targetXOffset += Config.CLIENT.getOffsetX() * (Config.CLIENT.getPassengerOffsetXMultiplier() - 1);
				targetYOffset += Config.CLIENT.getOffsetY() * (Config.CLIENT.getPassengerOffsetYMultiplier() - 1);
				targetZOffset += Config.CLIENT.getOffsetZ() * (Config.CLIENT.getPassengerOffsetZMultiplier() - 1);
			}
			
			if(camera.getEntity().isSprinting())
			{
				targetXOffset += Config.CLIENT.getOffsetX() * (Config.CLIENT.getSprintOffsetXMultiplier() - 1);
				targetYOffset += Config.CLIENT.getOffsetY() * (Config.CLIENT.getSprintOffsetYMultiplier() - 1);
				targetZOffset += Config.CLIENT.getOffsetZ() * (Config.CLIENT.getSprintOffsetZMultiplier() - 1);
			}
			
			if(Config.CLIENT.doCenterCameraWhenClimbing() && camera.getEntity() instanceof LivingEntity && ((LivingEntity) camera.getEntity()).onClimbable())
			{
				targetXOffset = 0;
			}
			
			if(ShoulderHelper.angle(camera.getLookVector(), VECTOR_NEGATIVE_Y) < Config.CLIENT.getCenterCameraWhenLookingDownAngle() * ShoulderHelper.DEG_TO_RAD)
			{
				targetXOffset = 0;
				targetYOffset = 0;
			}
			
			if(Config.CLIENT.doDynamicallyAdjustOffsets())
			{
				ActiveRenderInfoAccessor accessor = (ActiveRenderInfoAccessor) camera;
				Vector3d localCameraOffset = new Vector3d(targetXOffset, targetYOffset, targetZOffset);
				Vector3d worldCameraOffset = new Vector3d(camera.getUpVector()).scale(targetYOffset)
					.add(new Vector3d(accessor.getLeft()).scale(targetXOffset))
					.add(new Vector3d(camera.getLookVector()).scale(-targetZOffset))
					.normalize()
					.scale(localCameraOffset.length());
				Vector3d worldXYOffset = ShoulderHelper.calcRayTraceHeadOffset(camera, worldCameraOffset);
				Vector3d eyePosition = camera.getEntity().getEyePosition(partialTick);
				double absOffsetX = Math.abs(targetXOffset);
				double absOffsetY = Math.abs(targetYOffset);
				double absOffsetZ = Math.abs(targetZOffset);
				double targetX = absOffsetX;
				double targetY = absOffsetY;
				double clearance = Minecraft.getInstance().getCameraEntity().getBbWidth() / 3.0D;
				
				for(double dz = 0; dz <= absOffsetZ; dz += 0.03125D)
				{
					double scale = dz / absOffsetZ;
					Vector3d from = eyePosition.add(worldCameraOffset.scale(scale));
					Vector3d to = eyePosition.add(worldXYOffset).add(new Vector3d(camera.getLookVector()).scale(-dz));
					RayTraceContext context = new RayTraceContext(from, to, BlockMode.VISUAL, FluidMode.NONE, camera.getEntity());
					BlockRayTraceResult hitResult = level.clip(context);
					
					if(hitResult.getType() != Type.MISS)
					{
						double distance = hitResult.getLocation().distanceTo(from);
						double newTargetX = Math.max(distance + absOffsetX * scale - clearance, 0);
						
						if(newTargetX < targetX)
						{
							targetX = newTargetX;
						}
						
						double newTargetY = Math.max(distance + absOffsetY * scale - clearance, 0);
						
						if(newTargetY < targetY)
						{
							targetY = newTargetY;
						}
					}
				}
				
				targetXOffset = Math.signum(Config.CLIENT.getOffsetX()) * targetX;
				targetYOffset = Math.signum(Config.CLIENT.getOffsetY()) * targetY;
			}
			
			instance.setTargetOffsetX(targetXOffset);
			instance.setTargetOffsetY(targetYOffset);
			instance.setTargetOffsetZ(targetZOffset);
			
			ActiveRenderInfoAccessor accessor = ((ActiveRenderInfoAccessor) camera);
			double x = MathHelper.lerp(partialTick, camera.getEntity().xo, camera.getEntity().getX());
			double y = MathHelper.lerp(partialTick, camera.getEntity().yo, camera.getEntity().getY()) + MathHelper.lerp(partialTick, accessor.getEyeHeightOld(), accessor.getEyeHeight());
			double z = MathHelper.lerp(partialTick, camera.getEntity().zo, camera.getEntity().getZ());
			accessor.invokeSetPosition(x, y, z);
			double offsetX = MathHelper.lerp(partialTick, instance.getOffsetXOld(), instance.getOffsetX());
			double offsetY = MathHelper.lerp(partialTick, instance.getOffsetYOld(), instance.getOffsetY());
			double offsetZ = MathHelper.lerp(partialTick, instance.getOffsetZOld(), instance.getOffsetZ());
			Vector3d offset = new Vector3d(-offsetZ, offsetY, offsetX);
			this.cameraDistance = this.calcCameraDistance(camera, level, accessor.invokeGetMaxZoom(offset.length()), partialTick);
			Vector3d scaled = offset.normalize().scale(this.cameraDistance);
			accessor.invokeMove(scaled.x, scaled.y, scaled.z);
		}
	}
	
	private double calcCameraDistance(ActiveRenderInfo camera, World level, double distance, float partialTick)
	{
		Vector3d cameraPos = camera.getPosition();
		Vector3d cameraOffset = ShoulderHelper.calcCameraOffset(camera, distance, partialTick);
		
		for(int i = 0; i < 8; i++)
		{
			Vector3d offset = new Vector3d(i & 1, i >> 1 & 1, i >> 2 & 1)
				.scale(2)
				.subtract(1, 1, 1)
				.scale(0.075)
				.yRot(-camera.getYRot() * ShoulderHelper.DEG_TO_RAD);
			Vector3d from = cameraPos.add(offset);
			Vector3d to = from.add(cameraOffset);
			RayTraceContext context = new RayTraceContext(from, to, RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, camera.getEntity());
			RayTraceResult hitResult = level.clip(context);
			
			if(hitResult.getType() != Type.MISS)
			{
				double newDistance = hitResult.getLocation().distanceTo(cameraPos);
				
				if(newDistance < distance)
				{
					distance = newDistance;
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
		return ShoulderInstance.getInstance().doShoulderSurfing() &&
			(this.cameraDistance < Minecraft.getInstance().getCameraEntity().getBbWidth() * Config.CLIENT.keepCameraOutOfHeadMultiplier()
				|| Minecraft.getInstance().getCameraEntity().xRot < Config.CLIENT.getCenterCameraWhenLookingDownAngle() - 90);
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
