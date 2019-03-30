package com.teamderpy.shouldersurfing.math;

import java.util.List;

import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class RayTracer
{
	private boolean rayTraceInReach = false;
	private boolean skipPlayerRender = false;
	
	private Vec2f projectedVector = null;
	private Vec3d rayTraceHit = null;
	
	private static final RayTracer INSTANCE = new RayTracer();
	
	public static RayTracer getInstance()
	{
		return INSTANCE;
	}
	
	public void traceFromEyes(final float tick)
	{
		this.projectedVector = null;
		Entity renderView = Minecraft.getInstance().getRenderViewEntity();
		
		if(renderView != null)
		{
			if(Minecraft.getInstance().world != null)
			{
				if(Minecraft.getInstance().gameSettings.thirdPersonView == Config.CLIENT.getShoulderSurfing3ppId())
				{
					double playerReach = 1D;
					
					if(Config.CLIENT.showCrosshairFarther())
					{
						playerReach = ShoulderSurfing.RAYTRACE_DISTANCE;
					}
					else
					{
						playerReach = (double) Minecraft.getInstance().playerController.getBlockReachDistance();
					}
					
					RayTraceResult result = renderView.rayTrace(playerReach, tick, RayTraceFluidMode.NEVER);
					double blockDist = 0;
					
					if(result != null)
					{
						this.rayTraceHit = result.hitVec;
						blockDist = result.hitVec.distanceTo(new Vec3d(renderView.posX, renderView.posY, renderView.posZ));
						this.rayTraceInReach = blockDist <= (double) Minecraft.getInstance().playerController.getBlockReachDistance();
					}
					else
					{
						this.rayTraceHit = null;
					}
					
					Vec3d renderViewPos = renderView.getEyePosition(tick);
					Vec3d sightVector = renderView.getLook(tick);
					Vec3d sightRay = renderViewPos.add(sightVector.x * playerReach - 5, sightVector.y * playerReach, sightVector.z * playerReach);
					
					List<Entity> entityList = Minecraft.getInstance().world
							.getEntitiesWithinAABBExcludingEntity(renderView, renderView.getBoundingBox()
							.expand(sightVector.x * playerReach, sightVector.y * playerReach, sightVector.z * playerReach)
							.expand(1.0D, 1.0D, 1.0D));
					
					for(int i = 0; i < entityList.size(); ++i)
					{
						Entity ent = (Entity) entityList.get(i);
						
						if(ent.canBeCollidedWith())
						{
							float collisionSize = ent.getCollisionBorderSize();
							
							AxisAlignedBB aabb = ent.getBoundingBox().expand((double) collisionSize, (double) collisionSize, (double) collisionSize);
							RayTraceResult potentialIntercept = aabb.calculateIntercept(renderViewPos, sightRay);
							
							if(potentialIntercept != null)
							{
								double entityDist = potentialIntercept.hitVec.distanceTo(new Vec3d(renderView.posX, renderView.posY, renderView.posZ));
								
								if(entityDist < blockDist)
								{
									this.rayTraceHit = potentialIntercept.hitVec;
									
									// System.out.println("entity dist: " +
									// entityDist);
									this.rayTraceInReach = entityDist <= (double) Minecraft.getInstance().playerController.getBlockReachDistance();
								}
							}
						}
					}
				}
			}
		}
	}
	
	public boolean isRayTraceInReach()
	{
		return rayTraceInReach;
	}
	
	public void setRayTraceInReach(boolean rayTraceInReach)
	{
		this.rayTraceInReach = rayTraceInReach;
	}
	
	public boolean skipPlayerRender()
	{
		return this.skipPlayerRender;
	}
	
	public void setSkipPlayerRender(boolean skipPlayerRender)
	{
		this.skipPlayerRender = skipPlayerRender;
	}
	
	public Vec2f getProjectedVector()
	{
		return this.projectedVector;
	}
	
	public void setProjectedVector(Vec2f projectedVector)
	{
		this.projectedVector = projectedVector;
	}
	
	public Vec3d getRayTraceHit()
	{
		return this.rayTraceHit;
	}
	
	public void setRayTraceHit(Vec3d rayTraceHit)
	{
		this.rayTraceHit = rayTraceHit;
	}
}
