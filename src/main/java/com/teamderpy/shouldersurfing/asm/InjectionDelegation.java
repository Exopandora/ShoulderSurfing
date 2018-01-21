package com.teamderpy.shouldersurfing.asm;

import com.teamderpy.shouldersurfing.ShoulderCamera;
import com.teamderpy.shouldersurfing.ShoulderSettings;
import com.teamderpy.shouldersurfing.math.VectorConverter;
import com.teamderpy.shouldersurfing.renderer.ShoulderRenderBin;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * @author Joshua Powers <jsh.powers@yahoo.com>
 * @version 1.0
 * @since 2013-01-14
 * 
 *        Injected code is delegated here
 */
@SideOnly(Side.CLIENT)
public final class InjectionDelegation
{
	/**
	 * Called by injected code to modify the camera rotation
	 */
	public static float getShoulderRotation()
	{
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 3)
		{
			return ShoulderCamera.SHOULDER_ROTATION;
		}
		
		return 0F;
	}
	
	/**
	 * Called by injected code to modify the camera zoom
	 */
	public static float getShoulderZoomMod()
	{
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 3)
		{
			return ShoulderCamera.SHOULDER_ZOOM_MOD;
		}
		
		return 1.0F;
	}
	
	/**
	 * Called by injected code to project a raytrace hit to the screen
	 */
	public static void calculateRayTraceProjection()
	{
		if(ShoulderRenderBin.rayTraceHit != null)
		{
			ShoulderRenderBin.projectedVector = VectorConverter.project2D((float) (ShoulderRenderBin.rayTraceHit.xCoord), (float) (ShoulderRenderBin.rayTraceHit.yCoord), (float) (ShoulderRenderBin.rayTraceHit.zCoord));
			
			ShoulderRenderBin.rayTraceHit = null;
		}
	}
	
	/**
	 * Called by injected code to determine whether the camera is too close to
	 * the player
	 */
	public static void verifyReverseBlockDist(double distance)
	{
		if(distance < 0.80 && ShoulderSettings.HIDE_PLAYER_IF_TOO_CLOSE_TO_CAMERA)
		{
			ShoulderRenderBin.skipPlayerRender = true;
		}
	}
	
	/**
	 * Called by injected code to perform the ray trace
	 */
	public static MovingObjectPosition getRayTraceResult(World world, Vec3 vec1, Vec3 vec2)
	{
		if(ShoulderSettings.IGNORE_BLOCKS_WITHOUT_COLLISION)
		{
			return world.func_147447_a(vec1, vec2, false, true, false);
		}
		
		return world.rayTraceBlocks(vec1, vec2);
	}
	
	public static double checkDistance(double distance, float yaw, double posX, double posY, double posZ, double cameraXoffset, double cameraYoffset, double cameraZoffset)
	{
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 3)
		{
			double result = distance;
			float radiant = (float) (Math.PI / 180F);
			float offset = InjectionDelegation.getShoulderRotation();
			float oldYaw = yaw - offset;
			
			double length = MathHelper.cos((-90.0F - offset) * radiant) * distance;
			double addX = MathHelper.cos(oldYaw * radiant) * length;
			double addZ = MathHelper.sin(oldYaw * radiant) * length;
			
			for(int i = 0; i < 8; i++)
			{
				float offsetX = (float)((i & 1) * 2 - 1);
				float offsetY = (float)((i >> 1 & 1) * 2 - 1);
				float offsetZ = (float)((i >> 2 & 1) * 2 - 1);
				
				offsetX = offsetX * 0.1F;
				offsetY = offsetY * 0.1F;
				offsetZ = offsetZ * 0.1F;
				
				MovingObjectPosition raytraceresult = getRayTraceResult(Minecraft.getMinecraft().theWorld, Vec3.createVectorHelper(posX + offsetX, posY + offsetY, posZ + offsetZ), Vec3.createVectorHelper(posX - (cameraXoffset + addX) + offsetX + offsetZ, posY - cameraYoffset + offsetY, posZ - (cameraZoffset + addZ) + offsetZ));					
				
				if(raytraceresult != null)
				{
					double newDistance = raytraceresult.hitVec.distanceTo(Vec3.createVectorHelper(posX, posY, posZ));
					
					if(newDistance < result)
					{
						result = newDistance;
					}
				}
			}
			
			return result;
		}
		
		return distance;
	}
}
