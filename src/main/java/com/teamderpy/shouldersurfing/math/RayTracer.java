package com.teamderpy.shouldersurfing.math;

import java.util.List;
import java.util.Optional;

import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Config.ClientConfig.Perspective;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class RayTracer
{
	private static Vec2f projectedVector = null;
	private static Vector3d rayTraceHit = null;
	
	public static void traceFromEyes(final float partialTicks)
	{
		RayTracer.projectedVector = null;
		Entity renderView = Minecraft.getInstance().getRenderViewEntity();
		
		if(renderView != null && Minecraft.getInstance().world != null && Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId())
		{
			double playerReach = Config.CLIENT.showCrosshairFarther() ? ShoulderSurfing.RAYTRACE_DISTANCE : Minecraft.getInstance().playerController.getBlockReachDistance();
			double blockDist = 0;
			RayTraceResult result = renderView.pick(playerReach, partialTicks, false);
			
			if(result != null)
			{
				RayTracer.rayTraceHit = result.getHitVec();
				blockDist = result.getHitVec().distanceTo(renderView.getPositionVec());
			}
			else
			{
				RayTracer.rayTraceHit = null;
			}
			
			Vector3d renderViewPos = renderView.getEyePosition(partialTicks);
			Vector3d sightVector = renderView.getLook(partialTicks);
			Vector3d sightRay = renderViewPos.add(sightVector.x * playerReach - 5, sightVector.y * playerReach, sightVector.z * playerReach);
			
			List<Entity> entityList = Minecraft.getInstance().world.getEntitiesWithinAABBExcludingEntity(renderView, renderView.getBoundingBox()
					.expand(sightVector.x * playerReach, sightVector.y * playerReach, sightVector.z * playerReach)
					.expand(1.0D, 1.0D, 1.0D));
			
			for(Entity entity : entityList)
			{
				if(entity.canBeCollidedWith())
				{
					float collisionSize = entity.getCollisionBorderSize();
					AxisAlignedBB aabb = entity.getBoundingBox().expand(collisionSize, collisionSize, collisionSize);
					Optional<Vector3d> intercept = aabb.rayTrace(renderViewPos, sightRay);
					
					if(intercept.isPresent())
					{
						double entityDist = intercept.get().distanceTo(renderView.getPositionVec());
						
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
	
	public static Vector3d getRayTraceHit()
	{
		return RayTracer.rayTraceHit;
	}
	
	public static void setRayTraceHit(Vector3d rayTraceHit)
	{
		RayTracer.rayTraceHit = rayTraceHit;
	}
}
