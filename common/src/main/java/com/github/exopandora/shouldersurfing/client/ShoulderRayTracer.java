package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.CrosshairType;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Predicate;

public class ShoulderRayTracer
{
	private static final Predicate<Entity> ENTITY_IS_PICKABLE = entity -> !entity.isSpectator() && entity.isPickable();
	
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
			ShoulderRayTraceContext context = ShoulderRayTraceContext.from(camera, entity, partialTick, playerReachSq);
			Vector3d from = context.startPos();
			Vector3d to = context.endPos();
			aabb = aabb.move(context.startPos().subtract(eyePosition));
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
			ShoulderRayTraceContext context = ShoulderRayTraceContext.from(camera, entity, partialTick, distance * distance);
			Vector3d from = camera.getPosition();
			Vector3d to = context.endPos();
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
}
