package com.teamderpy.shouldersurfing.math;

import java.util.List;

import com.teamderpy.shouldersurfing.ShoulderSettings;
import com.teamderpy.shouldersurfing.renderer.ShoulderRenderBin;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * @author Joshua Powers <jsh.powers@yahoo.com>
 * @version 1.1
 * @since 2013-01-12
 */
@SideOnly(Side.CLIENT)
public final class RayTracer
{
	public static void traceFromEyes(final float tick)
	{
		ShoulderRenderBin.projectedVector = null;
		
		if(Minecraft.getMinecraft().renderViewEntity != null)
		{
			if(Minecraft.getMinecraft().theWorld != null)
			{
				if(Minecraft.getMinecraft().gameSettings.thirdPersonView == ShoulderSettings.getShoulderSurfing3ppId())
				{
					double playerReach = 1D;
					
					if(ShoulderSettings.USE_CUSTOM_RAYTRACE_DISTANCE)
					{
						playerReach = ShoulderSettings.RAYTRACE_DISTANCE;
					}
					else
					{
						playerReach = (double) Minecraft.getMinecraft().playerController.getBlockReachDistance();
					}
					
					// block collision
					MovingObjectPosition omo = Minecraft.getMinecraft().renderViewEntity.rayTrace(playerReach, tick);
					double blockDist = 0;
					
					if(omo != null)
					{
						ShoulderRenderBin.rayTraceHit = omo.hitVec;
						blockDist = omo.hitVec.distanceTo(Vec3.createVectorHelper(Minecraft.getMinecraft().renderViewEntity.posX, Minecraft.getMinecraft().renderViewEntity.posY, Minecraft.getMinecraft().renderViewEntity.posZ));
						
//						System.out.println("block dist: " + blockDist);
						ShoulderRenderBin.rayTraceInReach = blockDist <= (double) Minecraft.getMinecraft().playerController.getBlockReachDistance();
					}
					else
					{
						ShoulderRenderBin.rayTraceHit = null;
					}
					
					// entity collision
					Vec3 renderViewPos = Minecraft.getMinecraft().renderViewEntity.getPosition(tick);
					Vec3 sightVector = Minecraft.getMinecraft().renderViewEntity.getLook(tick);
					Vec3 sightRay = renderViewPos.addVector(sightVector.xCoord * playerReach - 5, sightVector.yCoord * playerReach, sightVector.zCoord * playerReach);
					
//					System.out.println(sightVector);
//					System.out.println(renderViewPos + " " + sightVector + " " + sightRay);
					
					List entityList = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABBExcludingEntity(Minecraft.getMinecraft().renderViewEntity, Minecraft.getMinecraft().renderViewEntity.boundingBox.expand(sightVector.xCoord * playerReach, sightVector.yCoord * playerReach, sightVector.zCoord * playerReach).expand(1.0D, 1.0D, 1.0D));
					
					for(int i = 0; i < entityList.size(); ++i)
					{
						Entity ent = (Entity) entityList.get(i);
						
						if(ent.canBeCollidedWith())
						{
							float collisionSize = ent.getCollisionBorderSize();
							
							AxisAlignedBB aabb = ent.boundingBox.expand((double) collisionSize, (double) collisionSize, (double) collisionSize);
							MovingObjectPosition potentialIntercept = aabb.calculateIntercept(renderViewPos, sightRay);
							
							if(potentialIntercept != null)
							{
								double entityDist = potentialIntercept.hitVec.distanceTo(Vec3.createVectorHelper(Minecraft.getMinecraft().renderViewEntity.posX, Minecraft.getMinecraft().renderViewEntity.posY, Minecraft.getMinecraft().renderViewEntity.posZ));
								
								if(entityDist < blockDist)
								{
									ShoulderRenderBin.rayTraceHit = potentialIntercept.hitVec;
									
									// System.out.println("entity dist: " +
									// entityDist);
									ShoulderRenderBin.rayTraceInReach = entityDist <= (double) Minecraft.getMinecraft().playerController.getBlockReachDistance();
								}
							}
						}
					}
				}
			}
		}
	}
}
