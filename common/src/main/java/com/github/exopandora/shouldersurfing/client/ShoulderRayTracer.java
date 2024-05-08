package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.CrosshairType;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class ShoulderRayTracer
{
	private static final Predicate<Entity> ENTITY_IS_PICKABLE = entity -> !entity.isSpectator() && entity.isPickable();
	
	public static HitResult traceBlocksAndEntities(Camera camera, Player player, double interactionRangeOverride, ClipContext.Fluid fluidContext, float partialTick, boolean traceEntities, boolean doOffsetTrace)
	{
		Entity entity = camera.getEntity();
		double interactionRange = Math.max(maxInteractionRange(player), interactionRangeOverride);
		HitResult blockHit = traceBlocks(camera, entity, fluidContext, interactionRange, partialTick, doOffsetTrace);
		
		if(!traceEntities)
		{
			return blockHit;
		}
		
		Vec3 eyePosition = entity.getEyePosition(partialTick);
		
		if(blockHit.getType() != HitResult.Type.MISS)
		{
			interactionRange = blockHit.getLocation().distanceTo(eyePosition);
		}
		
		EntityHitResult entityHit = traceEntities(camera, entity, interactionRange, partialTick, doOffsetTrace);
		
		if(entityHit != null)
		{
			double distance = eyePosition.distanceTo(entityHit.getLocation());
			
			if(distance < interactionRange || blockHit.getType() != HitResult.Type.MISS)
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
			ShoulderRayTraceContext context = ShoulderRayTraceContext.from(camera, entity, partialTick, playerReachSq);
			Vec3 from = context.startPos();
			Vec3 to = context.endPos();
			aabb = aabb.move(context.startPos().subtract(eyePosition));
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
			ShoulderRayTraceContext context = ShoulderRayTraceContext.from(camera, entity, partialTick, distance * distance);
			Vec3 from = camera.getPosition();
			Vec3 to = context.endPos();
			ClipContext.Block blockContext = ShoulderInstance.getInstance().isAiming() ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE;
			return entity.level().clip(new ClipContext(from, to, blockContext, fluidContext, entity));
		}
		else
		{
			Vec3 from = entity.getEyePosition(partialTick);
			Vec3 view = new Vec3(camera.getLookVector());
			Vec3 to = from.add(view.scale(distance));
			ClipContext.Block blockContext = ShoulderInstance.getInstance().isAiming() || Config.CLIENT.getCrosshairType() != CrosshairType.DYNAMIC ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE;
			return entity.level().clip(new ClipContext(from, to, blockContext, fluidContext, entity));
		}
	}
	
	public static double maxInteractionRange(Player player)
	{
		return Math.max(player.blockInteractionRange(), player.entityInteractionRange());
	}
}
