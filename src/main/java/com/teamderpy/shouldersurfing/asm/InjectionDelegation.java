package com.teamderpy.shouldersurfing.asm;

import com.teamderpy.shouldersurfing.ShoulderCamera;
import com.teamderpy.shouldersurfing.ShoulderSettings;
import com.teamderpy.shouldersurfing.math.VectorConverter;
import com.teamderpy.shouldersurfing.renderer.ShoulderRenderBin;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	 * Called by injected code to modify the camera rotation yaw
	 */
	public static float getShoulderRotationYaw()
	{
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView == ShoulderSettings.getShoulderSurfing3ppId())
		{
			return ShoulderCamera.SHOULDER_ROTATION_YAW;
		}
		
		return 0F;
	}
	
	/**
	 * Called by injected code to modify the camera rotation pitch
	 */
	public static float getShoulderRotationPitch()
	{
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView == ShoulderSettings.getShoulderSurfing3ppId())
		{
			return ShoulderCamera.SHOULDER_ROTATION_PITCH;
		}
		
		return 0F;
	}
	
	/**
	 * Called by injected code to modify the camera zoom
	 */
	public static float getShoulderZoomMod()
	{
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView == ShoulderSettings.getShoulderSurfing3ppId())
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
			ShoulderRenderBin.projectedVector = VectorConverter.project2D((float) (ShoulderRenderBin.rayTraceHit.x), (float) (ShoulderRenderBin.rayTraceHit.y), (float) (ShoulderRenderBin.rayTraceHit.z));
			
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
	public static RayTraceResult getRayTraceResult(World world, Vec3d vec1, Vec3d vec2)
	{
		if(ShoulderSettings.IGNORE_BLOCKS_WITHOUT_COLLISION)
		{
			return world.rayTraceBlocks(vec1, vec2, false, true, false);
		}
		
		return world.rayTraceBlocks(vec1, vec2);
	}
	
	/**
	 * Called by injected code to get the maximum value for third person
	 */
	public static int getMax3ppId()
	{
		if(ShoulderSettings.REPLACE_DEFAULT_3PP)
		{
			return 2;
		}
		
		return 3;
	}
	
	private static double CAMERA_DISTANCE = 0F;
	
	/**
	 * Called by injected code to get the maximum possible distance for the camera
	 */
	public static double checkDistance(double distance, float yaw, double posX, double posY, double posZ, double cameraXoffset, double cameraYoffset, double cameraZoffset)
	{
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView == ShoulderSettings.getShoulderSurfing3ppId())
		{
			double result = distance;
			float radiant = (float) (Math.PI / 180F);
			float offset = InjectionDelegation.getShoulderRotationYaw();
			float newYaw = yaw - offset;
			
			double length = MathHelper.cos((-90.0F - offset) * radiant) * distance;
			double addX = MathHelper.cos(newYaw * radiant) * length;
			double addZ = MathHelper.sin(newYaw * radiant) * length;
			
			for(int i = 0; i < 8; i++)
			{
				float offsetX = (float)((i & 1) * 2 - 1);
				float offsetY = (float)((i >> 1 & 1) * 2 - 1);
				float offsetZ = (float)((i >> 2 & 1) * 2 - 1);
				
				offsetX = offsetX * 0.1F;
				offsetY = offsetY * 0.1F;
				offsetZ = offsetZ * 0.1F;
				
				RayTraceResult raytraceresult = getRayTraceResult(Minecraft.getMinecraft().world, new Vec3d(posX + offsetX, posY + offsetY, posZ + offsetZ), new Vec3d(posX - (cameraXoffset + addX) + offsetX + offsetZ, posY - cameraYoffset + offsetY, posZ - (cameraZoffset + addZ) + offsetZ));					
				
				if(raytraceresult != null)
				{
					double newDistance = raytraceresult.hitVec.distanceTo(new Vec3d(posX, posY, posZ));
					
					if(newDistance < result)
					{
						result = newDistance;
					}
				}
			}
			
			return CAMERA_DISTANCE = result;
		}
		
		return CAMERA_DISTANCE = distance;
	}
	
	public static Vec3d getPositionEyes(Entity entity, Vec3d positionEyes)
	{
		if(!ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED)
		{
			float radiant = (float) (Math.PI / 180F);
			
			double length = MathHelper.cos((90F - InjectionDelegation.getShoulderRotationYaw()) * radiant) * CAMERA_DISTANCE;
			double addX = MathHelper.cos(entity.rotationYaw * radiant) * length;
			double addZ = MathHelper.sin(entity.rotationYaw * radiant) * length;
			
			return positionEyes.add(new Vec3d(addX, 0, addZ));
		}
		
		return positionEyes;
	}
}
