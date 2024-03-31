package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.CrosshairType;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import com.github.exopandora.shouldersurfing.plugin.ShoulderSurfingRegistrar;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.function.Predicate;

public class ShoulderHelper
{
	private static final Predicate<Entity> ENTITY_IS_PICKABLE = entity -> !entity.isSpectator() && entity.isPickable();
	
	public static ShoulderLook shoulderSurfingLook(Camera camera, Entity entity, float partialTick, double distanceSq)
	{
		Vec3 cameraOffset = camera.getPosition().subtract(entity.getEyePosition(partialTick));
		Vec3 headOffset = ShoulderHelper.calcRayTraceHeadOffset(camera, cameraOffset);
		Vec3 cameraPos = camera.getPosition();
		Vec3 viewVector = new Vec3(camera.getLookVector());
		
		if(Config.CLIENT.limitPlayerReach() && headOffset.lengthSqr() < distanceSq)
		{
			distanceSq -= headOffset.lengthSqr();
		}
		
		double distance = Math.sqrt(distanceSq) + cameraOffset.distanceTo(headOffset);
		Vec3 traceEnd = cameraPos.add(viewVector.scale(distance));
		return new ShoulderLook(cameraPos, traceEnd, headOffset);
	}
	
	public static Vec3 calcRayTraceHeadOffset(@NotNull Camera camera, Vec3 cameraOffset)
	{
		Vec3 lookVector = new Vec3(camera.getLookVector());
		return ShoulderHelper.calcPlaneWithLineIntersection(Vec3.ZERO, lookVector, cameraOffset, lookVector);
	}
	
	public static Vec3 calcPlaneWithLineIntersection(Vec3 planePoint, Vec3 planeNormal, Vec3 linePoint, Vec3 lineNormal)
	{
		double distance = (planeNormal.dot(planePoint) - planeNormal.dot(linePoint)) / planeNormal.dot(lineNormal);
		return linePoint.add(lineNormal.scale(distance));
	}
	
	public static HitResult traceBlocksAndEntities(Camera camera, MultiPlayerGameMode gameMode, double playerReachOverride, ClipContext.Fluid fluidContext, float partialTick, boolean traceEntities, boolean doOffsetTrace)
	{
		Entity entity = camera.getEntity();
		double playerReach = Math.max(gameMode.getPickRange(), playerReachOverride);
		HitResult blockHit = traceBlocks(camera, entity, fluidContext, playerReach, partialTick, doOffsetTrace);
		
		if(!traceEntities)
		{
			return blockHit;
		}
		
		Vec3 eyePosition = entity.getEyePosition(partialTick);
		
		if(gameMode.hasFarPickRange())
		{
			playerReach = Math.max(playerReach, gameMode.getPlayerMode().isCreative() ? 6.0D : 3.0D);
		}
		
		if(blockHit.getType() != HitResult.Type.MISS)
		{
			playerReach = blockHit.getLocation().distanceTo(eyePosition);
		}
		
		EntityHitResult entityHit = traceEntities(camera, entity, playerReach, partialTick, doOffsetTrace);
		
		if(entityHit != null)
		{
			double distance = eyePosition.distanceTo(entityHit.getLocation());
			
			if(distance < playerReach || blockHit.getType() != HitResult.Type.MISS)
			{
				return entityHit;
			}
		}
		
		return blockHit;
	}
	
	public static EntityHitResult traceEntities(Camera camera, Entity entity, double playerReach, float partialTick, boolean doOffsetTrace)
	{
		double playerReachSq = playerReach * playerReach;
		Vec3 viewVector = new Vec3(camera.getLookVector()).scale(playerReach);
		Vec3 eyePosition = entity.getEyePosition(partialTick);
		AABB aabb = entity.getBoundingBox()
			.expandTowards(viewVector)
			.inflate(1.0D, 1.0D, 1.0D);
		
		if(doOffsetTrace)
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(camera, entity, partialTick, playerReachSq);
			Vec3 from = eyePosition.add(look.headOffset());
			Vec3 to = look.traceEndPos();
			aabb = aabb.move(look.headOffset());
			return ProjectileUtil.getEntityHitResult(entity, from, to, aabb, ENTITY_IS_PICKABLE, from.distanceToSqr(to));
		}
		else
		{
			Vec3 from = eyePosition;
			Vec3 to = from.add(viewVector);
			return ProjectileUtil.getEntityHitResult(entity, from, to, aabb, ENTITY_IS_PICKABLE, playerReachSq);
		}
	}
	
	public static BlockHitResult traceBlocks(Camera camera, Entity entity, ClipContext.Fluid fluidContext, double distance, float partialTick, boolean doOffsetTrace)
	{
		if(doOffsetTrace)
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(camera, entity, partialTick, distance * distance);
			Vec3 from = camera.getPosition();
			Vec3 to = look.traceEndPos();
			return entity.level().clip(new ClipContext(from, to, ShoulderInstance.getInstance().isAiming() ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE, fluidContext, entity));
		}
		else
		{
			Vec3 from = entity.getEyePosition(partialTick);
			Vec3 view = new Vec3(camera.getLookVector());
			Vec3 to = from.add(view.scale(distance));
			ClipContext.Block blockContext = Config.CLIENT.getCrosshairType() == CrosshairType.DYNAMIC ? ClipContext.Block.OUTLINE : ClipContext.Block.COLLIDER;
			return entity.level().clip(new ClipContext(from, to, blockContext, fluidContext, entity));
		}
	}
	
	public static boolean isHoldingAdaptiveItem()
	{
		Minecraft minecraft = Minecraft.getInstance();
		
		if(minecraft.getCameraEntity() instanceof LivingEntity entity)
		{
			return ShoulderSurfingRegistrar.getInstance().getAdaptiveItemCallbacks().stream().anyMatch(callback -> callback.isHoldingAdaptiveItem(minecraft, entity));
		}
		
		return false;
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
	
	public record ShoulderLook(Vec3 cameraPos, Vec3 traceEndPos, Vec3 headOffset) {}
}
