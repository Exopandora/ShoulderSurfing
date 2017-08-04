package com.teamderpy.shouldersurfing.asm;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.teamderpy.shouldersurfing.ShoulderCamera;
import com.teamderpy.shouldersurfing.ShoulderSettings;
import com.teamderpy.shouldersurfing.math.VectorConverter;
import com.teamderpy.shouldersurfing.renderer.ShoulderRenderBin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
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
	private InjectionDelegation()
	{
		
	}
	
	/**
	 * Called by injected code to modify the camera rotation
	 */
	public static float getShoulderRotation()
	{
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 1)
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
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 1)
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
}
