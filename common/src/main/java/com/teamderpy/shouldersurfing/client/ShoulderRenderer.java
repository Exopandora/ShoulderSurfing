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
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
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
			ActiveRenderInfoAccessor accessor = ((ActiveRenderInfoAccessor) camera);
			double x = MathHelper.lerp(partialTick, camera.getEntity().xo, camera.getEntity().getX());
			double y = MathHelper.lerp(partialTick, camera.getEntity().yo, camera.getEntity().getY()) + MathHelper.lerp(partialTick, accessor.getEyeHeightOld(), accessor.getEyeHeight());
			double z = MathHelper.lerp(partialTick, camera.getEntity().zo, camera.getEntity().getZ());
			accessor.invokeSetPosition(x, y, z);
			Vector3d offset = new Vector3d(-Config.CLIENT.getOffsetZ(), Config.CLIENT.getOffsetY(), Config.CLIENT.getOffsetX());
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
			Vector3d offset = new Vector3d(i & 1, i >> 1 & 1, i >> 2 & 1).scale(2).subtract(1, 1, 1).scale(0.1);
			Vector3d from = cameraPos.add(offset);
			Vector3d to = from.add(cameraOffset);
			RayTraceContext context = new RayTraceContext(from, to, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, camera.getEntity());
			RayTraceResult hitResult = level.clip(context);
			
			if(hitResult != null)
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
	
	@SuppressWarnings("resource")
	public void updateDynamicRaytrace(ActiveRenderInfo camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float partialTick)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing())
		{
			PlayerController controller = Minecraft.getInstance().gameMode;
			double playerReachOverride = Config.CLIENT.useCustomRaytraceDistance() ? Config.CLIENT.getCustomRaytraceDistance() : 0;
			RayTraceResult hitResult = this.rayTraceFromEyes(camera, controller, playerReachOverride, partialTick);
			Vector3d position = hitResult.getLocation().subtract(camera.getPosition());
			this.projected = this.project2D(position, modelViewMatrix, projectionMatrix);
		}
	}
	
	private RayTraceResult rayTraceFromEyes(ActiveRenderInfo camera, PlayerController gameMode, double playerReachOverride, final float partialTick)
	{
		double playerReach = Math.max(gameMode.getPickRange(), playerReachOverride);
		Entity cameraEntity = camera.getEntity();
		RayTraceResult blockTrace = cameraEntity.pick(playerReach, partialTick, false);
		Vector3d eyePosition = cameraEntity.getEyePosition(partialTick);
		
		if(gameMode.hasFarPickRange())
		{
			playerReach = Math.max(playerReach, gameMode.getPlayerMode().isCreative() ? 6.0D : 3.0D);
		}
		
		double playerReachSqr = playerReach * playerReach;
		
		if(blockTrace != null)
		{
			playerReachSqr = blockTrace.getLocation().distanceToSqr(eyePosition);
		}
		
		Vector3d viewVector = cameraEntity.getViewVector(1.0F);
		Vector3d traceEnd = eyePosition.add(viewVector.scale(playerReach));
		AxisAlignedBB aabb = cameraEntity.getBoundingBox().expandTowards(viewVector.scale(playerReach)).inflate(1.0D, 1.0D, 1.0D);
		EntityRayTraceResult entityTrace = ProjectileHelper.getEntityHitResult(cameraEntity, eyePosition, traceEnd, aabb, entity -> !entity.isSpectator() && entity.isPickable(), playerReachSqr);
		
		if(entityTrace != null)
		{
			double distanceSq = eyePosition.distanceToSqr(entityTrace.getLocation());
			
			if(distanceSq < playerReachSqr || blockTrace == null)
			{
				return entityTrace;
			}
		}
		
		return blockTrace;
	}
	
	@Nullable
	private Vec2f project2D(Vector3d position, Matrix4f modelView, Matrix4f projection)
	{
		Vector4f vec = new Vector4f((float) position.x(), (float) position.y(), (float) position.z(), 1.0F);
		vec.transform(modelView);
		vec.transform(projection);
		
		if(vec.w() == 0.0F)
		{
			return null;
		}
		
		float w = (1.0F / vec.w()) * 0.5F;
		float x = vec.x() * w + 0.5F;
		float y = vec.y() * w + 0.5F;
		float z = vec.z() * w + 0.5F;
		vec.set(x, y, z, w);
		
		x *= Minecraft.getInstance().getWindow().getScreenWidth();
		y *= Minecraft.getInstance().getWindow().getScreenHeight();
		
		if(Float.isInfinite(x) || Float.isInfinite(y) || Float.isNaN(x) || Float.isNaN(y))
		{
			return null;
		}
		
		return new Vec2f(x, y);
	}
	
	public boolean skipRenderPlayer()
	{
		return this.cameraDistance < 0.80 && Config.CLIENT.keepCameraOutOfHead() && ShoulderInstance.getInstance().doShoulderSurfing();
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
