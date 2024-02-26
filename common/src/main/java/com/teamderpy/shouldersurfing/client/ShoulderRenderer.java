package com.teamderpy.shouldersurfing.client;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.math.Vec2f;
import com.teamderpy.shouldersurfing.mixins.CameraAccessor;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
	private double cameraOffsetX;
	private double cameraOffsetY;
	private double cameraOffsetZ;
	private float cameraEntityAlpha = 1.0F;
	
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
			poseStack.last().pose().translate(this.translation.getX(), -this.translation.getY(), 0F);
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
	
	public void offsetCamera(Camera camera, Level level, float partialTick)
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
			
			if(Config.CLIENT.doCenterCameraWhenClimbing() && camera.getEntity() instanceof LivingEntity living && living.onClimbable())
			{
				targetXOffset = 0;
			}
			
			if(camera.getLookVector().angle(VECTOR_NEGATIVE_Y) < Config.CLIENT.getCenterCameraWhenLookingDownAngle() * Mth.DEG_TO_RAD)
			{
				targetXOffset = 0;
				targetYOffset = 0;
			}
			
			if(Config.CLIENT.doDynamicallyAdjustOffsets())
			{
				Vec3 localCameraOffset = new Vec3(targetXOffset, targetYOffset, targetZOffset);
				Vec3 worldCameraOffset = new Vec3(camera.getUpVector()).scale(targetYOffset)
					.add(new Vec3(camera.getLeftVector()).scale(targetXOffset))
					.add(new Vec3(camera.getLookVector()).scale(-targetZOffset))
					.normalize()
					.scale(localCameraOffset.length());
				Vec3 worldXYOffset = ShoulderHelper.calcRayTraceHeadOffset(camera, worldCameraOffset);
				Vec3 eyePosition = camera.getEntity().getEyePosition(partialTick);
				double absOffsetX = Math.abs(targetXOffset);
				double absOffsetY = Math.abs(targetYOffset);
				double absOffsetZ = Math.abs(targetZOffset);
				double targetX = absOffsetX;
				double targetY = absOffsetY;
				double clearance = camera.getEntity().getBbWidth() / 3.0D;
				
				for(double dz = 0; dz <= absOffsetZ; dz += 0.03125D)
				{
					double scale = dz / absOffsetZ;
					Vec3 from = eyePosition.add(worldCameraOffset.scale(scale));
					Vec3 to = eyePosition.add(worldXYOffset).add(new Vec3(camera.getLookVector()).scale(-dz));
					ClipContext context = new ClipContext(from, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, camera.getEntity());
					HitResult hitResult = level.clip(context);
					
					if(hitResult.getType() != HitResult.Type.MISS)
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
			
			CameraAccessor accessor = ((CameraAccessor) camera);
			double x = Mth.lerp(partialTick, camera.getEntity().xo, camera.getEntity().getX());
			double y = Mth.lerp(partialTick, camera.getEntity().yo, camera.getEntity().getY()) + Mth.lerp(partialTick, accessor.getEyeHeightOld(), accessor.getEyeHeight());
			double z = Mth.lerp(partialTick, camera.getEntity().zo, camera.getEntity().getZ());
			accessor.invokeSetPosition(x, y, z);
			double offsetX = Mth.lerp(partialTick, instance.getOffsetXOld(), instance.getOffsetX());
			double offsetY = Mth.lerp(partialTick, instance.getOffsetYOld(), instance.getOffsetY());
			double offsetZ = Mth.lerp(partialTick, instance.getOffsetZOld(), instance.getOffsetZ());
			Vec3 offset = new Vec3(offsetX, offsetY, offsetZ);
			this.cameraDistance = this.calcCameraDistance(camera, level, accessor.invokeGetMaxZoom(offset.length()), partialTick);
			Vec3 scaled = offset.normalize().scale(this.cameraDistance);
			this.cameraOffsetX = scaled.x;
			this.cameraOffsetY = scaled.y;
			this.cameraOffsetZ = scaled.z;
			accessor.invokeMove(-scaled.z, scaled.y, scaled.x);
		}
	}
	
	private double calcCameraDistance(Camera camera, Level level, double distance, float partialTick)
	{
		Vec3 cameraPos = camera.getPosition();
		Vec3 cameraOffset = ShoulderHelper.calcCameraOffset(camera, distance, partialTick);
		
		for(int i = 0; i < 8; i++)
		{
			Vec3 offset = new Vec3(i & 1, i >> 1 & 1, i >> 2 & 1)
				.scale(2)
				.subtract(1, 1, 1)
				.scale(0.075)
				.yRot(-camera.getYRot() * Mth.DEG_TO_RAD);
			Vec3 from = cameraPos.add(offset);
			Vec3 to = from.add(cameraOffset);
			ClipContext context = new ClipContext(from, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, camera.getEntity());
			HitResult hitResult = level.clip(context);
			
			if(hitResult.getType() != HitResult.Type.MISS)
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
		vec.mul(modelView);
		vec.mul(projection);
		
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
	
	private boolean skipCameraEntityRendering()
	{
		return ShoulderInstance.getInstance().doShoulderSurfing() &&
			(this.cameraDistance < Minecraft.getInstance().getCameraEntity().getBbWidth() * Config.CLIENT.keepCameraOutOfHeadMultiplier()
				|| Minecraft.getInstance().getCameraEntity().getXRot() < Config.CLIENT.getCenterCameraWhenLookingDownAngle() - 90
				|| Minecraft.getInstance().getCameraEntity() instanceof Player player && player.isScoping());
	}
	
	public boolean preRenderCameraEntity(Entity entity, float partialTick)
	{
		if(this.skipCameraEntityRendering())
		{
			return true;
		}
		
		if(this.shouldRenderCameraEntityTransparent(entity))
		{
			this.cameraEntityAlpha = (float) Mth.clamp(Math.abs(this.cameraOffsetX) / (entity.getBbWidth() / 2.0D), 0.15F, 1.0F);
		}
		
		return false;
	}
	
	public void postRenderCameraEntity(Entity entity, float partialTick, MultiBufferSource multiBufferSource)
	{
		if(this.shouldRenderCameraEntityTransparent(entity))
		{
			((BufferSource) multiBufferSource).endLastBatch();
		}
		
		this.cameraEntityAlpha = 1.0F;
	}
	
	private boolean shouldRenderCameraEntityTransparent(Entity entity)
	{
		return ShoulderInstance.getInstance().doShoulderSurfing() && Math.abs(this.cameraOffsetX) < (entity.getBbWidth() / 2.0D);
	}
	
	public double getPlayerReach()
	{
		return Config.CLIENT.useCustomRaytraceDistance() ? Config.CLIENT.getCustomRaytraceDistance() : 0;
	}
	
	public double getCameraDistance()
	{
		return this.cameraDistance;
	}
	
	public double getCameraOffsetX()
	{
		return this.cameraOffsetX;
	}
	
	public double getCameraOffsetY()
	{
		return this.cameraOffsetY;
	}
	
	public double getCameraOffsetZ()
	{
		return this.cameraOffsetZ;
	}
	
	public float getCameraEntityAlpha()
	{
		return this.cameraEntityAlpha;
	}
	
	public static ShoulderRenderer getInstance()
	{
		return INSTANCE;
	}
}
