package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.CrosshairType;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import com.github.exopandora.shouldersurfing.plugin.ShoulderSurfingRegistrar;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ShoulderHelper
{
	public static final float DEG_TO_RAD = (float) (Math.PI / 180F);
	public static final float RAD_TO_DEG = (float) (180F / Math.PI);
	private static final Predicate<Entity> ENTITY_IS_PICKABLE = entity -> !entity.isSpectator() && entity.isPickable();
	
	public static ShoulderLook shoulderSurfingLook(ActiveRenderInfo camera, Entity entity, float partialTick, double distanceSq)
	{
		Vector3d cameraOffset = camera.getPosition().subtract(entity.getEyePosition(partialTick));
		Vector3d headOffset = ShoulderHelper.calcRayTraceHeadOffset(camera, cameraOffset);
		Vector3d cameraPos = camera.getPosition();
		Vector3d viewVector = new Vector3d(camera.getLookVector());
		
		if(headOffset.lengthSqr() < distanceSq)
		{
			distanceSq -= headOffset.lengthSqr();
		}
		
		double distance = Math.sqrt(distanceSq) + cameraOffset.distanceTo(headOffset);
		Vector3d traceEnd = cameraPos.add(viewVector.scale(distance));
		return new ShoulderLook(cameraPos, traceEnd, headOffset);
	}
	
	public static Vector3d calcRayTraceHeadOffset(@NotNull ActiveRenderInfo camera, Vector3d cameraOffset)
	{
		Vector3d lookVector = new Vector3d(camera.getLookVector());
		return ShoulderHelper.calcPlaneWithLineIntersection(Vector3d.ZERO, lookVector, cameraOffset, lookVector);
	}
	
	public static Vector3d calcPlaneWithLineIntersection(Vector3d planePoint, Vector3d planeNormal, Vector3d linePoint, Vector3d lineNormal)
	{
		double distance = (planeNormal.dot(planePoint) - planeNormal.dot(linePoint)) / planeNormal.dot(lineNormal);
		return linePoint.add(lineNormal.scale(distance));
	}
	
	public static RayTraceResult traceBlocksAndEntities(ActiveRenderInfo camera, PlayerController gameMode, double playerReachOverride, RayTraceContext.FluidMode fluidContext, float partialTick, boolean traceEntities, boolean doOffsetTrace)
	{
		Entity entity = camera.getEntity();
		double playerReach = Math.max(gameMode.getPickRange(), playerReachOverride);
		RayTraceResult blockHit = traceBlocks(camera, entity, fluidContext, playerReach, partialTick, doOffsetTrace);
		
		if(!traceEntities)
		{
			return blockHit;
		}
		
		Vector3d eyePosition = entity.getEyePosition(partialTick);
		
		if(gameMode.hasFarPickRange())
		{
			playerReach = Math.max(playerReach, gameMode.getPlayerMode().isCreative() ? 6.0D : 3.0D);
		}
		
		if(blockHit.getType() != RayTraceResult.Type.MISS)
		{
			playerReach = blockHit.getLocation().distanceTo(eyePosition);
		}
		
		EntityRayTraceResult entityHit = traceEntities(camera, entity, playerReach, partialTick, doOffsetTrace);
		
		if(entityHit != null)
		{
			double distance = eyePosition.distanceTo(entityHit.getLocation());
			
			if(distance < playerReach || blockHit.getType() != RayTraceResult.Type.MISS)
			{
				return entityHit;
			}
		}
		
		return blockHit;
	}
	
	public static EntityRayTraceResult traceEntities(ActiveRenderInfo camera, Entity entity, double playerReach, float partialTick, boolean doOffsetTrace)
	{
		double playerReachSq = playerReach * playerReach;
		Vector3d viewVector = new Vector3d(camera.getLookVector()).scale(playerReach);
		Vector3d eyePosition = entity.getEyePosition(partialTick);
		AxisAlignedBB aabb = entity.getBoundingBox()
			.expandTowards(viewVector)
			.inflate(1.0D, 1.0D, 1.0D);
		
		if(doOffsetTrace)
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(camera, entity, partialTick, playerReachSq);
			Vector3d from = eyePosition.add(look.headOffset());
			Vector3d to = look.traceEndPos();
			aabb = aabb.move(look.headOffset());
			return ProjectileHelper.getEntityHitResult(entity, from, to, aabb, ENTITY_IS_PICKABLE, from.distanceToSqr(to));
		}
		else
		{
			Vector3d from = eyePosition;
			Vector3d to = from.add(viewVector);
			return ProjectileHelper.getEntityHitResult(entity, from, to, aabb, ENTITY_IS_PICKABLE, playerReachSq);
		}
	}
	
	public static BlockRayTraceResult traceBlocks(ActiveRenderInfo camera, Entity entity, RayTraceContext.FluidMode fluidContext, double distance, float partialTick, boolean doOffsetTrace)
	{
		if(doOffsetTrace)
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(camera, entity, partialTick, distance * distance);
			Vector3d from = camera.getPosition();
			Vector3d to = look.traceEndPos();
			RayTraceContext.BlockMode blockContext = ShoulderInstance.getInstance().isAiming() ? RayTraceContext.BlockMode.COLLIDER : RayTraceContext.BlockMode.OUTLINE;
			return entity.level.clip(new RayTraceContext(from, to, blockContext, fluidContext, entity));
		}
		else
		{
			Vector3d from = entity.getEyePosition(partialTick);
			Vector3d view = new Vector3d(camera.getLookVector());
			Vector3d to = from.add(view.scale(distance));
			RayTraceContext.BlockMode blockContext = ShoulderInstance.getInstance().isAiming() || Config.CLIENT.getCrosshairType() != CrosshairType.DYNAMIC ? RayTraceContext.BlockMode.COLLIDER : RayTraceContext.BlockMode.OUTLINE;
			return entity.level.clip(new RayTraceContext(from, to, blockContext, fluidContext, entity));
		}
	}
	
	public static boolean isHoldingAdaptiveItem()
	{
		Minecraft minecraft = Minecraft.getInstance();
		
		if(minecraft.cameraEntity instanceof LivingEntity)
		{
			return ShoulderSurfingRegistrar.getInstance().getAdaptiveItemCallbacks().stream().anyMatch(callback -> callback.isHoldingAdaptiveItem(minecraft, (LivingEntity) minecraft.cameraEntity));
		}
		
		return false;
	}
	
	public static double angle(Vector3f a, Vector3f b)
	{
		return Math.acos(a.dot(b) / (length(a) * length(b)));
	}
	
	public static double length(Vector3f vec)
	{
		return MathHelper.sqrt(vec.x() * vec.x() + vec.y() * vec.y() + vec.z() * vec.z());
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
	
	public static class ShoulderLook
	{
		private final Vector3d cameraPos;
		private final Vector3d traceEndPos;
		private final Vector3d headOffset;
		
		public ShoulderLook(Vector3d cameraPos, Vector3d traceEndPos, Vector3d headOffset)
		{
			this.cameraPos = cameraPos;
			this.traceEndPos = traceEndPos;
			this.headOffset = headOffset;
		}
		
		public Vector3d cameraPos()
		{
			return this.cameraPos;
		}
		
		public Vector3d traceEndPos()
		{
			return this.traceEndPos;
		}
		
		public Vector3d headOffset()
		{
			return this.headOffset;
		}
	}
}
