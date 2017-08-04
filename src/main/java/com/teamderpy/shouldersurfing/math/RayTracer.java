package com.teamderpy.shouldersurfing.math;

import java.util.List;

import com.teamderpy.shouldersurfing.ShoulderSettings;
import com.teamderpy.shouldersurfing.renderer.ShoulderRenderBin;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		
		if(Minecraft.getMinecraft().getRenderViewEntity() != null)
		{
			if(Minecraft.getMinecraft().world != null)
			{
				if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 1)
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
					RayTraceResult omo = Minecraft.getMinecraft().getRenderViewEntity().rayTrace(playerReach, tick);
					double blockDist = 0;
					
					if(omo != null)
					{
						ShoulderRenderBin.rayTraceHit = omo.hitVec;
						blockDist = omo.hitVec.distanceTo(new Vec3d(
								Minecraft.getMinecraft().getRenderViewEntity().posX,
								Minecraft.getMinecraft().getRenderViewEntity().posY,
								Minecraft.getMinecraft().getRenderViewEntity().posZ));

						
//						System.out.println("block dist: " + blockDist);
						ShoulderRenderBin.rayTraceInReach = blockDist <= (double) Minecraft.getMinecraft().playerController.getBlockReachDistance();
					}
					else
					{
						ShoulderRenderBin.rayTraceHit = null;
					}
					
					// entity collision
					Vec3d renderViewPos = Minecraft.getMinecraft().getRenderViewEntity().getPositionEyes(tick);
					
					Vec3d sightVector = Minecraft.getMinecraft().getRenderViewEntity().getLook(tick);
					Vec3d sightRay = renderViewPos.addVector(sightVector.x * playerReach - 5, sightVector.y * playerReach, sightVector.z * playerReach);
					
//					System.out.println(sightVector);
					
//					System.out.println(renderViewPos + " " + sightVector + " " + sightRay);
					
					List entityList = Minecraft.getMinecraft().world.getEntitiesWithinAABBExcludingEntity(Minecraft.getMinecraft().getRenderViewEntity(), Minecraft.getMinecraft().getRenderViewEntity().getEntityBoundingBox().expand(sightVector.x * playerReach, sightVector.y * playerReach, sightVector.z * playerReach).expand(1.0D, 1.0D, 1.0D));
					
					for(int i = 0; i < entityList.size(); ++i)
					{
						Entity ent = (Entity) entityList.get(i);
						
						if(ent.canBeCollidedWith())
						{
							float collisionSize = ent.getCollisionBorderSize();
							
							AxisAlignedBB aabb = ent.getEntityBoundingBox().expand((double) collisionSize, (double) collisionSize, (double) collisionSize);
							RayTraceResult potentialIntercept = aabb.calculateIntercept(renderViewPos, sightRay);
							
							if(potentialIntercept != null)
							{
								double entityDist = potentialIntercept.hitVec.distanceTo(new Vec3d(Minecraft.getMinecraft().getRenderViewEntity().posX, Minecraft.getMinecraft().getRenderViewEntity().posY, Minecraft.getMinecraft().getRenderViewEntity().posZ));
								
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
