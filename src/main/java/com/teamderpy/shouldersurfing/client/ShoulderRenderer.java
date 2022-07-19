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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ShoulderRenderer
{
	private static ShoulderRenderer instance;
	private final ShoulderInstance shoulderInstance;
	private double cameraDistance;
	private Vec2f lastTranslation = Vec2f.ZERO;
	private Vec2f translation = Vec2f.ZERO;
	private Vec2f projected;
	
	public ShoulderRenderer(ShoulderInstance shoulderInstance)
	{
		instance = this;
		this.shoulderInstance = shoulderInstance;
	}
	
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
		
		if(Config.CLIENT.getCrosshairType().isDynamic() && this.shoulderInstance.doShoulderSurfing())
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
		if(Config.CLIENT.getCrosshairType().isDynamic() && this.shoulderInstance.doShoulderSurfing())
		{
			poseStack.popPose();
		}
	}
	
	public void offsetCamera(Camera camera, Level level, double partialTicks)
	{
		if(this.shoulderInstance.doShoulderSurfing() && level != null)
		{
			CameraAccessor accessor = ((CameraAccessor) camera);
			double x = Mth.lerp(partialTicks, camera.getEntity().xo, camera.getEntity().getX());
			double y = Mth.lerp(partialTicks, camera.getEntity().yo, camera.getEntity().getY()) + Mth.lerp(partialTicks, accessor.getEyeHeightOld(), accessor.getEyeHeight());
			double z = Mth.lerp(partialTicks, camera.getEntity().zo, camera.getEntity().getZ());
			accessor.invokeSetPosition(x, y, z);
			Vec3 offset = new Vec3(-Config.CLIENT.getOffsetZ(), Config.CLIENT.getOffsetY(), Config.CLIENT.getOffsetX());
			this.cameraDistance = this.calcCameraDistance(camera, level, accessor.invokeGetMaxZoom(offset.length()));
			Vec3 scaled = offset.normalize().scale(this.cameraDistance);
			accessor.invokeMove(scaled.x, scaled.y, scaled.z);
		}
	}
	
	public boolean skipRenderPlayer()
	{
		return this.cameraDistance < 0.80 && Config.CLIENT.keepCameraOutOfHead() && this.shoulderInstance.doShoulderSurfing();
	}
	
	@SuppressWarnings("resource")
	public void calcRaytrace(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float partialTicks)
	{
		if(this.shoulderInstance.doShoulderSurfing())
		{
			Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
			MultiPlayerGameMode controller = Minecraft.getInstance().gameMode;
			double playerReach = Config.CLIENT.useCustomRaytraceDistance() ? Config.CLIENT.getCustomRaytraceDistance() : 0;
			HitResult result = this.traceFromEyes(camera.getEntity(), controller, playerReach, partialTicks);
			Vec3 position = result.getLocation().subtract(camera.getPosition());
			this.projected = this.project2D(position, modelViewMatrix, projectionMatrix);
		}
	}
	
	private double calcCameraDistance(Camera camera, Level level, double distance)
	{
		Vec3 view = camera.getPosition();
		Vec3 cameraOffset = ShoulderHelper.cameraOffset(camera, distance);
		
		for(int i = 0; i < 8; i++)
		{
			Vec3 offset = new Vec3(i & 1, i >> 1 & 1, i >> 2 & 1).scale(2).subtract(1, 1, 1).scale(0.1);
			Vec3 head = view.add(offset);
			Vec3 cameraPosition = head.add(cameraOffset);
			ClipContext context = new ClipContext(head, cameraPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, camera.getEntity());
			HitResult result = level.clip(context);
			
			if(result != null)
			{
				double newDistance = result.getLocation().distanceTo(view);
				
				if(newDistance < distance)
				{
					distance = newDistance;
				}
			}
		}
		
		return distance;
	}
	
	@Nullable
	private Vec2f project2D(Vec3 position, Matrix4f modelView, Matrix4f projection)
	{
		Vector4f vec = new Vector4f((float) position.x(), (float) position.y(), (float) position.z(), 1.0F);
		vec.transform(modelView);
		vec.transform(projection);
		
		if(vec.w() == 0.0F)
		{
			return null;
		}
		
		vec.set(vec.x() * vec.w() + 0.5F, vec.y() * vec.w() + 0.5F, vec.z() * vec.w() + 0.5F, (1.0F / vec.w()) * 0.5F);
		
		float x = vec.x() * Minecraft.getInstance().getWindow().getScreenWidth();
		float y = vec.y() * Minecraft.getInstance().getWindow().getScreenHeight();
		
		if(Float.isInfinite(x) || Float.isInfinite(y) || Float.isNaN(x) || Float.isNaN(y))
		{
			return null;
		}
		
		return new Vec2f(x, y);
	}
	
	private HitResult traceFromEyes(Entity renderView, MultiPlayerGameMode gameMode, double playerReachOverride, final float partialTicks)
	{
		double blockReach = Math.max(gameMode.getPickRange(), playerReachOverride);
		HitResult blockTrace = renderView.pick(blockReach, partialTicks, false);
		Vec3 eyes = renderView.getEyePosition(partialTicks);
		double entityReach = blockReach;
		
		if(gameMode.hasFarPickRange())
		{
			entityReach = Math.max(6.0D, playerReachOverride);
			blockReach = entityReach;
		}
		
		entityReach = entityReach * entityReach;
		
		if(blockTrace != null)
		{
			entityReach = blockTrace.getLocation().distanceToSqr(eyes);
		}
		
		Vec3 look = renderView.getViewVector(1.0F);
		Vec3 end = eyes.add(look.scale(blockReach));
		AABB aabb = renderView.getBoundingBox().expandTowards(look.scale(blockReach)).inflate(1.0D, 1.0D, 1.0D);
		EntityHitResult entityTrace = ProjectileUtil.getEntityHitResult(renderView, eyes, end, aabb, entity -> !entity.isSpectator() && entity.isPickable(), entityReach);
		
		if(entityTrace != null)
		{
			double distanceSq = eyes.distanceToSqr(entityTrace.getLocation());
			
			if(distanceSq < entityReach || blockTrace == null)
			{
				return entityTrace;
			}
		}
		
		return blockTrace;
	}
	
	public double getCameraDistance()
	{
		return this.cameraDistance;
	}
	
	public static ShoulderRenderer getInstance()
	{
		return instance;
	}
}
