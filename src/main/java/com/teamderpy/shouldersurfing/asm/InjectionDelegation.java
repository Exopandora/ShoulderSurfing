package com.teamderpy.shouldersurfing.asm;

import com.teamderpy.shouldersurfing.ShoulderCamera;
import com.teamderpy.shouldersurfing.ShoulderSettings;
import com.teamderpy.shouldersurfing.math.VectorConverter;
import com.teamderpy.shouldersurfing.renderer.ShoulderRenderBin;

import net.minecraft.client.Minecraft;
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
			ShoulderRenderBin.projectedVector = VectorConverter.project2D((float) (ShoulderRenderBin.rayTraceHit.x), (float) (ShoulderRenderBin.rayTraceHit.y), (float) (ShoulderRenderBin.rayTraceHit.z));
			
			ShoulderRenderBin.rayTraceHit = null;
		}
	}
	
	/**
	 * Holds the last coordinate drawing position
	 */
	private static float lastX = 0.0F;
	private static float lastY = 0.0F;

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
}
