package com.teamderpy.shouldersurfing.math;

import java.util.List;
import java.util.Optional;

import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class RayTracer
{
	private static Vec2f projectedVector = null;
	private static Vec3d rayTraceHit = null;
	
	public static void traceFromEyes(final float partialTicks)
	{
		RayTracer.projectedVector = null;
		Entity renderView = Minecraft.getInstance().getRenderViewEntity();
		
		if(renderView != null && Minecraft.getInstance().world != null && Minecraft.getInstance().gameSettings.thirdPersonView == Config.CLIENT.getShoulderSurfing3ppId())
		{
			double playerReach = Config.CLIENT.showCrosshairFarther() ? ShoulderSurfing.RAYTRACE_DISTANCE : Minecraft.getInstance().playerController.getBlockReachDistance();
			double blockDist = 0;
			RayTraceResult result = renderView.func_213324_a(playerReach, partialTicks, false);
			
			if(result != null)
			{
				RayTracer.rayTraceHit = result.getHitVec();
				blockDist = result.getHitVec().distanceTo(new Vec3d(renderView.posX, renderView.posY, renderView.posZ));
			}
			else
			{
				RayTracer.rayTraceHit = null;
			}
			
			Vec3d renderViewPos = renderView.getEyePosition(partialTicks);
			Vec3d sightVector = renderView.getLook(partialTicks);
			Vec3d sightRay = renderViewPos.add(sightVector.x * playerReach - 5, sightVector.y * playerReach, sightVector.z * playerReach);
			
			List<Entity> entityList = Minecraft.getInstance().world.getEntitiesWithinAABBExcludingEntity(renderView, renderView.getBoundingBox()
					.expand(sightVector.x * playerReach, sightVector.y * playerReach, sightVector.z * playerReach)
					.expand(1.0D, 1.0D, 1.0D));
			
			for(Entity entity : entityList)
			{
				if(entity.canBeCollidedWith())
				{
					float collisionSize = entity.getCollisionBorderSize();
					AxisAlignedBB aabb = entity.getBoundingBox().expand(collisionSize, collisionSize, collisionSize);
					Optional<Vec3d> intercept = aabb.rayTrace(renderViewPos, sightRay);
					
					if(intercept.isPresent())
					{
						double entityDist = intercept.get().distanceTo(new Vec3d(renderView.posX, renderView.posY, renderView.posZ));
						
						if(entityDist < blockDist)
						{
							RayTracer.rayTraceHit = intercept.get();
						}
					}
				}
			}
		}
	}
	
	public static Vec2f getProjectedVector()
	{
		return RayTracer.projectedVector;
	}
	
	public static void setProjectedVector(Vec2f projectedVector)
	{
		RayTracer.projectedVector = projectedVector;
	}
	
	public static Vec3d getRayTraceHit()
	{
		return RayTracer.rayTraceHit;
	}
	
	public static void setRayTraceHit(Vec3d rayTraceHit)
	{
		RayTracer.rayTraceHit = rayTraceHit;
	}
}
